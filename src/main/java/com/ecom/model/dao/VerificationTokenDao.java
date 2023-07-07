package com.ecom.model.dao;

import com.ecom.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

public interface VerificationTokenDao extends ListCrudRepository<VerificationToken, Long> {
}
