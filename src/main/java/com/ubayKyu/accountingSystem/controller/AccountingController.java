package com.ubayKyu.accountingSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountingController {
    
    @RequestMapping("/accountingList")
    public String AccountingListPage() {
        

        return "SystemAdmin/AccountingList";
    }


}
