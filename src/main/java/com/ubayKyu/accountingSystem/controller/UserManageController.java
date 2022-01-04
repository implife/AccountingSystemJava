package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.dto.UserInputDto;
import com.ubayKyu.accountingSystem.dto.UserRowDto;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    // 會員列表頁
    @RequestMapping("/userList")
    public String UserListPage(@RequestParam(value = "page", required = false) String currentPageStr,
            @ModelAttribute ErrorCountAndMessageDto errCountMsgDto, Model model) {

        // Check Login
        UUID userId = (UUID) session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/loginPage";
        } else if (currentUser.get().getUserLevel() != 0) {
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
            .map(item -> new UserRowDto(item.getUserID().toString(), item.getAccount(), item.getName(),
                    item.getEmail(),
                    item.getUserLevel() == 0 ? "管理者" : "一般會員", item.getCreateDate()))
            .toList();

        model.addAttribute("userList", rows);
        model.addAttribute("currentUserId", userId.toString());

        // 是否為刪除後重新導向回來
        model.addAttribute("isDeleteRedirect",
                !(errCountMsgDto.getSuccessCount() == 0 && errCountMsgDto.getFailedCount() == 0));

        return "SystemAdmin/UserList";
    }

    // 刪除多筆使用者按鈕
    @PostMapping("/deleteUsers")
    public String deleteUsers(String[] delCheckBoxes, RedirectAttributes redirectAttr) {
        // Check Login
        UUID userId = (UUID) session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/loginPage";
        } else if (currentUser.get().getUserLevel() != 0) {
            response.setStatus(403);
            return null;
        }

        List<String> errMsg = new ArrayList<>();
        List<UserInfo> deleteList = userInfoService.checkUsers(delCheckBoxes, errMsg);

        // 將結果做成ErrorCountAndMessageDto再重新導向
        ErrorCountAndMessageDto errCountAndMsg = new ErrorCountAndMessageDto();
        for (UserInfo userItem : deleteList) {
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

    // userDetail頁面
    @RequestMapping("/userDetail")
    public String userDetailPage(@ModelAttribute UserInputDto userDto,
            @RequestParam(value = "UID", required = false) String modifiedUserId, Model model) {
        // Check Login
        UUID userId = (UUID) session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/loginPage";
        } else if (currentUser.get().getUserLevel() != 0) {
            return "redirect:/userProfile";
        }
        model.addAttribute("isManager", userInfoService.isManager(userId));

        // 如果userInputDtoBindingResult存在表示驗證不過重新導向回來
        boolean hasBindingResult = model.containsAttribute("userInputDtoBindingResult");
        if (hasBindingResult) {
            BindingResult result = (BindingResult) model.getAttribute("userInputDtoBindingResult");

            // 使用者或資料庫錯誤
            if (result.hasGlobalErrors()) {
                model.addAttribute("databaseAddError", result.getGlobalError().getDefaultMessage());
            }

            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "userInputDto", result);
        }

        Optional<UserInfo> editUser = userInfoService.getUserById(modifiedUserId);

        // 編輯模式
        if (editUser.isPresent()) {
            // 驗證不過導向回來要狀態還原而非填入DB資料
            if (!hasBindingResult) {
                userDto.setName(editUser.get().getName());
                userDto.setLevel(editUser.get().getUserLevel());
                userDto.setEmail(editUser.get().getEmail());
            }
            userDto.setUserId(editUser.get().getUserID());
            userDto.setAccount(editUser.get().getAccount());
            userDto.setCreateDate(editUser.get().getCreateDate());
            userDto.setModifyDate(editUser.get().getModifyDate());

            model.addAttribute("formAction", "/editUser");
        }
        // 新增模式
        else {
            if (modifiedUserId != null) {

                // URL有UID但該ID不存在，有BindingResult要顯示訊息，並改為新增模式
                if (hasBindingResult) {
                    userDto.setUserId(null);
                    userDto.setAccount(null);
                    userDto.setName(null);
                    userDto.setLevel("general");
                    userDto.setEmail(null);
                    userDto.setCreateDate("--");
                    userDto.setModifyDate("--");

                } else {
                    return "redirect:/userDetail";
                }
            }
            model.addAttribute("formAction", "/newUser");
        }

        return "SystemAdmin/UserDetail";
    }

    // 新增一筆使用者
    @PostMapping("/newUser")
    public String newUser(@Valid @ModelAttribute UserInputDto userDto, BindingResult result,
            RedirectAttributes redirectAttr) {
        // Check Login
        UUID userId = (UUID) session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/loginPage";
        } else if (currentUser.get().getUserLevel() != 0) {
            response.setStatus(403);
            return null;
        }

        // 驗證錯誤
        if (result.hasErrors()) {
            redirectAttr.addFlashAttribute("userInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("userInputDto", userDto);

            return "redirect:/userDetail";
        } else {
            List<String> errMessage = new ArrayList<>();
            UUID newItemId = userInfoService.addUser(userDto, errMessage);

            // 新增錯誤
            if (newItemId == null) {
                if (errMessage.get(0).startsWith("*")) {
                    result.rejectValue("account", "userInputDto.account.sameName", errMessage.get(0));
                } else {
                    // 使用Global Error
                    result.reject("error.SQLError", errMessage.get(0));
                }

                redirectAttr.addFlashAttribute("userInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("userInputDto", userDto);

                return "redirect:/userDetail";
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true);
            redirectAttr.addFlashAttribute("successType", "新增");
            return "redirect:/userDetail?UID=" + newItemId;
        }
    }

    @PostMapping("/editUser")
    public String editUser(@Valid @ModelAttribute UserInputDto userDto, BindingResult result,
            RedirectAttributes redirectAttr) {
        // Check Login
        UUID userId = (UUID) session.getAttribute("LoginID");
        Optional<UserInfo> currentUser = userInfoService.getUserById(userId);
        if (currentUser.isEmpty()) {
            return "redirect:/loginPage";
        } else if (currentUser.get().getUserLevel() != 0) {
            response.setStatus(403);
            return null;
        }

        // userId不可為null (ID不合法Binding時也會變null)
        if (userDto.getUserId() == null) {
            result.reject("error.userId.nullError", "使用者ID錯誤");
        }

        // 驗證錯誤
        if (result.hasErrors()) {

            // 編輯模式不用管account驗證
            if (result.hasFieldErrors("account") && result.getFieldErrorCount() == 1 && !result.hasGlobalErrors()) {
            } else {
                redirectAttr.addFlashAttribute("userInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("userInputDto", userDto);

                return "redirect:/userDetail?UID=" + userDto.getUserId();
            }
        }

        List<String> errMessage = new ArrayList<>();
        boolean isSuccess = userInfoService.updateUser(currentUser.get(), userDto, errMessage);

        // 新增錯誤
        if (!isSuccess) {
            if (errMessage.get(0).startsWith("*")) {
                result.rejectValue("level", "userInputDto.level.managerZero", errMessage.get(0));
            } else {
                // 使用Global Error
                result.reject("error.SQLError", errMessage.get(0));
            }

            redirectAttr.addFlashAttribute("userInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("userInputDto", userDto);

            return "redirect:/userDetail?UID=" + userDto.getUserId();
        }

        // success
        redirectAttr.addFlashAttribute("isSuccessModalShow", true);
        redirectAttr.addFlashAttribute("successType", "修改");
        return "redirect:/userDetail?UID=" + userDto.getUserId();
    }

}
