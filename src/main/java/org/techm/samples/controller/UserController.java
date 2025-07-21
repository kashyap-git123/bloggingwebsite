package org.techm.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.techm.samples.entity.User;
import org.techm.samples.service.UserService;

@RestController
@RequestMapping("/blog/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/login/{email}")
    public ResponseEntity<?> getUserDetails(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.loadUserByUsername(email));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}

