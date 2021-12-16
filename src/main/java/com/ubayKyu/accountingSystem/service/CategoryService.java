package com.ubayKyu.accountingSystem.service;

import java.util.ArrayList;
import java.util.List;
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
            .filter(item -> item.getUserInfo().getUserID().equals(id))
            .toList();
    }

    public Optional<Category> getCategoryByCategoryIDStr(UUID userId, String categoryIdStr){
        if(categoryIdStr == null){
            return Optional.empty();
        }

        String pattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";

        // 檢查格式
        try {
            if(categoryIdStr.matches(pattern)){
                return this.getCategoryByuserIdAndCategoryId(userId, UUID.fromString(categoryIdStr));
            }
            else{
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Category> getCategoryByuserIdAndCategoryId(UUID userId, UUID categoryId){
        if(userId == null || categoryId == null){
            return Optional.empty();
        }
        
        return this.getCategoriesByUserID(userId)
            .stream()
            .filter(item -> item.getCategoryID().equals(categoryId))
            .findFirst();
    }

    // 新增一筆類別進資料庫，成功的話回傳該UUID
    public UUID addCategory(UUID userId, CategoryInputDto categoryDto, List<String> errMsg){

        // check user
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return null;
        }

        // 類別名稱不可一樣
        boolean hasTheSame = repository.findAll()
            .stream()
            .filter(item -> item.getUserInfo().getUserID().equals(userId) && item.getCategoryName().equals(categoryDto.getCaption()))
            .findFirst()
            .isPresent();
        if(hasTheSame){
            errMsg.add("*該名稱已存在");
            return null;
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(categoryDto.getCaption());
        newCategory.setRemark(categoryDto.getRemark());
        newCategory.setUserInfo(currentUser.get());

        try {

            return repository.save(newCategory).getCategoryID();

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return null;
        }
    }

    // 編輯分類
    public boolean updateCategory(UUID userId, CategoryInputDto categoryDto, List<String> errMsg){
        
        // check user
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return false;
        }

        // check categoryId
        Optional<Category> targetCategory = this.getCategoryByuserIdAndCategoryId(userId, categoryDto.getCategoryId());
        if(targetCategory.isEmpty()){
            errMsg.add("該類別不存在");
            return false;
        }

        // 類別名稱不可一樣
        boolean hasTheSame = repository.findAll()
            .stream()
            .filter(item -> item.getUserInfo().getUserID().equals(userId) 
                && !item.getCategoryID().equals(categoryDto.getCategoryId()) 
                && item.getCategoryName().equals(categoryDto.getCaption()))
            .findFirst()
            .isPresent();
        if(hasTheSame){
            errMsg.add("*該名稱已存在");
            return false;
        }

        targetCategory.get().setCategoryName(categoryDto.getCaption());
        targetCategory.get().setRemark(categoryDto.getRemark());

        try {
            repository.save(targetCategory.get());
            return true;

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return false;
        }

    }

    // 刪除多筆類別，如果使用者錯誤直接回傳false
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
                .filter(obj -> obj.getCategoryID().equals(uuid))
                .findFirst();

            if(tempCat.isPresent()){
                try {
                    repository.delete(tempCat.get());
                } catch (Exception e) {
                    if(e.getMessage().contains("ConstraintViolationException")){
                        errMsg.add(tempCat.get().getCategoryName() + " -> 有流水帳使用中，無法刪除");
                    }
                    else{
                        errMsg.add(tempCat.get().getCategoryName() + " -> 資料庫錯誤");
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
