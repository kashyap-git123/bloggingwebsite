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
        logger.info("Attempting to register user with email: {}", user.getEmail());

        if (userRepo.findByEmail(user.getEmail()) != null) {
            logger.warn("Registration failed: User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);

        logger.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User userByUsername(String email) {
        logger.debug("Fetching user by email: {}", email);
        User user = userRepo.findByEmail(email);

        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UserNotFoundException("User not found with email: " + email);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user details for authentication: {}", email);
        User user = userRepo.findByEmail(email);

        if (user == null) {
            logger.error("Authentication failed: User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found");
        }

        String roleName = "ROLE_" + user.getRole().name();
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}
