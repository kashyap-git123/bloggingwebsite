package org.techm.samples.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.service.LikeService;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    @Test
    @WithMockUser(username = "guest@example.com", roles = "GUEST")
    void testAddLike() throws Exception {
        mockMvc.perform(post("/blog/likes/add")
                .param("postId", "1")
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/view/1"));

        verify(likeService).addLike(1L, "guest@example.com");
    }


    @Test
    @WithMockUser
    void testToggleLike() throws Exception {
        mockMvc.perform(post("/blog/likes/toggle")
                .param("postId", "2")
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/view/2"));

        verify(likeService).toggleLike(2L, "user");
    }

    @Test
    @WithMockUser
    void testGetLikesByPost() throws Exception {
        when(likeService.getLikesByPost(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/blog/likes/post/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void testGetAllLikes() throws Exception {
        when(likeService.getAllLikes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/blog/likes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void testRemoveLike() throws Exception {
        mockMvc.perform(delete("/blog/likes/remove/1").with(csrf()))
               .andExpect(status().isNoContent());

        verify(likeService).removeLike(1L);
    }
}
