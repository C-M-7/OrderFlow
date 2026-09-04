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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", 999.99, 10L);
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        String jsonRequest = """
            {
                "name": "Laptop",
                "price": 999.99,
                "quantity": 10
            }
            """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        ProductResponse response1 = new ProductResponse(1L, "Laptop", 999.99, 10L);
        ProductResponse response2 = new ProductResponse(2L, "Mouse", 25.50, 50L);
        when(productService.getAllProducts()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", 999.99, 10L);
        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop Pro", 1299.99, 15L);
        when(productService.updateProduct(any(UpdateProductRequest.class))).thenReturn(response);

        String jsonRequest = """
            {
                "id": 1,
                "name": "Laptop Pro",
                "price": 1299.99,
                "quantity": 15
            }
            """;

        mockMvc.perform(put("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    void deleteProductById_ShouldReturnDeletedProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", 999.99, 10L);
        when(productService.deleteProduct(1L)).thenReturn(response);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
