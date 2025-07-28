package org.techm.samples.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Role;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private Post publishedPost;
    private Post draftPost;
    
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setEmail("author@example.com");
        author.setName("Author Name");
        author.setPassword("password");
        author.setRole(Role.BLOGGER);
        userRepository.save(author);

        publishedPost = new Post();
        publishedPost.setTitle("Published Post");
        publishedPost.setContent("Content of published post");
        publishedPost.setStatus(Status.PUBLISHED);
        publishedPost.setCreatedAt(LocalDateTime.now());
        publishedPost.setUpdatedAt(LocalDateTime.now());
        publishedPost.setAuthor(author);
        postRepository.save(publishedPost);

        draftPost = new Post();
        draftPost.setTitle("Draft Post");
        draftPost.setContent("Content of draft post");
        draftPost.setStatus(Status.DRAFT);
        draftPost.setCreatedAt(LocalDateTime.now());
        draftPost.setUpdatedAt(LocalDateTime.now());
        draftPost.setAuthor(author);
        postRepository.save(draftPost);
    }

    @Test
    void testFindByStatus() {
        List<Post> published = postRepository.findByStatus(Status.PUBLISHED);
        assertEquals(1, published.size());
        assertEquals("Published Post", published.get(0).getTitle());
    }

    @Test
    void testFindByAuthorId() {
        List<Post> posts = postRepository.findByAuthorId(author.getId());
        assertEquals(2, posts.size());
    }

    @Test
    void testFindByAuthorIdAndStatus() {
        List<Post> drafts = postRepository.findByAuthorIdAndStatus(author.getId(), Status.DRAFT);
        assertEquals(1, drafts.size());
        assertEquals("Draft Post", drafts.get(0).getTitle());
    }

    @Test
    void testDeleteByCustomId() {
        postRepository.deleteByCustomId(publishedPost.getId());

testEntityManager.flush();

boolean exists = postRepository.existsById(publishedPost.getId());
assertFalse(exists);


        //assertFalse(postRepository.findById(publishedPost.getId()).isPresent());
        
    }
}
