package aks;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class MailTest {
    public JavaMailSender mailSender() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.starttls.enable", true);
        javaMailProperties.put("mail.smtp.host", "smtp.gmail.com");
        javaMailProperties.put("mail.smtp.port", 587);

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setJavaMailProperties(javaMailProperties);
        sender.setUsername("haibatest@gmail.com");
        sender.setPassword("Papkasse1");

        return sender;
    }

    public static void main(String[] args) {
        MailTest mailTest = new MailTest();
        mailTest.send();
    }

    private void send() {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                messageHelper.setTo("aks@trifork.com");
                messageHelper.setFrom("haibatest@gmail.com");
                messageHelper.setSubject("Haiba-test");
                messageHelper.setText("hest", true);
            }
        };
        mailSender().send(preparator);
    }
}
