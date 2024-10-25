package com.udipoc.api.config.filter;

import com.udipoc.api.config.security.JwtAuthenticationProvider;
import com.udipoc.api.entity.UserSession;
import com.udipoc.api.service.UserService;
import com.udipoc.api.service.impl.AccUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("AccUserDetailsService")
    private AccUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationProvider jwtTokenConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        String jwtToken = null;
        Optional<String> hashToken = Optional.ofNullable(request.getHeader("Authorization"));

        if (hashToken.isPresent() && hashToken.get().startsWith("Bearer ")) {
            jwtToken = hashToken.get().substring(7);
            try {
                username = jwtTokenConfig.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            } catch (SignatureException e) {
                logger.error("JWT Token has Invalid");
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenConfig.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                Optional<UserSession> optionalUserSession = userService.findByUsername(username);
                if (optionalUserSession.isPresent()) {
                    request.setAttribute("username", optionalUserSession.get().getUsername());
                    request.setAttribute("bearer-access-token", jwtToken);
                } else {
                    logger.debug("User - UNAUTHORIZED : " + username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token replaced or expired.");
                    return;
                }

            } else {
                logger.debug("Token - EXPIRED : " + username);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token replaced or expired.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
