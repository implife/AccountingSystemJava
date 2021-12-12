package com.ubayKyu.accountingSystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.repository.UserInfoRepository;

@Service
public class UserInfoService {
	@Autowired
	private UserInfoRepository repository;
	

	public int getTotalCount(){
		return repository.findAll().size();
	}

	public Optional<UserInfo> getUserById(UUID uuid){
		return repository.findById(uuid);
	}

	public UserInfo getUserByAccountPwd(String account, String pwd){
		return repository.findAll()
			.stream()
			.filter(item -> item.account.equals(account) && item.pwd.equals(pwd))
			.findFirst()
			.orElse(null);
	}

	public boolean isManager(UUID uuid) {
		boolean isManager = repository.findById(uuid)
			.map(user -> user.userLevel == 0)
			.orElse(false);
		return isManager;
	}

}
