package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.dto.AccountingInputDto;
import com.ubayKyu.accountingSystem.dto.AccountingRowDto;
import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.CategoryService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountingController {
    
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private HttpSession session;

    // 流水帳列表頁
    @RequestMapping("/accountingList")
    public String AccountingListPage(@RequestParam(value = "page", required = false) String currentPageStr,
        @ModelAttribute ErrorCountAndMessageDto errCountMsgDto,  Model model) {
        
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
        List<AccountingRowDto> rows = accountingService
            .getAccountingsByUserIdAndPages(userId, pagination.getCurrentPage(), pagination.getItemSizeInPage())
            .stream()
            .map(item -> new AccountingRowDto(item.getId(), item.getCreateDate(), 
                item.getCategory() == null ? "無類別" : item.getCategory().getCategoryName(), 
                item.getActType() == 0 ? "支出" : "收入", item.getAmount(), item.getCaption()))
            .toList();
            
        model.addAttribute("accountingList", rows);

        // 小記
        model.addAttribute("totalAmount", accountingService.getTotalAmountByUserId(userId))
            .addAttribute("totalAmountThisMonth", accountingService.getTotalAmountThisMonthByUserId(userId));

        // 是否為刪除後重新導向回來
        model.addAttribute("isDeleteRedirect", !(errCountMsgDto.getSuccessCount() == 0 && errCountMsgDto.getFailedCount() == 0));

        return "SystemAdmin/AccountingList";
    }

    // 流水帳編輯頁
    @RequestMapping("/accountingDetail")
    public String accountingDetailPage(@ModelAttribute AccountingInputDto accountingInputDto, Model model) {
        
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userId == null){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(userId));

        // 類別下拉選單
        List<String> categoryDropList = categoryService
            .getCategoriesByUserID(userId)
            .stream()
            .map(item -> item.getCategoryName())
            .toList();
        model.addAttribute("categoryDropList", categoryDropList);


        return "SystemAdmin/AccountingDetail";
    }

    // 新增一筆流水帳按鈕
    @PostMapping("/newAccounting")
    public String newAccounting(@ModelAttribute AccountingInputDto accountingInputDto) {
        
        List<String> errMessage = new ArrayList<>();
        accountingService.addAccounting((UUID)session.getAttribute("LoginID"), accountingInputDto, errMessage);
        return "redirect:/accountingDetail";
    }

    // 刪除多筆流水帳按鈕
    @PostMapping("/deleteAccounting")
    public String deleteAccounting(String[] delCheckBoxes, RedirectAttributes redirectAttr) {

        List<String> errMsg = new ArrayList<>();
        boolean userSuccess = accountingService.deleteAccountings(delCheckBoxes, (UUID)session.getAttribute("LoginID"), errMsg);

        // 將結果做成ErrorCountAndMessageDto再重新導向
        ErrorCountAndMessageDto errCountAndMsg = new ErrorCountAndMessageDto();
        if(!userSuccess){
            errCountAndMsg.setSuccessCount(0);
            errCountAndMsg.setFailedCount(delCheckBoxes.length);
        }
        else{
            errCountAndMsg.setSuccessCount(delCheckBoxes.length - errMsg.size());
            errCountAndMsg.setFailedCount(errMsg.size());
        }

        errCountAndMsg.setErrMessages(errMsg);
        redirectAttr.addFlashAttribute("errorCountAndMessageDto", errCountAndMsg);


        return "redirect:/accountingList";
    }



}
