package com.ecom.service;

import com.ecom.exception.EmailFailureException;
import com.ecom.model.LocalUser;
import com.ecom.model.VerificationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;
    @Value("${app.frontend.url}")
    private String url;
    @Autowired
    private JavaMailSender javaMailSender;

    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }


    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();

        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify to activate account");
        message.setText("Please follow the link below to verify your email to activate the account.\n" +
                url + "/auth/verify?token=" + verificationToken.getToken());

        try {
            javaMailSender.send(message);
        }catch (MailException ex){
            throw new EmailFailureException();
        }

    }

    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your password reset request link.");
        message.setText("You requested a password reset on our website. Please " +
                "find the link below to reset your password.\n" +
                url + "/auth/reset?token=" + token);

        try {
            javaMailSender.send(message);
        }catch (MailException ex){
            throw new EmailFailureException();
        }

    }

}
