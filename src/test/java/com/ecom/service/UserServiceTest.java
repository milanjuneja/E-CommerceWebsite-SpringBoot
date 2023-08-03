package com.ecom.service;

import com.ecom.api.model.LoginBody;
import com.ecom.api.model.PasswordResetBody;
import com.ecom.api.model.RegistrationBody;
import com.ecom.exception.EmailFailureException;
import com.ecom.exception.EmailNotFoundException;
import com.ecom.exception.UserAlreadyExistsException;
import com.ecom.exception.UserNotVerifiedException;
import com.ecom.model.LocalUser;
import com.ecom.model.VerificationToken;
import com.ecom.model.dao.LocalUserDao;
import com.ecom.model.dao.VerificationTokenDao;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {



//    @RegisterExtension
//    private static GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP)
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"));
//

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDao localUserDao;

    @Autowired
    private EncryptionService encryptionService;

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

    @Test
    @Transactional
    public void testVerifyUser(){
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exists");
        LoginBody body = new LoginBody();
        body.setUserName("UserB");
        body.setPassword("PasswordB123");

                try {
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have mail verified");
        }catch (UserNotVerifiedException | EmailFailureException ex){
                    List<VerificationToken> tokens = verificationTokenDao.findByUser_UserIdOrderByIdDesc(2L);
                    String token = tokens.get(0).getToken();
                    Assertions.assertTrue(userService.verifyUser(token), "Token should be valid");
                    Assertions.assertNotNull(body, "User should be verified");
        }

    }

    @Test
    @Transactional
    public void testForgotPassword(){
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> userService.forgotPassword("UserNotExist@junit.com"));

        Assertions.assertDoesNotThrow(()-> userService.forgotPassword("UserA@junit.com"));

//        Assertions.assertEquals("UserA@junit.com", greenMailExtention.getRecievedMessage()[0].getReciepts(Message.RecipientType.TO[0].toString()));



    }

    @Test
    public void testResetPassword(){
        LocalUser user = localUserDao.findByUserNameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);

        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password1234");
        userService.resetPassword(body);
        user = localUserDao.findByUserNameIgnoreCase("UserA").get();

        Assertions.assertTrue(encryptionService.verifyPassword("Password1234",
                user.getPassword()),
                "Password change should be written to DB");


    }

}