package com.ubayKyu.accountingSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubayKyu.accountingSystem.dto.UserProfileDto;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.UserInfoRepository;

@Service
public class UserInfoService {
	@Autowired
	private UserInfoRepository repository;
	

	public int getTotalCount(){
		return (int)repository.count();
	}

	public Optional<UserInfo> getUserById(UUID uuid){
		return repository.findById(uuid);
	}

	public Optional<UserInfo> getUserByAccountPwd(String account, String pwd){
		return repository.findAll()
			.stream()
			.filter(item -> item.getAccount().equals(account) && item.getPwd().equals(pwd))
			.findFirst();
	}

	public boolean isManager(UUID uuid) {
		return repository.findById(uuid)
			.map(user -> user.getUserLevel() == 0)
			.orElse(false);
	}

	//修改個人資料
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
            repository.save(currentUser.get());
            return true;

        } catch (Exception e) {
            errMsg.add("資料庫錯誤");
            return false;
        }
	}

}
