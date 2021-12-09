package com.ubayKyu.accountingSystem.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserProfileController {
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/userProfile")
    public String userProfilePage(HttpSession session, Model model) {
        
        UUID uuid = (UUID)session.getAttribute("LoginID");

        if(uuid == null){
            return "redirect:/loginPage";
        }

        model.addAttribute("isManager", userInfoService.isManager(uuid));

        return "SystemAdmin/UserProfile";
    }


}
