package org.techm.samples.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Role;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("guest@example.com");
        user.setName("Guest");
        user.setPassword("password");
        user.setRole(Role.GUEST);
        userRepository.save(user);

        post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setStatus(Status.PUBLISHED);
        post.setAuthor(user);
        postRepository.save(post);

        Comment comment1 = new Comment();
        comment1.setContent("First comment");
        comment1.setPost(post);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setContent("Second comment");
        comment2.setPost(post);
        commentRepository.save(comment2);
    }

    @Test
    void testDeleteByPostId() {
        List<Comment> commentsBefore = commentRepository.findAll();
        assertEquals(2, commentsBefore.size());

        commentRepository.deleteByPostId(post.getId());

        List<Comment> commentsAfter = commentRepository.findAll();
        assertTrue(commentsAfter.isEmpty());
    }
}
