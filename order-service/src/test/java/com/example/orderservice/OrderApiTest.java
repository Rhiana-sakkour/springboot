package com.example.orderservice;

import com.example.orderservice.client.UserClient;
import com.example.orderservice.client.UserInfo;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.exception.UserServiceUnavailableException;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Automated version of the four required test cases. user-service is replaced by a mock,
 * so these tests do not need user-service to be running.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTest {

    private static final String VALID_BODY =
            "{\"userId\":101,\"productName\":\"Laptop\",\"quantity\":2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private UserClient userClient;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void userExists_returns201AndSavesOrder() throws Exception {
        when(userClient.getUser(101L)).thenReturn(new UserInfo(101L, "Ahmed", "ahmed@example.com"));

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.userName").value("Ahmed"))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("CREATED"));

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void userNotFound_returns404AndSavesNothing() throws Exception {
        when(userClient.getUser(101L)).thenThrow(new UserNotFoundException(101L));

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        assertThat(orderRepository.count()).isZero();
    }

    @Test
    void userServiceDown_returns503AndSavesNothing() throws Exception {
        when(userClient.getUser(101L))
                .thenThrow(new UserServiceUnavailableException("user-service is down", new RuntimeException("boom")));

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("User service is unavailable"));

        assertThat(orderRepository.count()).isZero();
    }

    @Test
    void invalidBody_returns400AndDoesNotCallUserService() throws Exception {
        String invalid = "{\"userId\":101,\"productName\":\"\",\"quantity\":0}";

        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid order data"));

        verifyNoInteractions(userClient);
        assertThat(orderRepository.count()).isZero();
    }

    @Test
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("{\"userId\": "))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }
}
