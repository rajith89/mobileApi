package com.udipoc.api.service.impl;

import com.udipoc.api.entity.UserSession;
import com.udipoc.api.repository.UserSessionRepository;
import com.udipoc.api.service.UserService;
import com.udipoc.api.util.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserServiceImpl(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public Optional<UserSession> findByUsername(String username) {
        return userSessionRepository.findByUsername(username, Status.ACTIVE);
    }
}
