package com.ubayKyu.accountingSystem.service;

import java.util.List;

import com.ubayKyu.accountingSystem.entity.Accounting;
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
}
