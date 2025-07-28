package org.techm.samples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.PostRepository;

class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private PostRepository postRepo;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);
        comment.setGuestName("John");
        comment.setGuestEmail("john@example.com");
        comment.setContent("Nice post!");
    }

    @Test
    void testSaveComment() {
        commentService.saveComment(comment);
        verify(commentRepo).save(comment);
    }

    @Test
    void testAddCommentSuccess() {
        when(postRepo.findById(1L)).thenReturn(Optional.of(post));

        commentService.addComment(1L, "John", "john@example.com", "Nice post!");

        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void testAddCommentPostNotFound() {
        when(postRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            commentService.addComment(1L, "John", "john@example.com", "Nice post!")
        );
    }

    @Test
    void testGetAllComments() {
        when(commentRepo.findAll()).thenReturn(Collections.emptyList());

        assertNotNull(commentService.getAllComments());
    }

    @Test
    void testGetCommentByIdSuccess() {
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(1L);

        assertEquals("Nice post!", result.getContent());
    }

    @Test
    void testGetCommentByIdNotFound() {
        when(commentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void testEditCommentSuccess() {
        Comment updated = new Comment();
        updated.setGuestName("Jane");
        updated.setGuestEmail("jane@example.com");
        updated.setContent("Updated comment");

        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepo.save(any(Comment.class))).thenReturn(updated);

        Comment result = commentService.editComment(1L, updated);

        assertEquals("Updated comment", result.getContent());
        assertEquals("Jane", result.getGuestName());
    }

    @Test
    void testEditCommentNotFound() {
        when(commentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.editComment(1L, comment));
    }

    @Test
    void testDeleteCommentSuccess() {
        when(commentRepo.existsById(1L)).thenReturn(true);

        commentService.deleteComment(1L);

        verify(commentRepo).deleteById(1L);
    }

    @Test
    void testDeleteCommentNotFound() {
        when(commentRepo.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L));
    }
}
