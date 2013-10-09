/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.haiba.epimibaimporter.importer;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import dk.nsi.haiba.epimibaimporter.dao.HAIBADAO;
import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.haiba.epimibaimporter.model.Header;
import dk.nsi.haiba.epimibaimporter.model.Isolate;
import dk.nsi.haiba.epimibaimporter.model.Quantitative;
import dk.nsi.haiba.epimibaimporter.status.ImportStatusRepository;
import dk.nsi.haiba.epimibaimporter.ws.EpimibaWebserviceClient;
import dk.nsi.stamdata.jaxws.generated.Answer;
import dk.nsi.stamdata.jaxws.generated.ArrayOfPIsolate;
import dk.nsi.stamdata.jaxws.generated.ArrayOfPQuantitative;
import dk.nsi.stamdata.jaxws.generated.PIsolate;
import dk.nsi.stamdata.jaxws.generated.PQuantitative;

/*
 * Scheduled job, responsible for fetching new data from LPR, then send it to the RulesEngine for further processing
 */
public class ImportExecutor {
	
	private static Log log = new Log(Logger.getLogger(ImportExecutor.class));

	private boolean manualOverride;
	
	@Autowired
	HAIBADAO haibaDao;

	@Autowired
	ImportStatusRepository statusRepo;
	
	@Autowired
	EpimibaWebserviceClient epimibaWebserviceClient;

	@Scheduled(cron = "${cron.import.job}")
	public void run() {
		if(!isManualOverride()) {
			log.debug("Running Importer: " + new Date().toString());
			doProcess();
		} else {
			log.debug("Importer must be started manually");
		}
	}

	/*
	 * Separated into its own method for testing purpose, because testing a scheduled method isn't good
	 */
	public void doProcess() {
		// Fetch new records from LPR contact table
		try {
			statusRepo.importStartedAt(new DateTime());
			
			boolean hasAnswers = true;

			// save bakteriaemi
			while(hasAnswers) {
	        	long latestTransactionId = haibaDao.getLatestTransactionId(HAIBADAO.BAKTERIAEMI_TRANSACTIONTYPE);
	        	List<Answer> answers = epimibaWebserviceClient.getBakteriaemi(latestTransactionId);
	        	if(answers == null || answers.size() == 0) {
	        		log.debug("No more answers on Bakteriaemi");
	        		hasAnswers = false;
	        	} else {
			        for (Answer answer : answers) {
			        	log.debug(answer.getCprnr());
			        	Header header = getHeader(answer);
			        	haibaDao.saveBakteriaemi(header, answer.getTransactionID().longValue());
					}
	        	}
			}
			
			// save clostridium difficile
			hasAnswers = true;
			while(hasAnswers) {
	        	long latestTransactionId = haibaDao.getLatestTransactionId(HAIBADAO.CLOSTRIDIUM_TRANSACTIONTYPE);
	        	List<Answer> answers = epimibaWebserviceClient.getClostridium(latestTransactionId);
	        	if(answers == null || answers.size() == 0) {
	        		log.debug("No more answers on Clostridium difficile");
	        		hasAnswers = false;
	        	} else {
			        for (Answer answer : answers) {
			        	log.debug(answer.getCprnr());
			        	Header header = getHeader(answer);
			        	haibaDao.saveClostridiumDifficile(header, answer.getTransactionID().longValue());
					}
	        	}
			}

			haibaDao.clearAnalysisTable();
			haibaDao.saveAnalysis(epimibaWebserviceClient.getClassifications("Analysis"));

			haibaDao.clearInvestigationTable();
			haibaDao.saveInvestigation(epimibaWebserviceClient.getClassifications("Investigation"));
			
			haibaDao.clearLabSectionTable();
			haibaDao.saveLabSection(epimibaWebserviceClient.getClassifications("LabSection"));
			
			haibaDao.clearLocationTable();
			haibaDao.saveLocation(epimibaWebserviceClient.getClassifications("Locations"));
			
			haibaDao.clearOrganizationTable();
			haibaDao.saveOrganization(epimibaWebserviceClient.getClassifications("Organization"));
			
			haibaDao.clearMicroorganismTable();
			haibaDao.saveMicroorganism(epimibaWebserviceClient.getClassifications("Microorganism"));
	        
			statusRepo.importEndedWithSuccess(new DateTime());
			
		} catch(Exception e) {
			log.error("", e);
			statusRepo.importEndedWithFailure(new DateTime(), e.getMessage());
		}
	}
	
	private Header getHeader(Answer answer) {
		
		Header h = new Header();
		h.setHeaderId(answer.getHeaderId());
		// h.setAlnr() - TODO not found in answer
		h.setAvd(answer.getAvd());
		h.setCprnr(answer.getCprnr().replace("-", ""));
		h.setEvaluationText(answer.getEvaluationText());
		h.setExtid(answer.getExtId());
		if(answer.getIndate() != null) {
			h.setInDate(answer.getIndate().toGregorianCalendar().getTime());
		}
		h.setLabnr(""+answer.getLabnr());
		h.setLar(""+answer.getLar());
		h.setMgkod(answer.getMgkod());
		h.setPname(answer.getPname());
		if(answer.getPrdate() != null) {
			h.setPrDate(answer.getPrdate().toGregorianCalendar().getTime());
		}
    	h.setRefnr(answer.getRefnr());
		h.setResult(answer.getResult());
		// h.setStnr()  - TODO not found in answer
		h.setUsnr(answer.getUsnr());
		
		ArrayOfPIsolate isolates = answer.getIsolates();
		List<PIsolate> pIsolate = isolates.getPIsolate();
		log.debug("Adding "+pIsolate.size()+" isolates");
		for (PIsolate isolate : pIsolate) {
			Isolate i = new Isolate();
			i.setIsolateId(isolate.getIsolateId());
			i.setQuantity(isolate.getIsolateQuantity());
			log.debug("Adding isolate: " + isolate.getIsolateId());
			h.addIsolate(i);
		}
		
		ArrayOfPQuantitative quantitatives = answer.getQuantitatives();
		List<PQuantitative> pQuantitative = quantitatives.getPQuantitative();
		log.debug("Adding "+pQuantitative.size()+" quantitatives");
		for (PQuantitative quantitative : pQuantitative) {
			Quantitative q = new Quantitative();
			q.setQuantitativeId(quantitative.getQuantitativeId());
			q.setAnalysis(quantitative.getQuantitativeAnalysis());
			q.setComment(quantitative.getQuantitativeComment());
			q.setEvaluationText(quantitative.getQuantitativeEvaluationText());
			q.setQtnr("" + quantitative.getQuantitativeQtnr()); // TODO shouldn't this be an int?
			q.setQuantity(quantitative.getQuantitativeQuantity());
			log.debug("Adding quantitative: " + quantitative.getQuantitativeId());
			h.addQuantitative(q);
		}
		
		return h;
	}

	public boolean isManualOverride() {
		return manualOverride;
	}

	public void setManualOverride(boolean manualOverride) {
		this.manualOverride = manualOverride;
	}
	
}
