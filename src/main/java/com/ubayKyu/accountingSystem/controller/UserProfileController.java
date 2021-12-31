package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ubayKyu.accountingSystem.dto.UserProfileDto;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserProfileController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private HttpSession session;

    // userProfile頁面
    @RequestMapping("/userProfile")
    public String userProfilePage(@ModelAttribute UserProfileDto userProfileDto, Model model) {
        
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(userId));
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);

        // 如果userProfileDtoBindingResult存在表示驗證不過重新導向回來
        boolean hasBindingResult = model.containsAttribute("userProfileDtoBindingResult");
        if(hasBindingResult){
            BindingResult result = (BindingResult)model.getAttribute("userProfileDtoBindingResult");

            // 使用者或資料庫錯誤
            if(result.hasGlobalErrors()){
                model.addAttribute("databaseAddError", result.getGlobalError().getDefaultMessage());
            }

            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "userProfileDto", result);
        }
        else{
            userProfileDto.setName(currentUser.get().getName());
            userProfileDto.setEmail(currentUser.get().getEmail());
        }

        userProfileDto.setUserId(currentUser.get().getUserID());
        userProfileDto.setAccount(currentUser.get().getAccount());

        return "SystemAdmin/UserProfile";
    }

    // 儲存profile按鈕
    @PostMapping("/editUserProfile")
    public String editUserProfile(@Valid @ModelAttribute UserProfileDto userProfileDto, BindingResult result, 
        RedirectAttributes redirectAttr) {

        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }

        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);

        userProfileDto.setUserId(currentUser.get().getUserID());
        userProfileDto.setAccount(currentUser.get().getAccount());

        // 驗證錯誤
        if(result.hasErrors()){
            redirectAttr.addFlashAttribute("userProfileDtoBindingResult", result);
            redirectAttr.addFlashAttribute("userProfileDto", userProfileDto);

            return "redirect:/userProfile";
        }
        else{
            List<String> errMessage = new ArrayList<>();
            boolean isSuccess = userInfoService.updateUserProfile(userProfileDto, errMessage);
            
            // 使用者或資料庫錯誤
            if (!isSuccess) {
                // 使用Global Error
                result.reject("error.SQLError", errMessage.get(0));

                redirectAttr.addFlashAttribute("userProfileDtoBindingResult", result);
                redirectAttr.addFlashAttribute("userProfileDto", userProfileDto); 

                return "redirect:/userProfile";
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true); 
            redirectAttr.addFlashAttribute("successType", "修改"); 
            return "redirect:/userProfile";
        }
    }

}
