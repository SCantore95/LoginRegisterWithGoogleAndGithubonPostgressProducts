package com.oauth.implementation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oauth.implementation.model.User;

import jakarta.transaction.Transactional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	User findByEmail(String emailId);
	@Transactional
    @Modifying
	@Query(value = "ALTER SEQUENCE public.products_id_seq RESTART WITH 1", nativeQuery = true)
    void resetSequence();
}
