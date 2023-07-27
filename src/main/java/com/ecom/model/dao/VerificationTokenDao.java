package com.ecom.model.dao;

import com.ecom.model.LocalUser;
import com.ecom.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDao extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);


    List<VerificationToken> findByUser_UserIdOrderByIdDesc(Long id);
}
