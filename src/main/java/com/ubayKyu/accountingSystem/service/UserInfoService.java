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

}
