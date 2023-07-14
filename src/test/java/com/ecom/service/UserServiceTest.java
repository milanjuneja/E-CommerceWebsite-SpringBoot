package com.ecom.service;

import com.ecom.api.model.LoginBody;
import com.ecom.api.model.RegistrationBody;
import com.ecom.exception.EmailFailureException;
import com.ecom.exception.UserAlreadyExistsException;
import com.ecom.exception.UserNotVerifiedException;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class UserServiceTest {



//    @RegisterExtension
//    private static GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP)
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"));
//

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody registrationBody = new RegistrationBody();
        registrationBody.setUserName("UserA");
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setFirstName("FirstName");
        registrationBody.setLastName("LastName");
        registrationBody.setPassword("MySecretPassword123");

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody), "UserName should already in use");


        registrationBody.setUserName("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setEmail("UserA@junit.com");

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registrationBody),
                "Email should already in use");

        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(registrationBody),
                "user should register successfully");


//        Assertions.assertEquals(registrationBody.getEmail(), greenMail.getReceivedMessages()[0]
//                .getRecipients(Message.RecipientType.TO)[0].toString());

    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setUserName("UserA-NotExists");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(body), "User should not exists");

        body.setUserName("UserA");

        Assertions.assertNull(userService.loginUser(body), "Password should be incorrect");

        body.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(body), "User should be login successfully");

        body.setPassword("PasswordB123");
        body.setUserName("UserB");
//        try {
//            userService.loginUser(body);
//            Assertions.assertTrue(false, "User should not have mail verified");
//        }catch (UserNotVerifiedException ex){
//            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent");
////            Assertions.assertEquals(1, greenMail.getReceivedMessages().lenght);
//        }
//
//        try {
//            userService.loginUser(body);
//            Assertions.assertTrue(false, "User should not have mail verified");
//        }catch (UserNotVerifiedException ex){
//            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not  be resent");
////            Assertions.assertEquals(1, greenMail.getReceivedMessages().lenght);
//        }



    }

}