package com.orderflow.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    public void saveProduct(Product product){
        products.add(product);
    }

    public List<Product> findAllProducts(){
        return products;
    } 

    public Product findById(Long id){
        for(Product prod : products){
            if(prod.getId().equals(id)){
                return prod;
            }
        }
        return null;
    }

    public Product deleteById(Long id) {
        Product product = findById(id);
        if (product != null) {
            products.remove(product);
        }
        return product;
    }
}
