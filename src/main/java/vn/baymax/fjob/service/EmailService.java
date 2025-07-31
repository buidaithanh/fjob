package vn.baymax.fjob.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final MailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, MailSender mailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public String sendEmail() {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo("thanh.11db@gmail.com");
        smm.setSubject("test email");
        smm.setText("Hello world");
        mailSender.send(smm);
        return "ok";
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // when use async, will run 2 thread so thread 2th not recive data bc of use
    // lazy fetch
    @Async
    public void sendEmailFromTemplateSync(String to,
            String subject,
            String templateName,
            String username,
            Object value) {
        Context context = new Context();
        context.setVariable("name", username);
        context.setVariable("jobs", value);
        String content = templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);

    }
}
