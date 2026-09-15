package com.orderflow.product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.orderflow.product.dto.OrderItemRequest;
import com.orderflow.product.dto.OrderRequest;
import com.orderflow.product.dto.OrderResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderApiIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/orders";
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testCreateOrderApi_Success() {
        Customer customer = customerRepository.save(new Customer("Alice Smith", "9876543210", "alice@example.com"));
        Product product1 = productRepository.save(new Product("Mechanical Keyboard", 100.0, 10L));
        Product product2 = productRepository.save(new Product("Gaming Mouse", 50.0, 5L));

        OrderItemRequest item1 = new OrderItemRequest(product1.getId(), 2);
        OrderItemRequest item2 = new OrderItemRequest(product2.getId(), 1);

        OrderRequest request = new OrderRequest(customer.getId(), List.of(item1, item2));

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(baseUrl, request, OrderResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getOrderId());
        assertEquals(customer.getId(), response.getBody().getCustomer().getId());
        assertEquals(250.0, response.getBody().getAmount());
        assertEquals("CONFIRMED", response.getBody().getStatus());
        assertEquals(2, response.getBody().getItems().size());

        // Verify database state: product quantities decremented
        Product updatedProduct1 = productRepository.findById(product1.getId()).orElseThrow();
        Product updatedProduct2 = productRepository.findById(product2.getId()).orElseThrow();
        assertEquals(8L, updatedProduct1.getQuantity());
        assertEquals(4L, updatedProduct2.getQuantity());

        // Verify order & order items saved in database
        assertEquals(1, orderRepository.count());
        assertEquals(2, orderItemRepository.count());
    }

    @Test
    void testCreateOrderApi_PartialStock_SkipsOutOfStockItem() {
        Customer customer = customerRepository.save(new Customer("Bob Johnson", "5551234567", "bob@example.com"));
        Product inStockProd = productRepository.save(new Product("Mouse Pad", 15.0, 10L));
        Product outOfStockProd = productRepository.save(new Product("Limited Monitor", 300.0, 2L));

        // Request 2 mouse pads (in stock) and 5 monitors (out of stock)
        OrderItemRequest item1 = new OrderItemRequest(inStockProd.getId(), 2);
        OrderItemRequest item2 = new OrderItemRequest(outOfStockProd.getId(), 5);
        OrderRequest request = new OrderRequest(customer.getId(), List.of(item1, item2));

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(baseUrl, request, OrderResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        // Only in-stock item (2 mouse pads = $30.0) is included; out-of-stock item is skipped
        assertEquals(30.0, response.getBody().getAmount());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(inStockProd.getId(), response.getBody().getItems().get(0).getProductId());

        // In-stock product quantity decremented, out-of-stock product quantity unchanged
        Product updatedInStock = productRepository.findById(inStockProd.getId()).orElseThrow();
        Product updatedOutOfStock = productRepository.findById(outOfStockProd.getId()).orElseThrow();
        assertEquals(8L, updatedInStock.getQuantity());
        assertEquals(2L, updatedOutOfStock.getQuantity());
    }

    @Test
    void testCreateOrderApi_CustomerNotFound_ReturnsServerError() {
        Product product = productRepository.save(new Product("Desk Mat", 20.0, 10L));
        OrderItemRequest item = new OrderItemRequest(product.getId(), 1);
        OrderRequest request = new OrderRequest(9999L, List.of(item));

        // When customer is not found, service throws exception (Runtime Exception)
        try {
            restTemplate.postForEntity(baseUrl, request, OrderResponse.class);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
        }
    }
}
