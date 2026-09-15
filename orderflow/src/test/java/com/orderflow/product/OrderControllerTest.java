package com.orderflow.product;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.orderflow.product.dto.OrderItemResponse;
import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;
import com.orderflow.product.exception.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        Customer customer = new Customer("John Doe", "1234567890", "john@example.com");
        customer.setId(1L);

        OrderItemResponse itemResponse = new OrderItemResponse(2L, "Wireless Mouse", 2, 50.0);
        OrderResponse response = new OrderResponse(100L, customer, 50.0, "CONFIRMED", List.of(itemResponse));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        String jsonRequest = """
            {
                "customerId": 1,
                "items": [
                    {
                        "productId": 2,
                        "quantity": 2
                    }
                ]
            }
            """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.customer.name").value("John Doe"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(2))
                .andExpect(jsonPath("$.items[0].productName").value("Wireless Mouse"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(50.0));
    }

    @Test
    void createOrder_WhenMissingCustomerId_ShouldReturnBadRequest() throws Exception {
        String jsonRequest = """
            {
                "items": [
                    {
                        "productId": 2,
                        "quantity": 2
                    }
                ]
            }
            """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer ID is required"));
    }

    @Test
    void createOrder_WhenItemsListIsEmpty_ShouldReturnBadRequest() throws Exception {
        String jsonRequest = """
            {
                "customerId": 1,
                "items": []
            }
            """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.items").value("Items list cannot be empty"));
    }

    @Test
    void createOrder_WhenItemQuantityIsZeroOrNegative_ShouldReturnBadRequest() throws Exception {
        String jsonRequest = """
            {
                "customerId": 1,
                "items": [
                    {
                        "productId": 2,
                        "quantity": 0
                    }
                ]
            }
            """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].quantity']").value("Quantity must be greater than 0"));
    }
}
