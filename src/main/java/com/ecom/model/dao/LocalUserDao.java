package com.ecom.model.dao;

import com.ecom.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface LocalUserDao extends ListCrudRepository<LocalUser, Long> {

    Optional<LocalUser> findByEmailIgnoreCase(String email);

    Optional<LocalUser> findByUserNameIgnoreCase(String name);

}
