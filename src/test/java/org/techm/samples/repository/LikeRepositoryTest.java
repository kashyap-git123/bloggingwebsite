package org.techm.samples.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.techm.samples.entity.Like;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Role;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    private Post post;
    private User user;
    private Like like;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("guest@example.com");
        user.setName("Guest");
        user.setPassword("password");
        user.setRole(Role.GUEST);
        userRepository.save(user);

        post = new Post();
        post.setTitle("Sample Post");
        post.setContent("Sample Content");
        post.setStatus(Status.PUBLISHED);
        post.setAuthor(user);
        postRepository.save(post);

        like = new Like();
        like.setGuestEmail("guest@example.com");
        like.setPost(post);
        likeRepository.save(like);
    }

    @Test
    void testExistsByGuestEmailAndPostId() {
        boolean exists = likeRepository.existsByGuestEmailAndPostId("guest@example.com", post.getId());
        assertTrue(exists);
    }

    @Test
    void testFindByPostId() {
        List<Like> likes = likeRepository.findByPostId(post.getId());
        assertEquals(1, likes.size());
        assertEquals("guest@example.com", likes.get(0).getGuestEmail());
    }

    @Test
    void testFindByGuestEmailAndPostId() {
        Optional<Like> foundOpt = likeRepository.findByGuestEmailAndPostId("guest@example.com", post.getId());
        assertTrue(foundOpt.isPresent());
        Like found = foundOpt.get();
        assertNotNull(found);
        assertEquals("guest@example.com", found.getGuestEmail());
    }

    @Test
    void testDeleteByPostId() {
        likeRepository.deleteByPostId(post.getId());
        assertFalse(likeRepository.existsByGuestEmailAndPostId("guest@example.com", post.getId()));
    }
}
