package com.udipoc.api.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Component
@Transactional
public class CustomLogoutHandler  extends SecurityContextLogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.setClearAuthentication(authentication != null);
        super.setInvalidateHttpSession(request.getSession() != null);
        super.logout(request, response, authentication);
        request.removeAttribute("username");
        request.removeAttribute("bearer-access-token");
    }
}
