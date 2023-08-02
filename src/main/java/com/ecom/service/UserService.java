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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
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

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = this.localUserDao.findByUserNameIgnoreCase(loginBody.getUserName());

        if(opUser.isPresent()){

            LocalUser user = opUser.get();
            if(this.encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                }else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();

                    boolean resend = verificationTokens.size() == 0 ||
                            verificationTokens.get(0).getCreatedTimeStamp()
                                    .before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));

                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDao.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }

                    throw new UserNotVerifiedException(resend);
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


    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenDao.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserDao.save(user);
                //verificationTokenDao.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDao.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        }
        else {
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body){
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> opUser = localUserDao.findByEmailIgnoreCase(email);

        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserDao.save(user);
        }
        else {

        }
    }

}
