package com.orderflow.product;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public void createProduct(Product prod){
        productRepository.saveProduct(prod);
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
