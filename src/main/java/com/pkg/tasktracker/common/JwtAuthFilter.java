package com.pkg.tasktracker.common;

import com.pkg.tasktracker.repository.RevokedTokenRepository;
import com.pkg.tasktracker.service.JwtService;
import com.pkg.tasktracker.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;
    @Autowired
    private UserService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            logger.info("Header: " + header);
            Claims claims = jwtService.parse(header.split(" ")[1]);

            logger.info("Claims: " + claims);
            if (!revokedTokenRepository.existsById((Long.valueOf(claims.getSubject())))) {
                UserDetails user = userDetailsService.getUserById(Long.valueOf(claims.getSubject()));
                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            logger.info("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        }catch(JwtException | IllegalArgumentException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
