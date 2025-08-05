package org.techm.samples.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.techm.samples.entity.Like;
import org.techm.samples.entity.Post;
import org.techm.samples.repository.LikeRepository;
import org.techm.samples.repository.PostRepository;

class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepo;

    @Mock
    private PostRepository postRepo;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        post = new Post();
        post.setId(1L);
    }

    @Test
    void testAddLike_NewLike() {
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepo.existsByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(false);

        likeService.addLike(1L, "guest@example.com");

        verify(likeRepo).save(any(Like.class));
    }

    @Test
    void testAddLike_AlreadyLiked() {
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepo.existsByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(true);

        likeService.addLike(1L, "guest@example.com");

        verify(likeRepo, never()).save(any(Like.class));
    }

    @Test
    void testAddLike_PostNotFound() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> likeService.addLike(1L, "guest@example.com"));
    }

    @Test
    void testGetLikesByPost() {
        when(likeRepo.findByPostId(1L)).thenReturn(Collections.emptyList());

        List<Like> likes = likeService.getLikesByPost(1L);

        assertNotNull(likes);
        assertTrue(likes.isEmpty());
    }

    @Test
    void testRemoveLike_Success() {
        when(likeRepo.existsById(1L)).thenReturn(true);

        likeService.removeLike(1L);

        verify(likeRepo).deleteById(1L);
    }

    @Test
    void testRemoveLike_NotFound() {
        when(likeRepo.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> likeService.removeLike(1L));
    }

    @Test
    void testAlreadyLiked() {
        when(likeRepo.existsByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(true);

        assertTrue(likeService.alreadyLiked("guest@example.com", 1L));
    }

    @Test
    void testGetAllLikes() {
        when(likeRepo.findAll()).thenReturn(Collections.emptyList());

        List<Like> likes = likeService.getAllLikes();

        assertNotNull(likes);
        assertTrue(likes.isEmpty());
    }

    @Test
    void testToggleLike_AddNew() {
        when(likeRepo.existsByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(false);
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));

        boolean result = likeService.toggleLike(1L, "guest@example.com");

        assertTrue(result);
        verify(likeRepo).save(any(Like.class));
    }

    @Test
    void testToggleLike_RemoveExisting() {
        Like like = new Like();
        like.setId(1L);
        like.setGuestEmail("guest@example.com");
        like.setPost(post);

        when(likeRepo.existsByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(true);
        when(likeRepo.findByGuestEmailAndPostId("guest@example.com", 1L)).thenReturn(Optional.of(like));  // ✅ Fixed

        boolean result = likeService.toggleLike(1L, "guest@example.com");

        assertFalse(result);
        verify(likeRepo).delete(like);
    }
}
