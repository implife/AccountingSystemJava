package com.ubayKyu.accountingSystem.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.dto.AccountingRowDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountingController {
    
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private HttpSession session;

    @RequestMapping("/accountingList")
    public String AccountingListPage(@RequestParam(value = "page", required = false) String currentPageStr, Model model) {
        
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userId == null){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(userId));

        // page參數
        int currentPage;
        try {
            currentPage = Integer.parseInt(currentPageStr);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        // 處理頁數
        PaginationDto pagination = new PaginationDto("/accountingList", accountingService.getAccountingsCountByUserId(userId), currentPage);
        model.addAttribute("pagerObj", pagination);

        // 取得所在頁資料並做成AccountingRowDto的List
        List<AccountingRowDto> rows = accountingService.getAccountingsByUserIdAndPages(userId, pagination.getCurrentPage(), pagination.getItemSizeInPage())
            .stream()
            .map(item -> new AccountingRowDto(item.getId(), item.getCreateDate(), 
                item.getCategory() == null ? "無類別" : item.getCategory().getCategoryName(), 
                item.getActType() == 0 ? "支出" : "收入", item.getAmount(), item.getRemark()))
            .toList();
            
        model.addAttribute("accountingList", rows);

        // 小記
        model.addAttribute("totalAmount", accountingService.getTotalAmountByUserId(userId))
            .addAttribute("totalAmountThisMonth", accountingService.getTotalAmountThisMonthByUserId(userId));

        return "SystemAdmin/AccountingList";
    }

    @RequestMapping("/accountingDetail")
    public String accountingDetailPage() {
        
        
        return "AccountingDetail";
    }


}
