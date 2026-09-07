package com.orderflow.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.ProductResponse;
import com.orderflow.product.dto.UpdateProductRequest;
import com.orderflow.product.exception.ProductNotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = findCategory(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return mapToProductResponse(product);
    }

    public ProductResponse deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
        return mapToProductResponse(product);
    }

    public ProductResponse updateProduct(UpdateProductRequest request) {
        Product product = findProductById(request.getId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
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
        Category category = categoryRepository.findById(categoryId)
                                              .orElseThrow(() -> new RuntimeException("Category not found"));
        return category;
    }
}
