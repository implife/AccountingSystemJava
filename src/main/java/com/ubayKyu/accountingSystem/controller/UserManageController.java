package com.ubayKyu.accountingSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserManageController {

    @RequestMapping("/userList")
    public String UserListPage() {
        

        return "SystemAdmin/UserList";
    }


}
