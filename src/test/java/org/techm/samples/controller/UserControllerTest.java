package org.techm.samples.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.entity.User;
import org.techm.samples.service.UserService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/blog/users/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("blogs/login"));

    }

    @Test
    @WithMockUser
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/blog/users/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("blogs/register"))
               .andExpect(model().attributeExists("user"));
    }

	/*
	 * @Test
	 * 
	 * @WithMockUser void testRegisterUser() throws Exception { User user = new
	 * User(); user.setEmail("test@example.com");
	 * 
	 * //doNothing().when(userService).register(user);
	 * when(userService.register(user)).thenReturn(user);
	 * 
	 * 
	 * mockMvc.perform(post("/blog/users/register") .param("email",
	 * "test@example.com") .flashAttr("user", user))
	 * .andExpect(status().is3xxRedirection())
	 * .andExpect(redirectedUrl("/blog/users/login")); }
	 */
    
    

    @Test
    @WithMockUser
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.register(user)).thenReturn(user);

        mockMvc.perform(post("/blog/users/register")
                .param("email", "test@example.com")
                .flashAttr("user", user)
                .with(csrf()));  // ✅ This lineExpect(redirectedUrl("/blog/users/login"));
    }


    @Test
    @WithMockUser(roles = "BLOGGER")
    void testBloggerDashboardAccess() throws Exception {
        mockMvc.perform(get("/blog/users/blogger/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("blogs/blogger-dashboard"));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGuestDashboardAccess() throws Exception {
        mockMvc.perform(get("/blog/users/guest/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("blogs/guest-dashboard"));
    }

    @Test
    @WithMockUser(roles = "BLOGGER")
    void testRedirectAfterLoginForBlogger() throws Exception {
        mockMvc.perform(get("/blog/users/redirect"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/blog/users/blogger/dashboard"));
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testRedirectAfterLoginForGuest() throws Exception {
        mockMvc.perform(get("/blog/users/redirect"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/blog/users/guest/dashboard"));
    }
}

