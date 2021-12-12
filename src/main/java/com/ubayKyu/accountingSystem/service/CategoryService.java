package com.ubayKyu.accountingSystem.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.ubayKyu.accountingSystem.dto.CategoryInputDto;
import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private UserInfoService userInfoService;

    public List<Category> getCategoriesByUserID(UUID id){
        return repository.findAll(Sort.by("createDate"))
            .stream()
            .filter(item -> item.userInfo.userID.equals(id))
            .toList();
    }

    public boolean addCategory(UUID userId, CategoryInputDto categoryDto){
        // add error message
        // category name can't be the same

        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            return false;
        }

        Category newCategory = new Category();
        newCategory.categoryName = categoryDto.getCaption();
        newCategory.remark = categoryDto.getRemark();
        newCategory.userInfo = currentUser.get();

        try {
            repository.save(newCategory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

        return true;
    }

}
