package com.auth.email;

import com.auth.config.AuthProperties;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {
    private final String activationEmail = """
                            <html>
                            <body>
                                <h1>${title}</h1>
                                <a href="${url}">${clickHere}</a>
                            </body>
                        </html>
            """;

    private JavaMailSenderImpl mailSender;
    private AuthProperties authProperties;
    private MessageSource messageSource;

    @Autowired
    public EmailService(AuthProperties authProperties, MessageSource messageSource) {
        this.authProperties = authProperties;
        this.messageSource = messageSource;
    }


    @PostConstruct
    public void initialize() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(authProperties.getEmail().host());
        mailSender.setPort(authProperties.getEmail().port());

        mailSender.setUsername(authProperties.getEmail().username());
        mailSender.setPassword(authProperties.getEmail().password());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
    }

    public void sendActivationEmail(String email, String activationToken) {
        var activationUrl = authProperties.getClient().host() + activationToken+"/active";
        var title = messageSource.getMessage("auth.mail.user.created.title", null, LocaleContextHolder.getLocale());
        var clickHere = messageSource.getMessage("auth.mail.click.here", null, LocaleContextHolder.getLocale());

        var mailBody = activationEmail
                .replace("${url}", activationUrl)
                .replace("${title}", title)
                .replace("${clickHere}", clickHere);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            message.setFrom(authProperties.getEmail().from());
            message.setTo(email);
            message.setSubject(title);
            message.setText(mailBody, true);
        } catch (MessagingException exception) {
            exception.printStackTrace();
        }

        this.mailSender.send(mimeMessage);
    }
}


