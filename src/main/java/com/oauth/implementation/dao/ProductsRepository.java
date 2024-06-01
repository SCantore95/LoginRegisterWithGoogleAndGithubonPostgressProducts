package com.oauth.implementation.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oauth.implementation.model.Product;
import com.oauth.implementation.model.User;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
    List<Product> findByUser(User user);
    
}
