package com.ubayKyu.accountingSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CategoryController {
    
    @RequestMapping("/categoryList")
    public String CategoryListPage() {
        
        return "SystemAdmin/CategoryList";
    }

}
