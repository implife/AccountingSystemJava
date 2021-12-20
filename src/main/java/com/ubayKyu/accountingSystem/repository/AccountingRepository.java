package com.ubayKyu.accountingSystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.entity.UserInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountingRepository extends JpaRepository<Accounting,Integer>{
    
    List<Accounting> findByUserInfoOrderByCreateDateDesc(UserInfo userInfo);

    Page<Accounting> findByUserInfo(UserInfo userInfo, Pageable pageable);

    /**
     * 取得特定使用者的流水帳數量
     * 
     * @param userId must not be null
     * @return count of accountings, may be null
     */
    @Query(value = "SELECT COUNT(*) FROM accounting WHERE user_id = ?1", nativeQuery = true)
    Integer getCountByUserId(UUID userId);

    /**
     * 該使用者的所有帳目金額小記
     * 
     * @param userId must not be null
     * @return amount of all accountings, may be null when there's no accounting
     */
    @Query(value = "SELECT SUM(amount) FROM accounting WHERE user_id = ?1" , nativeQuery = true)
    Integer getTotalAmountByUserId(UUID userId);

    /**
     * 使用者的日期區間的帳目小記
     * 
     * @param userId must not be null
     * @param startDate must not be null, should be before endDate
     * @param endDate must not be null, should be after startDate 
     * @return amount of accountings in such time interval, may be null when there's no accounting
     */
    @Query(value = "SELECT SUM(amount) FROM accounting " +
        "WHERE user_id = ?1 " + 
        "AND create_date BETWEEN ?2 AND ?3" , nativeQuery = true)
    Integer getTotalAmountByUserIdAndDate(UUID userId, LocalDateTime startDate, LocalDateTime endDate);


    
}
