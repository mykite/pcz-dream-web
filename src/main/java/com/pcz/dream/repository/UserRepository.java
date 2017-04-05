package com.pcz.dream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcz.dream.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
