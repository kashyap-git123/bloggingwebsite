package org.techm.samples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.techm.samples.entity.Role;
import org.techm.samples.entity.User;
import org.techm.samples.exception.UserAlreadyExistsException;
import org.techm.samples.exception.UserNotFoundException;
import org.techm.samples.repository.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");
        user.setRole(Role.BLOGGER);
    }

    @Test
    void testRegisterNewUserSuccess() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        User registered = userService.register(user);

        assertEquals(user.getEmail(), registered.getEmail());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(user));
    }

    @Test
    void testUserByUsernameSuccess() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        User found = userService.userByUsername(user.getEmail());

        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testUserByUsernameNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.userByUsername(user.getEmail()));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        user.setPassword("encodedPassword");

        UserDetails details = userService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), details.getUsername());
        assertTrue(details.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_BLOGGER")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                     () -> userService.loadUserByUsername(user.getEmail()));
    }
}
