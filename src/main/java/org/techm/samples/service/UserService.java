package org.techm.samples.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.techm.samples.entity.User;

public interface UserService extends UserDetailsService {
    User register(User user);
}

