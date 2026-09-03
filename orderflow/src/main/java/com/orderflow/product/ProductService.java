package com.orderflow.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CreateProductRequest;
import com.orderflow.product.dto.UpdateProductRequest;
import com.orderflow.product.exception.ProductNotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        return product;
    }

    public Product updateProduct(UpdateProductRequest request) {
        Product product = getProductById(request.getId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        return productRepository.save(product);
    }
}

