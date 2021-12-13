package com.ubayKyu.accountingSystem.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // 新增一筆類別進資料庫
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

    // 刪除多筆類別
    public boolean deleteCategories(String[] ids, UUID userId, List<String> errMsg){

        // 使用者錯誤回傳false
        if(userInfoService.getUserById(userId).isEmpty()){
            errMsg.add("使用者錯誤");
            return false;
        }

        List<Category> catList = this.getCategoriesByUserID(userId);
        List<UUID> deleteList = new ArrayList<>();
        String pattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";

        // 檢查格式
        for(String uuidStr: ids){
            try {
                if(uuidStr.matches(pattern)){
                    deleteList.add(UUID.fromString(uuidStr));
                }
                else{
                    throw new IllegalArgumentException(uuidStr + " -> 無法辨識ID");
                }
            } catch (IllegalArgumentException e) {
                errMsg.add(e.getMessage());
            }
        }

        // 檢查是否在catList裡，並刪除
        for(UUID uuid: deleteList){
            Optional<Category> tempCat = catList.stream()
                .filter(obj -> obj.categoryID.equals(uuid))
                .findFirst();

            if(tempCat.isPresent()){
                try {
                    repository.delete(tempCat.get());
                } catch (Exception e) {
                    if(e.getMessage().contains("ConstraintViolationException")){
                        errMsg.add(tempCat.get().categoryName + " -> 有流水帳使用中，無法刪除");
                    }
                    else{
                        errMsg.add(tempCat.get().categoryName + " -> 資料庫錯誤");
                    }
                }
            }
            else{
                errMsg.add(uuid.toString() + " -> ID不存在");
            }
        }

        return true;
    }

}
