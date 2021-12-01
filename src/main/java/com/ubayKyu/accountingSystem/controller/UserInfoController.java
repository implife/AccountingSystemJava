package com.ubayKyu.accountingSystem.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubayKyu.accountingSystem.dto.User;
import com.ubayKyu.accountingSystem.dto.UserTest;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.service.UserInfoService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserInfoController {

	@Autowired
	private UserInfoService service;

	@GetMapping("/getall")
	public List<User> getAllUserInfo(){
		return Arrays.asList(
			new User("Jack", "123"),
			new User("Martin", "asdzxc"),
			new User("Tom", "12345678")
		);
	}

	@GetMapping("/getpara/{id}")
	public String getAllPara(@PathVariable String id){
		return "ID = " + id;
	}

	@PostMapping("/addUserInfo")
	public UserInfo addClient(@RequestBody UserInfo userInfo) {
		return service.saveUserInfo(userInfo);
	}

	@GetMapping("/userInfos")
	public List<UserInfo> findAllClient() {
		return service.getUserInfos();
	}
	
	@PostMapping("/login")
	public void login(@ModelAttribute User user) {
		System.out.println(user.name);
	}

	@PostMapping("/testpost")
	public String testpostShow(Model model, String name, @ModelAttribute User user, @ModelAttribute UserTest userT, @RequestParam(name="qqid", defaultValue = "1001-abcde") String QID) {
		model.addAttribute("message1", "Redirect. Name = " + user.name + ", pwd = " + user.Password);
		model.addAttribute("message2", "Redirect. Country = " + userT.getCountry() + ", age = " + userT.getAge() + ", Tname = " + userT.getName());
		model.addAttribute("message3", "QueryString. QID = " + QID + "<br>name=" + name);
		return "test/test123";
	}
}