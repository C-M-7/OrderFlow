package com.orderflow.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;
import com.orderflow.product.exception.ProductNotFoundException;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with name: {}, price: {}, quantity: {}", request.getName(), request.getPrice(), request.getQuantity());
        Category category = findCategory(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return mapToProductResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = findProductById(id);
        return mapToProductResponse(product);
    }

    public ProductResponse deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = findProductById(id);
        productRepository.delete(product);
        log.info("Product {} deleted successfully", id);
        return mapToProductResponse(product);
    }

    public ProductResponse updateProduct(UpdateProductRequest request) {
        log.info("Updating product with id: {}", request.getId());
        Product product = findProductById(request.getId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        Product updatedProduct = productRepository.save(product);
        log.info("Product {} updated successfully", updatedProduct.getId());
        return mapToProductResponse(updatedProduct);
    }

    private Product findProductById(Long id) {
        log.debug("Looking up product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ProductNotFoundException(id);
                });
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getQuantity()
        );
    }

    private Category findCategory(Long categoryId){
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", categoryId);
                    return new RuntimeException("Category not found");
                });
    }
}
