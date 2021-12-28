package com.ubayKyu.accountingSystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    Optional<Accounting> findByIdAndUserInfo(Integer accountingId, UserInfo userInfo);

    long deleteByUserInfo(UserInfo userInfo);

    /**
     * 取得最早的流水帳時間
     * 
     * @return may be null when there's no accountings.
     */
    @Query(value = "SELECT MIN(create_date) FROM accounting", nativeQuery = true)
    LocalDateTime getFirstAccountingDateTime();

    /**
     * 取得最晚的流水帳時間
     * 
     * @return may be null when there's no accountings.
     */
    @Query(value = "SELECT MAX(create_date) FROM accounting", nativeQuery = true)
    LocalDateTime getLastAccountingDateTime();

    /**
     * 取得特定使用者的流水帳數量
     * 
     * @param userId must not be null
     * @return count of accountings, may be null
     */
    @Query(value = "SELECT COUNT(*) FROM accounting WHERE user_id = ?1", nativeQuery = true)
    Optional<Integer> getCountByUserId(UUID userId);

    /**
     * 該使用者的所有帳目金額小記
     * 
     * @param userId must not be null
     * @return amount of all accountings, may be null when there's no accounting
     */
    @Query(value = "SELECT SUM(amount) FROM accounting WHERE user_id = ?1" , nativeQuery = true)
    Optional<Integer> getTotalAmountByUserId(UUID userId);

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
    Optional<Integer> getTotalAmountByUserIdAndDate(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 使用 user ID & category ID回傳流水帳數量
     * 
     * @param userId must not be null
     * @param categoryId must not be null
     * @return count of accountings, may be null when there's no matching accounting
     */
    @Query(value = "SELECT COUNT(*) FROM accounting " + 
        "WHERE user_id = ?1 AND category_id = ?2", nativeQuery = true)
    Optional<Integer> getAccountingCountByUserIdAndCategoryId(UUID userId, UUID categoryId);
    
}
