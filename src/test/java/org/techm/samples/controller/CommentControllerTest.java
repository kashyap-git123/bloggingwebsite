package org.techm.samples.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import ResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.service.CommentService;
import org.techm.samples.service.PostService;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetAllComments() throws Exception {
        when(commentService.getAllComments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/blog/comments"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void testGetCommentById() throws Exception {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");

        when(commentService.getCommentById(1L)).thenReturn(comment);

        mockMvc.perform(get("/blog/comments/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @WithMockUser
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/blog/comments/delete/1").with(csrf()))
               .andExpect(status().isNoContent());

        verify(commentService).deleteComment(1L);
    }

    @Test
    @WithMockUser
    void testEditComment() throws Exception {
        Comment updated = new Comment();
        updated.setId(1L);
        updated.setContent("Updated content");

        when(commentService.editComment(eq(1L), any(Comment.class))).thenReturn(updated);

        mockMvc.perform(put("/blog/comments/edit/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updated))
                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @WithMockUser
    void testAddComment() throws Exception {
        Post post = new Post();
        post.setId(1L);

        when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(post("/blog/comments/add")
                .param("postId", "1")
                .param("guestName", "John Doe")
                .param("guestEmail", "john@example.com")
                .param("content", "Nice post!")
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/view/1"));

        verify(commentService).saveComment(any(Comment.class));
    }
}
