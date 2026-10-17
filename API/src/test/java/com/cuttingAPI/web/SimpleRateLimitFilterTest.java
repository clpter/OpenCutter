package com.cuttingAPI.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "api.rate-limit.max-requests-per-minute=5")
@AutoConfigureMockMvc
class SimpleRateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return HTTP 429 when exceeding rate limit")
    void shouldReturn429_WhenRateLimitExceeded() throws Exception {
        String validPayload = """
            {
                "stock": [{"x": 0, "y": 0, "width": 100, "height": 100, "id": 1}],
                "pieces": [{"x": 0, "y": 0, "width": 50, "height": 50, "id": 2, "assignment": 0, "number": 1}]
            }
            """;

        String testIp = "192.168.1.100";

        // First 5 requests should succeed (rate limit is configured to 5 for this test)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/cut_1")
                    .header("X-Forwarded-For", testIp)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validPayload))
                    .andExpect(status().isOk());
        }

        // 6th request from the same IP should be blocked with 429
        mockMvc.perform(post("/api/cut_1")
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
                .andExpect(status().isTooManyRequests());
    }
}
