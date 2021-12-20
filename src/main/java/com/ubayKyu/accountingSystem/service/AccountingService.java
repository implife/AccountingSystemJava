package com.ubayKyu.accountingSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.AccountingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {
    @Autowired
    private AccountingRepository repository;
    @Autowired
    private UserInfoService userInfoService;

    public int getTotalCount(){
        return (int)repository.count();
    }

    public List<Accounting> getAll(){
        return repository.findAll(Sort.by("createDate"));
    }

    public List<Accounting> getAccountingsByUserId(UUID userId) {
        Optional<UserInfo> userInfo = userInfoService.getUserById(userId);

        // check userInfo
        if(userInfo.isEmpty()){
            return new ArrayList<Accounting>();
        }

        return repository.findByUserInfoOrderByCreateDateDesc(userInfo.get());
    }

    public int getAccountingsCountByUserId(UUID userId) {
        if(userId == null){
            return 0;
        }

        Integer result = repository.getCountByUserId(userId);
        return result == null ? 0 : result;

    }

    // 該使用者的所有帳目小記
    public int getTotalAmountByUserId(UUID userId){
        Integer result = repository.getTotalAmountByUserId(userId);
        return result == null ? 0 : result;
    }

    // 該使用者這個月的小記
    public int getTotalAmountThisMonthByUserId(UUID userId){
        LocalDate now = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getMonth().length(now.isLeapYear()), 23, 59, 59);

        Integer result = repository.getTotalAmountByUserIdAndDate(userId, start, end);
        return result == null ? 0 : result;
    }

    // 處理分頁
    public List<Accounting> getAccountingsByUserIdAndPages(UUID userId, int currentPage, int sizeInPage) {
        return repository
            .findByUserInfo(userInfoService.getUserById(userId).get(), PageRequest.of(currentPage - 1, sizeInPage, Direction.DESC, "createDate"))
            .getContent();
    }

    // 輸入category ID回傳流水帳數量
    public int getCountOfCategory(UUID uuid){
        return (int)repository.findAll()
            .stream()
            .filter(item -> Optional.ofNullable(item.getCategory())
                .map(obj -> obj.getCategoryID().equals(uuid))
                .orElse(false))
            .count();
    }
}
