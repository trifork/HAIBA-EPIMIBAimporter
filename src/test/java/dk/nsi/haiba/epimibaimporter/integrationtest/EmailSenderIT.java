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
package dk.nsi.haiba.epimibaimporter.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import dk.nsi.haiba.epimibaimporter.email.EmailSender;
import dk.nsi.haiba.epimibaimporter.importer.ImportExecutor;
import dk.nsi.haiba.epimibaimporter.model.Classification;
import dk.nsi.haiba.epimibaimporter.status.CurrentImportProgress;
import dk.nsi.haiba.epimibaimporter.ws.EpimibaWebserviceClient;
import dk.nsi.stamdata.jaxws.generated.Answer;
import dk.nsi.stamdata.jaxws.generated.ArrayOfPComment;
import dk.nsi.stamdata.jaxws.generated.ArrayOfPIsolate;
import dk.nsi.stamdata.jaxws.generated.ArrayOfPQuantitative;
import dk.nsi.stamdata.jaxws.generated.PComment;
import dk.nsi.stamdata.jaxws.generated.PIsolate;
import dk.nsi.stamdata.jaxws.generated.PQuantitative;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional("haibaTransactionManager")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class EmailSenderIT {
    @Configuration
    @PropertySource("classpath:test.properties")
    @Import(EPIMIBAIntegrationTestConfiguration.class)
    static class TestConfiguration {
        @Bean
        public EmailSender emailSender() {
            return Mockito.mock(EmailSender.class);
        }

        @Bean
        public EpimibaWebserviceClient epimibaWebserviceClient() {
            return Mockito.mock(EpimibaWebserviceClient.class);
        }
    }

    @Autowired
    EmailSender emailSender;

    @Autowired
    EpimibaWebserviceClient epimibaWebserviceClient;

    @Autowired
    ImportExecutor importExecutor;

    @Autowired
    @Qualifier("haibaJdbcTemplate")
    JdbcTemplate jdbc;

    @Autowired
    CurrentImportProgress currentImportProgress;

    @Before
    public void init() {
        Logger.getLogger(ImportExecutor.class).setLevel(Level.DEBUG);
        Logger.getLogger(EmailSender.class).setLevel(Level.DEBUG);
        Logger.getLogger(EpimibaWebserviceClient.class).setLevel(Level.DEBUG);
    }

    @Test
    public void simpleMailTest() {
        emailSender.sendHello();
    }

    @Test
    public void testHeaderInserts() {
        Logger.getLogger(ImportExecutor.class).setLevel(Level.ERROR);
        int count = jdbc.queryForInt("SELECT COUNT(*) FROM Header");
        assertEquals("Empty Header", 0, count);
        int answerCount = 100;
        List<Answer> answers = createAnswers(answerCount);

        Mockito.when(epimibaWebserviceClient.getAnswers(1, 119)).thenReturn(answers);
        long time = System.currentTimeMillis();
        importExecutor.doProcess(true);
        long period = System.currentTimeMillis() - time;

        System.out.println("time to insert " + answerCount + " into headers: " + period / 1000d + " seconds");

        Mockito.verify(epimibaWebserviceClient, Mockito.times(1)).getAnswers(1, 119);
        count = jdbc.queryForInt("SELECT COUNT(*) FROM Header");
        assertEquals("Not empty Header", answerCount, count);
        
        String commentText = jdbc.queryForObject("SELECT CommentText FROM Header LIMIT 1", String.class);
        assertNotNull(commentText);
        assertTrue(commentText.contains("///"));
        
        Logger.getLogger(ImportExecutor.class).setLevel(Level.DEBUG);
    }

    private List<Answer> createAnswers(int answerCount) {
        List<Answer> returnValue = new ArrayList<Answer>();
        for (int i = 0; i < answerCount; i++) {
            Answer a = new Answer();
            a.setHeaderId(i);
            a.setLocationAlnr("1");
            a.setCprnr(i + "x");
            a.setTransactionID(new BigInteger("" + 1 + i));

            PIsolate pisolate = new PIsolate();
            pisolate.setIsolateBanr(i % 10 + "");
            PIsolate pisolate2 = new PIsolate();
            pisolate2.setIsolateBanr((10 + (i % 10)) + "");
            ArrayOfPIsolate arrayOfPIsolate = new ArrayOfPIsolate();
            arrayOfPIsolate.getPIsolate().add(pisolate);
            arrayOfPIsolate.getPIsolate().add(pisolate2);
            a.setIsolates(arrayOfPIsolate);

            PQuantitative quantitative = new PQuantitative();
            quantitative.setQuantitativeQuantity("" + i * 2);
            PQuantitative quantitative2 = new PQuantitative();
            quantitative2.setQuantitativeQuantity("" + (i * 2 - 1));
            ArrayOfPQuantitative quantitatives = new ArrayOfPQuantitative();
            quantitatives.getPQuantitative().add(quantitative);
            quantitatives.getPQuantitative().add(quantitative2);
            a.setQuantitatives(quantitatives);
            
            ArrayOfPComment arrayOfPComment = new ArrayOfPComment();
            PComment pComment = new PComment();
            pComment.setCommentText("1");
            arrayOfPComment.getPComment().add(pComment);
            pComment = new PComment();
            pComment.setCommentText("2");
            arrayOfPComment.getPComment().add(pComment);
            a.setComments(arrayOfPComment);

            returnValue.add(a);
        }
        return returnValue;
    }

    @Test
    public void testEmailSendOnNewEntries() {
        Mockito.reset(emailSender);
        int count = jdbc.queryForInt("SELECT COUNT(*) FROM Anvendt_Klass_microorganism");
        assertEquals("Empty Anvendt_Klass_microorganism", 0, count);
        count = jdbc.queryForInt("SELECT COUNT(*) FROM Anvendt_Klass_Location");
        assertEquals("Empty Anvendt_Klass_Location", 0, count);

        List<Answer> answers = new ArrayList<Answer>();
        Answer a = new Answer();
        a.setHeaderId(0);
        a.setLocationAlnr("1");
        a.setCprnr("x");
        PIsolate pisolate = new PIsolate();
        pisolate.setIsolateBanr("2");
        ArrayOfPIsolate arrayOfPIsolate = new ArrayOfPIsolate();
        arrayOfPIsolate.getPIsolate().add(pisolate);
        a.setIsolates(arrayOfPIsolate);
        a.setQuantitatives(new ArrayOfPQuantitative());
        a.setTransactionID(new BigInteger("17"));
        answers.add(a);
        a = new Answer();
        a.setHeaderId(0);
        a.setLocationAlnr("1");
        a.setCprnr("x");
        pisolate = new PIsolate();
        pisolate.setIsolateBanr("3");
        arrayOfPIsolate = new ArrayOfPIsolate();
        arrayOfPIsolate.getPIsolate().add(pisolate);
        a.setIsolates(arrayOfPIsolate);
        a.setQuantitatives(new ArrayOfPQuantitative());
        a.setTransactionID(new BigInteger("18"));
        answers.add(a);
        Mockito.when(epimibaWebserviceClient.getAnswers(1, 119)).thenReturn(answers);
        List<Classification> locations = new ArrayList<Classification>();
        Classification classification = new Classification();
        classification.setCode("1");
        classification.setId(1);
        classification.setText("1");
        locations.add(classification);
        Mockito.when(epimibaWebserviceClient.getClassifications("Locations")).thenReturn(locations);
        List<Classification> microorganisms = new ArrayList<Classification>();
        classification = new Classification();
        classification.setCode("2");
        classification.setId(2);
        classification.setText("2");
        microorganisms.add(classification);
        classification = new Classification();
        classification.setCode("3");
        classification.setId(3);
        classification.setText("3");
        microorganisms.add(classification);
        Mockito.when(epimibaWebserviceClient.getClassifications("Microorganism")).thenReturn(microorganisms);

        importExecutor.doProcess(true);

        // new, so notify
        Set<String> unknownBanrSet = new HashSet<String>(Arrays.asList(new String[] { "2", "3" }));
        Set<String> unknownAlnrSet = new HashSet<String>(Arrays.asList(new String[] { "1" }));
        Mockito.verify(emailSender, Mockito.times(1)).send(unknownBanrSet, unknownAlnrSet);

        count = jdbc.queryForInt("SELECT COUNT(*) FROM Anvendt_Klass_microorganism");
        assertEquals("Not Empty Anvendt_Klass_microorganism", 2, count);
        count = jdbc.queryForInt("SELECT COUNT(*) FROM Anvendt_Klass_Location");
        assertEquals("Not Empty Anvendt_Klass_Location", 1, count);

        System.out.println(currentImportProgress.getStatus());
        importExecutor.doProcess(true);

        // not new, so dont notify (we still have the 1 notification)
        Mockito.verify(emailSender, Mockito.times(1)).send(Mockito.anySet(), Mockito.anySet());
    }
}
