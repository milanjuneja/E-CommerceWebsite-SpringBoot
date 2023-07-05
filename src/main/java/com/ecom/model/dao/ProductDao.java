package com.ecom.model.dao;

import com.ecom.model.LocalUser;
import com.ecom.model.Orders;
import com.ecom.model.Product;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ProductDao extends ListCrudRepository<Product, Long> {



}
