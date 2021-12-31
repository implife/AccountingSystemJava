package com.ubayKyu.accountingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo,UUID>{

    Optional<UserInfo> findByAccount(String account);

    Optional<UserInfo> findByAccountAndPwd(String account, String pwd);

    /**
     * 取得管理者人數
     * 
     * @return 管理者人數
     */
    @Query(value = "SELECT COUNT(*) FROM user_info WHERE user_level = 0", nativeQuery = true)
    int getCountOfManager();
}
