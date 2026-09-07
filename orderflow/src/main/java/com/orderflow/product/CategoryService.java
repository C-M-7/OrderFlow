package com.orderflow.product;

import com.orderflow.product.dto.CategoryRequest;
import com.orderflow.product.dto.CategoryResponse;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest){
        Category category = new Category();

        category.setName(categoryRequest.getName());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory.getId(), savedCategory.getName());
    }
}
