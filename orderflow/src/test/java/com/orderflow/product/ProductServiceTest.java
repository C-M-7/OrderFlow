package com.orderflow.product;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;
import com.orderflow.product.exception.ProductNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_ShouldReturnProductResponse() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Wireless Mouse");
        request.setPrice(29.99);
        request.setQuantity(50L);

        Product savedProduct = new Product(1L, "Wireless Mouse", 29.99, 50L);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Wireless Mouse", response.getName());
        assertEquals(29.99, response.getPrice());
        assertEquals(50L, response.getQuantity());
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductResponses() {
        Product p1 = new Product(1L, "Laptop", 999.99, 10L);
        Product p2 = new Product(2L, "Mouse", 25.50, 50L);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
        assertEquals("Mouse", responses.get(1).getName());
    }

    @Test
    void getProductById_WhenFound_ShouldReturnProductResponse() {
        Product product = new Product(1L, "Monitor", 299.99, 15L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getName());
        assertEquals(299.99, response.getPrice());
        assertEquals(15L, response.getQuantity());
    }

    @Test
    void getProductById_WhenNotFound_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void updateProduct_WhenFound_ShouldReturnUpdatedProductResponse() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setId(1L);
        request.setName("Monitor Ultra");
        request.setPrice(349.99);
        request.setQuantity(20L);

        Product existingProduct = new Product(1L, "Monitor", 299.99, 15L);
        Product updatedProduct = new Product(1L, "Monitor Ultra", 349.99, 20L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor Ultra", response.getName());
        assertEquals(349.99, response.getPrice());
        assertEquals(20L, response.getQuantity());
    }

    @Test
    void deleteProduct_WhenFound_ShouldDeleteAndReturnProductResponse() {
        Product product = new Product(1L, "Monitor", 299.99, 15L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.deleteProduct(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getName());
        verify(productRepository).delete(product);
    }
}
