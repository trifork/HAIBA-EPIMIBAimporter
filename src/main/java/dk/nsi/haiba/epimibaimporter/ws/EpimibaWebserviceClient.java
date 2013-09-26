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

import dk.nsi.haiba.epimibaimporter.log.Log;
import dk.nsi.stamdata.jaxws.generated.Answer;
import dk.nsi.stamdata.jaxws.generated.ArrayOfAnswer;
import dk.nsi.stamdata.jaxws.generated.PResult;
import dk.nsi.stamdata.jaxws.generated.WebService;
import dk.nsi.stamdata.jaxws.generated.WebServiceSoap;

public class EpimibaWebserviceClient {
	
	private static Log log = new Log(Logger.getLogger(EpimibaWebserviceClient.class));

	public List<Answer> getBakteriaemi(long latestTransactionId) {
		return getAnswers(latestTransactionId + 1, 86);
	}
	
	public List<Answer> getClostridium(long latestTransactionId) {
		// TODO find ID for clostridium
		return getAnswers(latestTransactionId + 1, 0);
	}
	
    List<Answer> getAnswers(long latestTransactionId, int caseDefinitionId) {
    	
    	log.debug("Calling getanswers");
    	List<Answer> answerList = new ArrayList<Answer>();
    	
    	try {
        	URL wsdlLocation = new URL("http://epimibapsrv:8081/WebService.asmx?wsdl");
            QName serviceName = new QName("http://www.ssi.dk/", "WebService");
            WebService service = new WebService(wsdlLocation, serviceName);
        	
            // TODO - make the webservice to change this...	
            // Hack - to convert long to int as the webservice supports
            String transactionId = ""+latestTransactionId;
            int tid = new Integer(transactionId).intValue();
            
            WebServiceSoap wsClient = service.getWebServiceSoap();
            PResult answers = wsClient.getAnswers("HaibaImporter", "Test1234", caseDefinitionId, tid, 100);
            ArrayOfAnswer answers2 = answers.getAnswers();
            answerList = answers2.getAnswer();
    	} catch(Exception e)  {
    		log.error("", e);
    		throw new EpimibaWebserviceException(e.getMessage(), e);
    	}
    	log.debug("called getanswers");
    	
    	return answerList;
    }
    


}
