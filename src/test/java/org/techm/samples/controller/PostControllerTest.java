package org.techm.samples.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;
import org.techm.samples.service.CommentService;
import org.techm.samples.service.LikeService;
import org.techm.samples.service.PostService;
import org.techm.samples.service.UserService;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LikeService likeService;

    @MockitoBean
    private CommentService commentService;

    @Test
    @WithMockUser(roles = "BLOGGER")
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/posts/create"))
               .andExpect(status().isOk())
               .andExpect(view().name("blogs/create-post"))
               .andExpect(model().attributeExists("post"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "BLOGGER")
    void testHandleCreatePublishedPost() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        Post post = new Post();
        post.setStatus(Status.PUBLISHED);

        when(userService.userByUsername("test@example.com")).thenReturn(user);

        mockMvc.perform(post("/posts/create")
                .flashAttr("post", post)
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/mine"));

        verify(postService).createPost(any(Post.class));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "BLOGGER")
    void testHandleCreateDraftPost() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        Post post = new Post();
        post.setStatus(Status.DRAFT);

        when(userService.userByUsername("test@example.com")).thenReturn(user);

        mockMvc.perform(post("/posts/create")
                .flashAttr("post", post)
        .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/posts/drafts"));

        verify(postService).saveAsDraft(any(Post.class));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "BLOGGER")
    void testShowMyPosts() throws Exception {
        when(postService.getPublishedPostsByUserEmail("test@example.com"))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts/mine"))
               .andExpect(status().isOk())
               .andExpect(view().name("blogs/my-posts"))
               .andExpect(model().attributeExists("posts"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "BLOGGER")
    void testDeletePost() throws Exception {
        when(postService.deletePost(1L)).thenReturn(true);



mockMvc.perform(post("/posts/delete/1").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/posts/mine"));


        verify(postService).deletePost(1L);
    }
}
