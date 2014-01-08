package dk.nsi.haiba.epimibaimporter.email;

import java.util.Properties;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class EmailSender {
    @Autowired
    private JavaMailSender javaMailSender;

    public void send(final Set<String> unknownBanrSet, final Set<String> unknownAlnrSet) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setTo("aks@trifork.com");
                messageHelper.setFrom("haibatest@gmail.com");
                messageHelper.setSubject("EPIMIBA: Notification on unknown table entries");
                String html = "<h2>After the recent import, the following unknown table entries are discovered:</h2>";
                if (!unknownAlnrSet.isEmpty()) {
                    html += "<h2>alnr:</h2><br><ul>";
                    for (String alnr : unknownAlnrSet) {
                        html += "<li>" + alnr + "</li>";
                    }
                    html += "</ul>";
                }
                if (!unknownBanrSet.isEmpty()) {
                    if (!html.isEmpty()) {
                        html += "<br>";
                    }
                    html += "<h2>banr:</h2><br><ul>";
                    for (String banr : unknownBanrSet) {
                        html += "<li>" + banr + "</li>";
                    }
                    html += "</ul>";
                }
                messageHelper.setText(html, true);
            }
        };
        javaMailSender.send(preparator);
    }
}
