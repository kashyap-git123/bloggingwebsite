package org.techm.samples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.User;
import org.techm.samples.repository.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService,UserService {
    @Autowired 
    private UserRepository userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    public User userByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        return user;
        /*return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );*/
    }
 
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Prefix the role with "ROLE_"
        String roleName = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(roleName))
        );
    }

}