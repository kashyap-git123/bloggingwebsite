package org.techm.samples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;
import org.techm.samples.exception.AuthorNotFoundException;
import org.techm.samples.exception.PostNotFoundException;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.LikeRepository;
import org.techm.samples.repository.PostRepository;
import org.techm.samples.repository.ReplyRepository;
import org.techm.samples.repository.UserRepository;

class PostServiceImplTest {

    @Mock 
    private PostRepository postRepository;
    @Mock 
    private LikeRepository likeRepository;
    @Mock 
    private ReplyRepository replyRepository;
    @Mock 
    private UserRepository userRepository;
    @Mock 
    private CommentRepository commentRepository;

    @InjectMocks 
    private PostServiceImpl postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthor(user);
    }

    @Test
    void testCreatePostSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.createPost(post);

        assertEquals(Status.PUBLISHED, result.getStatus());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testCreatePostAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> postService.createPost(post));
    }

    @Test
    void testSaveAsDraftSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.saveAsDraft(post);

        assertEquals(Status.DRAFT, result.getStatus());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testEditPostSuccess() {
        Post updated = new Post();
        updated.setTitle("Updated Title");
        updated.setContent("Updated Content");
        updated.setStatus(Status.PUBLISHED);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(updated);

        Post result = postService.editPost(1L, updated);

        assertEquals("Updated Title", result.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testEditPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.editPost(1L, post));
    }

    @Test
    void testDeletePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        boolean result = postService.deletePost(1L);

        assertTrue(result);
        verify(commentRepository).deleteByPostId(1L);
        verify(likeRepository).deleteByPostId(1L);
        verify(postRepository).deleteByCustomId(1L);
    }

    @Test
    void testDeletePostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void testGetPostByIdSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPostByIdNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    void testGetPublishedPosts() {
        when(postRepository.findByStatus(Status.PUBLISHED)).thenReturn(List.of(post));

        List<Post> posts = postService.getPublishedPosts();

        assertFalse(posts.isEmpty());
    }

    @Test
    void testGetPostsByUserEmailSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(post));

        List<Post> posts = postService.getPostsByUserEmail("test@example.com");

        assertEquals(1, posts.size());
    }

    @Test
    void testGetPostsByUserEmailNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        assertThrows(AuthorNotFoundException.class, () -> postService.getPostsByUserEmail("test@example.com"));
    }

    @Test
    void testPublishDraft() {
        post.setStatus(Status.DRAFT);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.publishDraft(1L);

        assertEquals(Status.PUBLISHED, post.getStatus());
        verify(postRepository).save(post);
    }
}
