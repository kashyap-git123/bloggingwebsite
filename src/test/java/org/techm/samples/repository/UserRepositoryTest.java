package org.techm.samples.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.techm.samples.entity.User;
import org.techm.samples.entity.Role;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("securePassword");
        user.setRole(Role.BLOGGER);
        user.setName("Test User"); 
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId(), "User ID should not be null after saving");
        assertEquals("test@example.com", savedUser.getEmail());
    }


    @Test
    void testFindByEmailSuccess() {
        userRepository.save(user);
        User foundUser = userRepository.findByEmail("test@example.com");
        assertNotNull(foundUser, "User should be found by email");
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        User foundUser = userRepository.findByEmail("notfound@example.com");
        assertNull(foundUser, "User should be null when email does not exist");
    }
}
