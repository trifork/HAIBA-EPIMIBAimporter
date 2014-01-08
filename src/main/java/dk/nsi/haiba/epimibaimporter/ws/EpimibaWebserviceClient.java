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
package dk.nsi.haiba.epimibaimporter.ws;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.haiba.epimibaimporter.model.Classification;
import dk.nsi.stamdata.jaxws.generated.Answer;
import dk.nsi.stamdata.jaxws.generated.ArrayOfAnswer;
import dk.nsi.stamdata.jaxws.generated.ArrayOfGenericCoding;
import dk.nsi.stamdata.jaxws.generated.GenericCoding;
import dk.nsi.stamdata.jaxws.generated.GenericCodingResult;
import dk.nsi.stamdata.jaxws.generated.PResult;
import dk.nsi.stamdata.jaxws.generated.WebService;
import dk.nsi.stamdata.jaxws.generated.WebServiceSoap;

public class EpimibaWebserviceClient {
	
	@Value("${epimiba.webservice.wsdl.url}")
	String wsdlURL;

	@Value("${epimiba.webservice.username}")
	String username;

	@Value("${epimiba.webservice.password}")
	String password;

	@Value("${epimiba.casedefinition.batchsize}")
	int batchSize;

	private static Log log = new Log(Logger.getLogger(EpimibaWebserviceClient.class));

    public List<Answer> getAnswers(long latestTransactionId, int caseDefinitionId) {
    	
    	log.debug("Calling getanswers");
    	List<Answer> answerList = new ArrayList<Answer>();
    	
    	try {
            // TODO - make the webservice to change this...	
            // Hack - to convert long to int as the webservice supports
            String transactionId = ""+latestTransactionId;
            int tid = new Integer(transactionId).intValue();
            
            WebServiceSoap wsClient = createWebserviceClient();
            PResult answers = wsClient.getAnswers(username, password, caseDefinitionId, tid, batchSize);
            ArrayOfAnswer answers2 = answers.getAnswers();
            answerList = answers2.getAnswer();
    	} catch(Exception e)  {
    		log.error("", e);
    		throw new EpimibaWebserviceException(e.getMessage(), e);
    	}
    	log.debug("called getanswers");
    	
    	return answerList;
    }

	private WebServiceSoap createWebserviceClient() throws Exception {
		
		URL wsdlLocation = new URL(wsdlURL);
		QName serviceName = new QName("http://www.ssi.dk/", "WebService");
		WebService ws = new WebService(wsdlLocation, serviceName);
        WebServiceSoap wsClient = ws.getWebServiceSoap();
        return wsClient;
	}
    
    public List<Classification> getClassifications(String classificationType) {
    	
    	log.debug("Calling getClassifications with type "+classificationType);
    	List<Classification> list = new ArrayList<Classification>();
    	
    	try {
            List<GenericCoding> genericCoding = getCodings(classificationType);
            
            for (GenericCoding gc : genericCoding) {
            	Classification c = new Classification();
            	c.setId(gc.getId());
				c.setCode(gc.getCode());
				c.setText(gc.getText());
				list.add(c);
			}
            
    	} catch(Exception e)  {
    		log.error("", e);
    		throw new EpimibaWebserviceException(e.getMessage(), e);
    	}
    	log.debug("called getClassifications");
    	
    	return list;
    }

    List<GenericCoding> getCodings(String codingsType) throws Exception {
		
		WebServiceSoap wsClient = createWebserviceClient();
		GenericCodingResult codings = wsClient.getCodings(username, password, codingsType);
		ArrayOfGenericCoding codes = codings.getCodes();
		List<GenericCoding> genericCoding = codes.getGenericCoding();
		return genericCoding;
	}
    


}
