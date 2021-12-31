package com.ubayKyu.accountingSystem.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubayKyu.accountingSystem.dto.UserInputDto;
import com.ubayKyu.accountingSystem.dto.UserProfileDto;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.AccountingRepository;
import com.ubayKyu.accountingSystem.repository.CategoryRepository;
import com.ubayKyu.accountingSystem.repository.UserInfoRepository;

@Service
public class UserInfoService {
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private AccountingRepository accountingRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	// 所有使用者數量
	public int getTotalCount(){
		return (int)userInfoRepository.count();
	}

	// 利用UUID搜尋user
	public Optional<UserInfo> getUserById(UUID uuid){
		if(uuid == null){
			return Optional.empty();
		}
		return userInfoRepository.findById(uuid);
	}

	// 利用UUID字串搜尋user
	public Optional<UserInfo> getUserById(String uuidStr){
		if(uuidStr == null){
			return Optional.empty();
		}

        String pattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";

        // 檢查格式
        try {
            if(uuidStr.matches(pattern)){
                return this.getUserById(UUID.fromString(uuidStr));
            }
            else{
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
	}

	// 帳號密碼取得使用者
	public Optional<UserInfo> getUserByAccountPwd(String account, String pwd){
		return userInfoRepository.findByAccountAndPwd(account, pwd);
	}

	// 判斷是否為管理者
	public boolean isManager(UUID uuid) {
		return userInfoRepository.findById(uuid)
			.map(user -> user.getUserLevel() == 0)
			.orElse(false);
	}

    // 處理分頁
    public List<UserInfo> getUsersByPages(int currentPage, int sizeInPage) {
        return userInfoRepository
            .findAll(PageRequest.of(currentPage - 1, sizeInPage, Sort.by("name")))
            .getContent();
    }

	// 修改個人資料
	public boolean updateUserProfile(UserProfileDto userDto, List<String> errMsg){

        // check user
		if(userDto.getUserId() == null){
            errMsg.add("使用者錯誤");
            return false;
		}
        Optional<UserInfo> currentUser = this.getUserById(userDto.getUserId());
        if(currentUser.isEmpty()){
            errMsg.add("使用者錯誤");
            return false;
        }

		currentUser.get().setName(userDto.getName());
		currentUser.get().setEmail(userDto.getEmail());
		currentUser.get().setModifyDate(LocalDateTime.now());

        try {
            userInfoRepository.save(currentUser.get());
            return true;

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return false;
        }
	}

	/**
	 * 驗證使用者ID
	 * 
	 * @param ids 要驗證的id字串陣列
	 * @param errMsg 錯誤訊息List
	 * @return 驗證通過的ID換成UserInfo加入List回傳
	 */
    public List<UserInfo> checkUsers(String[] ids, List<String> errMsg) {
        String pattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";
		List<UserInfo> deleteList = new ArrayList<>();

        // 檢查格式
        for(String uuidStr: ids){
            try {
                if(uuidStr.matches(pattern)){
					userInfoRepository.findById(UUID.fromString(uuidStr))
						.ifPresentOrElse(deleteList::add, () -> {
							throw new IllegalArgumentException(uuidStr + " -> ID不存在");
						});
                }
                else{
                    throw new IllegalArgumentException(uuidStr + " -> 無法辨識ID");
                }
            } catch (IllegalArgumentException e) {
                errMsg.add(e.getMessage());
            }
        }

        return deleteList;
    }

	// 使用Transaction刪除一筆使用者資料
	@Transactional
	public boolean deleteUserTransaction(UserInfo userInfo){

		accountingRepository.deleteByUserInfo(userInfo);
		
		categoryRepository.deleteByUserInfo(userInfo);

		userInfoRepository.deleteById(userInfo.getUserID());

		return true;
	}

	/**
	 * 新增一筆使用者資料進資料庫
	 * @param userDto 新使用者的DTO
	 * @param errMsg 錯誤訊息的List
	 * @return 成功的話回傳新使用者UUID，失敗回傳null
	 */
	public UUID addUser(UserInputDto userDto, List<String> errMsg) {

        // 帳號不可一樣
        if(userInfoRepository.findByAccount(userDto.getAccount()).isPresent()){
            errMsg.add("*該帳號已存在");
            return null;
        }

        UserInfo newUser = new UserInfo();
		newUser.setAccount(userDto.getAccount());
		newUser.setName(userDto.getName());
		newUser.setPwd("12345678");
		newUser.setUserLevel(userDto.getLevel().equals("manager") ? 0 : 1);
		newUser.setEmail(userDto.getEmail());
		newUser.setModifyDate(LocalDateTime.now());

        try {

            return userInfoRepository.save(newUser).getUserID();

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return null;
        }
	}

    // 編輯使用者
	public boolean updateUser(UserInfo currentUser, UserInputDto userDto, List<String> errMsg) {
        // check userId
        Optional<UserInfo> targetUser = this.getUserById(userDto.getUserId());
        if(targetUser.isEmpty()){
            errMsg.add("該使用者不存在");
            return false;
        }

		// 管理者人數不可為0
		if(currentUser.getUserID().equals(userDto.getUserId()) && userDto.getLevel().equals("general")){
			if(userInfoRepository.getCountOfManager() == 1){
				errMsg.add("*管理者人數不可為0");
				return false;	
			}
		}

        targetUser.get().setName(userDto.getName());
        targetUser.get().setEmail(userDto.getEmail());
        targetUser.get().setUserLevel(userDto.getLevel().equals("manager") ? 0 : 1);
		targetUser.get().setModifyDate(LocalDateTime.now());

        try {
            userInfoRepository.save(targetUser.get());
            return true;

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return false;
        }
	}

}
