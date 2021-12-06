package com.ubayKyu.accountingSystem.repository;

import com.ubayKyu.accountingSystem.entity.Accounting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingRepository extends JpaRepository<Accounting,Integer>{
    
}
