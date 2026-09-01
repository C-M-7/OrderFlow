package com.orderflow.product;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product createProduct(String productName, double productPrice){
        Product product = new Product(productName, productPrice);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product deleteProduct(Long id) {
        Product product = getProductById(id);
        if (product != null) {
            productRepository.delete(product);
        }
        return product;
    }
}
