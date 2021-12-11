package com.ubayKyu.accountingSystem.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.dto.CategoryRowDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.CategoryService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {
    
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountingService accountingService;

    @GetMapping("/categoryList")
    public String CategoryListPage(@RequestParam(value = "page", required = false) String currentPageStr, Model model, HttpSession session) {

        // page參數
        int currentPage;
        try {
            currentPage = Integer.parseInt(currentPageStr);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        UUID uuid = (UUID)session.getAttribute("LoginID");

        if(uuid == null){
            return "redirect:/loginPage";
        }
        
        model.addAttribute("isManager", userInfoService.isManager(uuid));

        // 處理頁數
        int itemSize = 112;
        model.addAttribute("pagerObj", new PaginationDto("/categoryList", itemSize, currentPage));


        // 該使用者的分類做成CategoryRowDto的List
        List<CategoryRowDto> rows = categoryService.getCategoriesByUserID(uuid)
            .stream()
            .map(item -> new CategoryRowDto(item.createDate, item.categoryName,
                 accountingService.getCountOfCategory(item.categoryID), item.categoryID.toString()))
            .toList();

        model.addAttribute("categoryList", rows);

        return "SystemAdmin/CategoryList";
    }

}
