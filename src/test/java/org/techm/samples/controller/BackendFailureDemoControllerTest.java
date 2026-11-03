package org.techm.samples.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.techm.samples.entity.Role;
import org.techm.samples.entity.User;
import org.techm.samples.service.UserService;

@WebMvcTest(BackendFailureDemoController.class)
@ActiveProfiles("elk")
class BackendFailureDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    void testBackendFallbackSimulationKeepsEndpointSuccessful() throws Exception {
        User fallback = new User();
        fallback.setEmail("fallback-demo@example.test");
        fallback.setRole(Role.GUEST);
        when(userService.simulateSilentBackendFailure("fallback-demo@example.test", true)).thenReturn(fallback);

        mockMvc.perform(get("/blog/users/debug/backend-fallback").param("simulate", "true"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("ok"))
               .andExpect(jsonPath("$.simulate").value(true))
               .andExpect(jsonPath("$.user.role").value("GUEST"));
    }
}
