package com.ubayKyu.accountingSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultLoginController {
    
    @RequestMapping("/")
    public String DefaultPage(Model model){
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
