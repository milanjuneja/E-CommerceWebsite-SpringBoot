package com.ecom.model.dao;

import com.ecom.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalUserDao extends JpaRepository<LocalUser, Long> {
}
