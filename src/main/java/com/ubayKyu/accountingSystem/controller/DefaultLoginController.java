package com.ubayKyu.accountingSystem.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ubayKyu.accountingSystem.dto.UserLoginDto;
import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultLoginController {

    @Autowired
    private AccountingService accountService;
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/")
    public String DefaultPage(Model model, HttpSession session) {

        // 處理最早與最晚時間
        LocalDateTime f = accountService.getFirstAccountingDateTime();
        LocalDateTime l = accountService.getLastAccountingDateTime();
        String first = "--", last = "--";

        if(f != null && l != null){
            first = f.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
            last = l.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        }
        
        model.addAttribute("accountingTotalCount", accountService.getTotalCount())
            .addAttribute("userTotalCount", userInfoService.getTotalCount())
            .addAttribute("firstAccount", first)
            .addAttribute("lastAccount", last);

        // check login
        boolean isLogin = session.getAttribute("LoginID") != null;
        model.addAttribute("isLogin", isLogin);

        return "Default";
    }

    @GetMapping("/loginPage")
    public String LoginPage(@ModelAttribute UserLoginDto userLogin, HttpSession session) {

        // check login
        if (session.getAttribute("LoginID") != null) {
            return "redirect:/userProfile";
        }

        return "Login";
    }

    @PostMapping("/login")
    public String Login(@Valid @ModelAttribute UserLoginDto userLogin, BindingResult result, HttpSession session) {

        // Check Input
        if (result.hasErrors()) {
            return "Login";
        }

        // Check Account & PWD
        Optional<UserInfo> user = userInfoService.getUserByAccountPwd(userLogin.getAccount(), userLogin.getPassword());
        if (user.isEmpty()) {
            result.rejectValue("password", "accountIncorrect", "帳號或密碼不正確");
            return "Login";
        }

        // 加入Session
        session.setAttribute("LoginID", user.get().getUserID());

        return "redirect:/userProfile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, "account", trimmerEditor);
    }

}
