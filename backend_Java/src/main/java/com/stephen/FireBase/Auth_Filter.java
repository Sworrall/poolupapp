package com.stephen.FireBase;

import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class Auth_Filter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(Auth_Filter.class);

    private final Auth_Service authService;

    public Auth_Filter(Auth_Service authService) {
        this.authService = authService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
        return;


//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
//            return;
//        }
//
//        String idToken = authHeader.substring(7);
//
//        try {
//            FirebaseToken decoded = authService.verifyToken(idToken);
//            request.setAttribute("uid", decoded.getUid());
//            request.setAttribute("email", decoded.getEmail());
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            log.error("Token verification failed", e);
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//        }
    }
}