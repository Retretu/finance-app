package org.gouenji.financeapp.controller.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // === Тест /error ===
    @Test
    void getErrorPage_ShouldReturnErrorView() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/error/error-page"));
    }

    // === Тест обработки исключения ===
    @Test
    void handleThrowable_ShouldRedirectToError() throws Exception {
        mockMvc.perform(get("/fail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @RestController
    static class DummyController {
        @GetMapping("/fail")
        public String fail() {
            throw new RuntimeException("Boom!");
        }
    }
}
