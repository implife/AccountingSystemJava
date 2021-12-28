package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.dto.UserRowDto;
import com.ubayKyu.accountingSystem.entity.UserInfo;
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
public class UserManageController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/userList")
    public String UserListPage(@RequestParam(value = "page", required = false) String currentPageStr,
        @ModelAttribute ErrorCountAndMessageDto errCountMsgDto, Model model) {
        
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            return "redirect:/loginPage";
        }
        else if(currentUser.get().getUserLevel() != 0){
            response.setStatus(403);
            return null;
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
        PaginationDto pagination = new PaginationDto("/userList", userInfoService.getTotalCount(), currentPage);
        model.addAttribute("pagerObj", pagination);

        // 取得所在頁資料並做成UserRowDto的List
        List<UserRowDto> rows = userInfoService
            .getUsersByPages(pagination.getCurrentPage(), pagination.getItemSizeInPage())
            .stream()
            .map(item -> new UserRowDto(item.getUserID().toString(), item.getAccount(), item.getName(), item.getEmail(),
                item.getUserLevel() == 0 ? "管理者" : "一般會員", item.getCreateDate()))
            .toList();

        model.addAttribute("userList", rows);
        model.addAttribute("currentUserId", userId.toString());

        // 是否為刪除後重新導向回來
        model.addAttribute("isDeleteRedirect", !(errCountMsgDto.getSuccessCount() == 0 && errCountMsgDto.getFailedCount() == 0));

        return "SystemAdmin/UserList";
    }

    // 刪除多筆使用者按鈕
    @PostMapping("/deleteUsers")
    public String deleteUsers(String[] delCheckBoxes, RedirectAttributes redirectAttr) {
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if(currentUser.isEmpty()){
            return "redirect:/loginPage";
        }
        else if(currentUser.get().getUserLevel() != 0){
            response.setStatus(403);
            return null;
        }

        List<String> errMsg = new ArrayList<>();
        List<UserInfo> deleteList = userInfoService.checkUsers(delCheckBoxes, errMsg);

        // 將結果做成ErrorCountAndMessageDto再重新導向
        ErrorCountAndMessageDto errCountAndMsg = new ErrorCountAndMessageDto();
        for(UserInfo userItem: deleteList){
            try {
                userInfoService.deleteUserTransaction(userItem);
            } catch (Exception e) {
                errMsg.add(userItem.getAccount() + " -> 資料庫錯誤");
            }
        }

        errCountAndMsg.setSuccessCount(delCheckBoxes.length - errMsg.size());
        errCountAndMsg.setFailedCount(errMsg.size());
        errCountAndMsg.setErrMessages(errMsg);
        
        redirectAttr.addFlashAttribute("errorCountAndMessageDto", errCountAndMsg);

        return "redirect:/userList";
    }
}
