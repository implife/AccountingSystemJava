package com.ubayKyu.accountingSystem.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultLoginController {
    
    @Autowired
    private AccountingService accountService;
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/")
    public String DefaultPage(Model model){
        List<Accounting> accountingAll = accountService.getAll();
        int accountSize = accountingAll.size();

        model.addAttribute("accountingTotalCount", accountSize);
        model.addAttribute("userTotalCount", userInfoService.getTotalCount());

        String first = accountingAll.get(0).createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        String last = accountingAll.get(accountSize - 1).createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

        model.addAttribute("firstAccount", first);
        model.addAttribute("lastAccount", last);


        return "Default";
    }

    @RequestMapping("/loginPage")
    public String LoginPage(Model model){
        return "login";
    }
    
    @RequestMapping("/login")
    public String Login(Model model){

        return "SystemAdmin/UserList";
    }

}
