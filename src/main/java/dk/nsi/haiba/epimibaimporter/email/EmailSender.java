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
package dk.nsi.haiba.epimibaimporter.email;

import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import dk.nsi.haiba.epimibaimporter.log.Log;

public class EmailSender {
    private static Log log = new Log(Logger.getLogger(EmailSender.class));

    @Value("${smtp.from}")
    private String from;
    @Value("${smtp.to_commaseparated}")
    private String to_commaseparated;

    @Autowired
    private JavaMailSender javaMailSender;

    public void send(final Collection<String> unknownBanrSet, final Collection<String> unknownAlnrSet) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setValidateAddresses(true);

                String[] split = to_commaseparated.split(",");
                for (String emailAddress : split) {
                    emailAddress = emailAddress.trim();
                    try {
                        log.debug("adding " + emailAddress);
                        messageHelper.addTo(emailAddress);
                        log.debug("added " + emailAddress);
                    } catch (MessagingException e) {
                        log.error("unable to parse email address from " + emailAddress, e);
                    }
                }
                messageHelper.setFrom(from);
                messageHelper.setSubject("EPIMIBA: Notification on unknown table entries");
                String not_html = "After the recent import, the following unknown table entries are discovered:\n";
                if (!unknownAlnrSet.isEmpty()) {
                    not_html += "-----\n";
                    not_html += "alnr:\n";
                    String delim = "";
                    for (String alnr : unknownAlnrSet) {
                        not_html += delim + alnr;
                        delim = ", ";
                    }
                    not_html+="\n";
                }
                if (!unknownBanrSet.isEmpty()) {
                    not_html += "-----\n";
                    not_html += "banr:\n";
                    String delim = "";
                    for (String banr : unknownBanrSet) {
                        not_html += delim + banr;
                        delim = ", ";
                    }
                    not_html+="\n";
                }
                messageHelper.setText(not_html, false);
            }
        };
        javaMailSender.send(preparator);
    }

    public String getTo() {
        return to_commaseparated;
    }
}
