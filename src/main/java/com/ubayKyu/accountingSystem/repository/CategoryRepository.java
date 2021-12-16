package com.ubayKyu.accountingSystem.repository;

import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,UUID> {
    
}
