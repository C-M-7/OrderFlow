package com.orderflow.product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/products";
        productRepository.deleteAll();
    }

    @Test
    void testCreateProductApi() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Gaming Laptop");
        request.setPrice(1499.99);
        request.setQuantity(5L);

        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(baseUrl, request, ProductResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Gaming Laptop", response.getBody().getName());
        assertEquals(1499.99, response.getBody().getPrice());
        assertEquals(5L, response.getBody().getQuantity());
    }

    @Test
    void testGetAllProductsApi() {
        productRepository.save(new Product("Phone", 699.99, 12L));
        productRepository.save(new Product("Tablet", 399.99, 20L));

        ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ProductResponse>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetProductByIdApi() {
        Product savedProduct = productRepository.save(new Product("Headphones", 199.99, 15L));

        ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
            baseUrl + "/" + savedProduct.getId(),
            ProductResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Headphones", response.getBody().getName());
    }

    @Test
    void testUpdateProductApi() {
        Product savedProduct = productRepository.save(new Product("Smartwatch", 249.99, 8L));

        UpdateProductRequest request = new UpdateProductRequest();
        request.setId(savedProduct.getId());
        request.setName("Smartwatch v2");
        request.setPrice(299.99);
        request.setQuantity(10L);

        HttpEntity<UpdateProductRequest> requestEntity = new HttpEntity<>(request);
        ResponseEntity<ProductResponse> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.PUT,
            requestEntity,
            ProductResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Smartwatch v2", response.getBody().getName());
        assertEquals(299.99, response.getBody().getPrice());
        assertEquals(10L, response.getBody().getQuantity());
    }

    @Test
    void testDeleteProductApi() {
        Product savedProduct = productRepository.save(new Product("Speaker", 99.99, 30L));

        ResponseEntity<ProductResponse> response = restTemplate.exchange(
            baseUrl + "/" + savedProduct.getId(),
            HttpMethod.DELETE,
            null,
            ProductResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Speaker", response.getBody().getName());
        assertEquals(0, productRepository.count());
    }
}
