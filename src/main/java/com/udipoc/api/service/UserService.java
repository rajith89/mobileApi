package com.udipoc.api.service;

import com.udipoc.api.entity.UserSession;

import java.util.Optional;

public interface UserService {

    Optional<UserSession> findByUsername(String username);
}
