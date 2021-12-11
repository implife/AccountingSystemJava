package com.ubayKyu.accountingSystem.service;

import java.util.List;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> getCategoriesByUserID(UUID id){
        return repository.findAll()
            .stream()
            .filter(item -> item.userInfo.userID.equals(id))
            .toList();
    }

}
