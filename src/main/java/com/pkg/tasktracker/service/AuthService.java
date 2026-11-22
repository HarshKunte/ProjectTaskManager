package com.pkg.tasktracker.service;

import com.pkg.tasktracker.dto.AuthResponse;
import com.pkg.tasktracker.dto.LoginRequest;
import com.pkg.tasktracker.dto.SignUpRequest;
import com.pkg.tasktracker.dto.UserResponse;
import com.pkg.tasktracker.entity.RevokedToken;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.repository.RevokedTokenRepository;
import com.pkg.tasktracker.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    public UserResponse signUp(SignUpRequest req) throws ConflictException {
        if(userService.userExists(req.email())){
            throw new ConflictException("User already exists");
        }
        User u = new User();
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setFullName(req.fullName());
        u = userService.createUser(u);
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName());
    }

    public AuthResponse login(LoginRequest r) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(r.email().toLowerCase(), r.password()));

        User user = (User) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(user), "Bearer", jwtService.getExpirationMs());
    }

    public void logout(String bearerToken) {
        Claims c = jwtService.parse(bearerToken.split(" ")[1]);
        RevokedToken rt = new RevokedToken();
        rt.setJti(c.getId());
        rt.setExpiresAt(c.getExpiration().toInstant());
        revokedTokenRepository.save(rt);
    }
}
