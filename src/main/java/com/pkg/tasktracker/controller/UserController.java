package com.pkg.tasktracker.controller;


import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.dto.UpdateProfileRequest;
import com.pkg.tasktracker.dto.UserResponse;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(){
        User u = currentUser.get();
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getEmail(), u.getFullName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser( @Valid @RequestBody UpdateProfileRequest r){
        User u = currentUser.get();
        u = userService.updateUser(u, r);
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getEmail(), u.getFullName()));
    }
}
