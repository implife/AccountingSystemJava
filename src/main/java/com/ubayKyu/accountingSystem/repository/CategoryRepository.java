package com.ubayKyu.accountingSystem.repository;

import java.util.List;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.entity.UserInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category,UUID> {
    
    Page<Category> findByUserInfo(UserInfo userInfo, Pageable pageable);

    /**
     * 取得特定使用者的分類數量
     * 
     * @param userId must not be null
     * @return count of categories, may be null
     */
    @Query(value = "SELECT COUNT(*) FROM category WHERE user_id = ?1", nativeQuery = true)
    Integer getCountByUserId(UUID userId);

}
