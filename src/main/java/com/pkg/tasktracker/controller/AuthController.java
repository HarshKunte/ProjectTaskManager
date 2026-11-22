package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.AuthResponse;
import com.pkg.tasktracker.dto.LoginRequest;
import com.pkg.tasktracker.dto.SignUpRequest;
import com.pkg.tasktracker.dto.UserResponse;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup") @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> register(@Valid @RequestBody SignUpRequest r) throws ConflictException {
        return ResponseEntity.ok(authService.signUp(r));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest r) {
        return ResponseEntity.ok(authService.login(r));
    }

    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String header) {
        authService.logout(header);
        return ResponseEntity.ok("Logged out successfully");
    }
}
