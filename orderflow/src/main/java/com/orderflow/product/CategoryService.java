package com.orderflow.product;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderflow.product.dto.CategoryRequest;
import com.orderflow.product.dto.CategoryResponse;

@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest){
        log.info("Creating category with name: {}", categoryRequest.getName());
        Category category = new Category();

        category.setName(categoryRequest.getName());

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());

        return new CategoryResponse(savedCategory.getId(), savedCategory.getName());
    }
}
