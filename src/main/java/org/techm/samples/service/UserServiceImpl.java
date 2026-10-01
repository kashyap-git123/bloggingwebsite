/*
 * package org.techm.samples.service;
 * 
 * import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.security.core.authority.SimpleGrantedAuthority; import
 * org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.core.userdetails.UsernameNotFoundException;
 * import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 * import org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.stereotype.Service; import org.techm.samples.entity.User;
 * import org.techm.samples.repository.UserRepository;
 * 
 * @Service public class UserServiceImpl implements
 * UserDetailsService,UserService {
 * 
 * @Autowired private UserRepository userRepo;
 * 
 * @Autowired private PasswordEncoder passwordEncoder;
 * 
 * public User userByUsername(String email) throws UsernameNotFoundException {
 * User user = userRepo.findByEmail(email); return user; return new
 * org.springframework.security.core.userdetails.User( user.getEmail(),
 * user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" +
 * user.getRole().name())) ); }
 * 
 * public User register(User user) {
 * user.setPassword(passwordEncoder.encode(user.getPassword())); return
 * userRepo.save(user); }
 * 
 * 
 * @Override public UserDetails loadUserByUsername(String username) throws
 * UsernameNotFoundException { User user = userRepo.findByEmail(username); if
 * (user == null) { throw new UsernameNotFoundException("User not found"); }
 * 
 * // Prefix the role with "ROLE_" String roleName = "ROLE_" +
 * user.getRole().name();
 * 
 * return new org.springframework.security.core.userdetails.User(
 * user.getEmail(), user.getPassword(), List.of(new
 * SimpleGrantedAuthority(roleName)) ); }
 * 
 * }
 */

package org.techm.samples.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.User;
import org.techm.samples.exception.UserAlreadyExistsException;
import org.techm.samples.exception.UserNotFoundException;
import org.techm.samples.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        if (userRepo.findByEmail(user.getEmail()) != null) {
            logger.atWarn()
                .addKeyValue("event.action", "user.registration")
                .addKeyValue("event.outcome", "failure")
                .addKeyValue("user.role", user.getRole())
                .log("User registration rejected");
            throw new UserAlreadyExistsException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        logger.atInfo()
            .addKeyValue("event.action", "user.registration")
            .addKeyValue("event.outcome", "success")
            .addKeyValue("user.id", savedUser.getId())
            .addKeyValue("user.role", savedUser.getRole())
            .log("User registered");
        return savedUser;
    }

    @Override
    public User userByUsername(String email) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            logger.atWarn()
                .addKeyValue("event.action", "user.authentication")
                .addKeyValue("event.outcome", "failure")
                .log("Authentication rejected");
            throw new UsernameNotFoundException("User not found");
        }

        String roleName = "ROLE_" + user.getRole().name();
        logger.atInfo()
            .addKeyValue("event.action", "user.authentication.lookup")
            .addKeyValue("event.outcome", "success")
            .addKeyValue("user.id", user.getId())
            .addKeyValue("user.role", user.getRole())
            .log("Authentication principal loaded");
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}
