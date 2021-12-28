package com.ubayKyu.accountingSystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	

	public int getTotalCount(){
		return (int)userInfoRepository.count();
	}

	public Optional<UserInfo> getUserById(UUID uuid){
		if(uuid == null){
			return Optional.empty();
		}
		return userInfoRepository.findById(uuid);
	}

	public Optional<UserInfo> getUserByAccountPwd(String account, String pwd){
		return userInfoRepository.findAll()
			.stream()
			.filter(item -> item.getAccount().equals(account) && item.getPwd().equals(pwd))
			.findFirst();
	}

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

}
