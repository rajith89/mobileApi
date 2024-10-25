package com.udipoc.api.service.impl;

import com.udipoc.api.entity.UserSession;
import com.udipoc.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service("AccUserDetailsService")
public class AccUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired

    public AccUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserSession> optionalUserSession = userService.findByUsername(username);
        if (optionalUserSession.isPresent()) {
            return new User(optionalUserSession.get().getUsername(), optionalUserSession.get().getPassword(), new ArrayList<>());

        } else {
            throw new InternalAuthenticationServiceException("Access to your account is temporarily disabled.\n Please contact your system administrator");
        }

    }
}
