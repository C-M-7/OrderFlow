package com.orderflow.product;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final AtomicLong idCounter = new AtomicLong(1);

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product createProduct(String productName, double productPrice){
        Product product = new Product(idCounter.getAndIncrement(), productName, productPrice);
        productRepository.saveProduct(product);
        return product;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAllProducts();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }
}
