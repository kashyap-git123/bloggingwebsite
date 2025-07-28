package org.techm.samples.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Reply;
import org.techm.samples.service.CommentServiceImpl;
import org.techm.samples.service.ReplyServiceImpl;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ReplyController.class)
class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReplyServiceImpl replyService;

    @MockitoBean
    private CommentServiceImpl commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testAddReply() throws Exception {
        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);

        when(commentService.getCommentById(1L)).thenReturn(comment);

        mockMvc.perform(post("/blog/reply/add")
                .param("commentId", "1")
                .param("replyText", "Thanks for your comment!")
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/view/1"));

        verify(replyService).addReply(any(Reply.class));
    }

    @Test
    @WithMockUser
    void testGetReplyById() throws Exception {
        Reply reply = new Reply();
        reply.setId(1L);
        reply.setReply("Sample reply");

        when(replyService.getReply(1L)).thenReturn(reply);

        mockMvc.perform(get("/blog/reply/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.reply").value("Sample reply"));
    }

    @Test
    @WithMockUser
    void testGetAllReplies() throws Exception {
        when(replyService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/blog/reply"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }
}
