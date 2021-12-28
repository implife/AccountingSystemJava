package com.ubayKyu.accountingSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ubayKyu.accountingSystem.dto.AccountingInputDto;
import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.AccountingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {
    @Autowired
    private AccountingRepository repository;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CategoryService categoryService;

    public int getTotalCount(){
        return (int)repository.count();
    }

    // 最早記帳時間
    public LocalDateTime getFirstAccountingDateTime(){
        return repository.getFirstAccountingDateTime();
    }

    // 最晚記帳時間
    public LocalDateTime getLastAccountingDateTime(){
        return repository.getLastAccountingDateTime();
    }

    public List<Accounting> getAccountingsByUserId(UUID userId) {
        Optional<UserInfo> userInfo = userInfoService.getUserById(userId);

        // check userInfo
        if(userInfo.isEmpty()){
            return new ArrayList<Accounting>();
        }

        return repository.findByUserInfoOrderByCreateDateDesc(userInfo.get());
    }

    // 使用userId(驗證用)和accountingId取得該流水帳
    public Optional<Accounting> getAccountingByIdAndUserId(UUID userId, int accountingId){
        if(userId == null || accountingId <= 0){
            return Optional.empty();
        }

        Optional<UserInfo> user = userInfoService.getUserById(userId);
        if(user.isEmpty()){
            return Optional.empty();
        }

        return repository.findByIdAndUserInfo(accountingId, user.get());
    }

    // 該使用者的流水帳數量
    public int getAccountingsCountByUserId(UUID userId) {
        if(userId == null){
            return 0;
        }

        return repository.getCountByUserId(userId).orElse(0);

    }

    // 該使用者的所有帳目小記
    public int getTotalAmountByUserId(UUID userId){
        if(userId == null){
            return 0;
        }

        return repository.getTotalAmountByUserId(userId).orElse(0);
    }

    // 該使用者這個月的小記
    public int getTotalAmountThisMonthByUserId(UUID userId){
        if(userId == null){
            return 0;
        }

        LocalDate now = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getMonth().length(now.isLeapYear()), 23, 59, 59);

        return repository.getTotalAmountByUserIdAndDate(userId, start, end).orElse(0);
    }

    // 處理分頁
    public List<Accounting> getAccountingsByUserIdAndPages(UUID userId, int currentPage, int sizeInPage) {
        return repository
            .findByUserInfo(userInfoService.getUserById(userId).get(), PageRequest.of(currentPage - 1, sizeInPage, Direction.DESC, "createDate"))
            .getContent();
    }

    // 新增流水帳，失敗回傳-1
    public int addAccounting(UUID userId, AccountingInputDto accountingDto, List<String> errMsg) {
        
        // check user
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return -1;
        }

        Accounting newAccount = new Accounting();
        newAccount.setUserInfo(currentUser.get());
        newAccount.setCaption(accountingDto.getCaption());
        newAccount.setActType(accountingDto.getInout().equals("out") ? 0 : 1);
        newAccount.setCategory(categoryService.getCategoryByUserIdAndCategoryName(userId, accountingDto.getCategoryName()));
        newAccount.setAmount(accountingDto.getInout().equals("out") ? accountingDto.getAmount() * -1 : accountingDto.getAmount());
        newAccount.setRemark(accountingDto.getRemark());

        try {

            return repository.save(newAccount).getId();

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return -1;
        }
    }

    // 編輯流水帳
    public boolean updateAccounting(UUID userId, AccountingInputDto accountingDto, List<String> errMsg){
        
        // check user
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return false;
        }

        // check accountingId
        Optional<Accounting> targetAccounting = this.getAccountingByIdAndUserId(userId, accountingDto.getAccountingId());
        if(targetAccounting.isEmpty()){
            errMsg.add("該類別不存在");
            return false;
        }

        targetAccounting.get().setActType(accountingDto.getInout().equals("out") ? 0 : 1);
        targetAccounting.get().setCaption(accountingDto.getCaption());
        targetAccounting.get().setAmount(accountingDto.getInout().equals("out") ? accountingDto.getAmount() * -1 : accountingDto.getAmount());
        targetAccounting.get().setCategory(categoryService.getCategoryByUserIdAndCategoryName(userId, accountingDto.getCategoryName()));
        targetAccounting.get().setRemark(accountingDto.getRemark());

        try {
            repository.save(targetAccounting.get());
            return true;

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return false;
        }

    }

    // 輸入category ID回傳流水帳數量
    public int getCountOfCategory(UUID userId, UUID categoryId){

        if(userId == null || categoryId == null){
            return 0;
        }

        return repository.getAccountingCountByUserIdAndCategoryId(userId, categoryId).orElse(0);
    }

    // 刪除多筆流水帳資料
    public boolean deleteAccountings(String[] ids, UUID userId, List<String> errMsg) {
        // 使用者錯誤回傳false
        if(userId == null){
            errMsg.add("使用者錯誤");
            return false;
        }
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            errMsg.add("使用者不存在");
            return false;
        }

        List<Accounting> deleteList = new ArrayList<>();

        // 檢查格式並從資料庫尋找
        for(String id: ids){
            int convertResult;
            try {
                convertResult = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                errMsg.add(id + " -> ID無法辨識");
                continue;
            }

            Optional<Accounting> accoResult = repository.findByIdAndUserInfo(convertResult, currentUser.get());
            if(accoResult.isEmpty()){
                errMsg.add(id + " -> ID不存在");
                continue;
            }
            else{
                deleteList.add(accoResult.get());
            }
        }

        // 刪除資料
        for(Accounting accounting: deleteList){
            try {
                repository.delete(accounting);
            } catch (Exception e) {
                errMsg.add(accounting.getCaption() + " -> 資料庫錯誤");
            }
        }

        return true;
    }
}
