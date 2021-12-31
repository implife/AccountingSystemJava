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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;
    @Autowired
    private UserInfoService userInfoService;

    // 利用user ID取得該使用者的所有分類
    public List<Category> getCategoriesByUserID(UUID userId){
        if(userId == null){
            return new ArrayList<Category>();
        }
        return repository.findByUserInfo(userInfoService.getUserById(userId).get(), Sort.by(Direction.DESC, "createDate"));
    }

    // 使用userId和分類名稱取得該分類
    public Category getCategoryByUserIdAndCategoryName(UUID userId, String categoryName){
        if(userId == null || categoryName == null){
            return null;
        }

        Optional<UserInfo> userInfo = userInfoService.getUserById(userId);
        if(userInfo.isEmpty()){
            return null;
        }

        return repository.findByUserInfoAndCategoryName(userInfo.get(), categoryName);
    }

    // 取得該使用者的分類數量
    public int getCategoriesCountByUserId(UUID userId){
        if(userId == null){
            return 0;
        }

        return repository.getCountByUserId(userId).orElse(0);
    }

    // 利用使用者ID(驗證用)和分類ID取得該分類
    public Optional<Category> getCategoryByUserIdAndCategoryId(UUID userId, UUID categoryId){
        if(userId == null || categoryId == null){
            return Optional.empty();
        }
        
        return this.getCategoriesByUserID(userId)
            .stream()
            .filter(item -> item.getCategoryID().equals(categoryId))
            .findFirst();
    }

    // 利用使用者ID(驗證用)和categoryID(String)取得該分類
    public Optional<Category> getCategoryByUserIdAndCategoryId(UUID userId, String categoryIdStr){
        if(userId == null || categoryIdStr == null){
            return Optional.empty();
        }

        String pattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";

        // 檢查格式
        try {
            if(categoryIdStr.matches(pattern)){
                return this.getCategoryByUserIdAndCategoryId(userId, UUID.fromString(categoryIdStr));
            }
            else{
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 處理分頁
    public List<Category> getCategoriesByUserIdAndPages(UUID userId, int currentPage, int sizeInPage){
        return repository
            .findByUserInfo(userInfoService.getUserById(userId).get(), PageRequest.of(currentPage - 1, sizeInPage, Direction.DESC, "createDate"))
            .getContent();
    }

    // 新增一筆分類進資料庫，成功的話回傳該UUID
    public UUID addCategory(UUID userId, CategoryInputDto categoryDto, List<String> errMsg){

        // check user
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return null;
        }

        // 分類名稱不可一樣
        boolean hasTheSame = repository.findByUserInfoAndCategoryName(currentUser.get(), categoryDto.getCaption()) != null;
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
        Optional<Category> targetCategory = this.getCategoryByUserIdAndCategoryId(userId, categoryDto.getCategoryId());
        if(targetCategory.isEmpty()){
            errMsg.add("該分類不存在");
            return false;
        }

        // 分類名稱不可一樣
        if(!targetCategory.get().getCategoryName().equals(categoryDto.getCaption())){
            if(repository.findByUserInfoAndCategoryName(currentUser.get(), categoryDto.getCaption()) != null){
                errMsg.add("*該名稱已存在");
                return false;
            }
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

    // 刪除多筆分類，如果使用者錯誤直接回傳false
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
