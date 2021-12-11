package com.ubayKyu.accountingSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.repository.AccountingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {
    @Autowired
    private AccountingRepository repository;

    public int getTotalCount(){
        return repository.findAll().size();
    }

    public List<Accounting> getAll(){
        return repository.findAll(Sort.by("createDate"));
    }

    // 輸入category ID回傳流水帳數量
    public int getCountOfCategory(UUID uuid){
        return (int)repository.findAll()
            .stream()
            .filter(item -> Optional.ofNullable(item.category)
                .map(obj -> obj.categoryID.equals(uuid))
                .orElse(false))
            .count();
    }
}
