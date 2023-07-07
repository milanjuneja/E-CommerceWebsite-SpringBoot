package com.ecom.service;

import com.ecom.api.model.LoginBody;
import com.ecom.api.model.RegistrationBody;
import com.ecom.exception.EmailFailureException;
import com.ecom.exception.UserAlreadyExistsException;
import com.ecom.exception.UserNotVerifiedException;
import com.ecom.model.LocalUser;
import com.ecom.model.VerificationToken;
import com.ecom.model.dao.LocalUserDao;
import com.ecom.model.dao.VerificationTokenDao;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private LocalUserDao localUserDao;
    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private VerificationTokenDao verificationTokenDao;
    @Autowired
    private EmailService emailService;
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {

        if(this.localUserDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || this.localUserDao.findByUserNameIgnoreCase(registrationBody.getUserName()).isPresent()){
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUserName(registrationBody.getUserName());
        user.setPassword(this.encryptionService.encryptPassword(registrationBody.getPassword()));
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        verificationTokenDao.save(verificationToken);
        return this.localUserDao.save(user);

    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException {
        Optional<LocalUser> opUser = this.localUserDao.findByUserNameIgnoreCase(loginBody.getUserName());

        if(opUser.isPresent()){

            LocalUser user = opUser.get();
            if(this.encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                }else {
                    throw new UserNotVerifiedException();
                }

            }

        }
        return null;
    }


    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

}
