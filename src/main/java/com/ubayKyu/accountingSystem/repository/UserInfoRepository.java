package com.ubayKyu.accountingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubayKyu.accountingSystem.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer>{

}
