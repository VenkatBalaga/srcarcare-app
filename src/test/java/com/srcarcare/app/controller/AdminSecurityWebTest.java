package com.srcarcare.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SR CAR CARE")));
    }

    @Test
    void adminDashboardRedirectsToLoginWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }

    @Test
    void adminDashboardAccessibleAfterAuthentication() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void loginFailsWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", "admin")
                        .param("password", "wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error"));
    }

    @Test
    void loginSucceedsWithSeededDefaultAdmin() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("username", "admin")
                        .param("password", "Test@1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    void postWithoutCsrfTokenIsRejected() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("username", "admin")
                        .param("password", "Test@1234"))
                .andExpect(status().isForbidden());
    }
}
