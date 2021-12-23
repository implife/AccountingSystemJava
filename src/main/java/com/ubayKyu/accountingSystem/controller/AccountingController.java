package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ubayKyu.accountingSystem.dto.AccountingInputDto;
import com.ubayKyu.accountingSystem.dto.AccountingRowDto;
import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.entity.Accounting;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.CategoryService;
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
    public String accountingDetailPage(@ModelAttribute AccountingInputDto accountingInputDto,
        @RequestParam(value = "AID", required = false) String accountingIdStr, Model model) {
        
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

        // 如果accountingInputDtoBindingResult存在表示驗證不過重新導向回來
        boolean hasBindingResult = model.containsAttribute("accountingInputDtoBindingResult");
        if(hasBindingResult){
            BindingResult result = (BindingResult)model.getAttribute("accountingInputDtoBindingResult");

            // 使用者或資料庫錯誤
            if(result.hasGlobalErrors()){
                model.addAttribute("databaseAddError", result.getGlobalError().getDefaultMessage());
            }

            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "accountingInputDto", result);
        }

        // 處理AID
        int accountingId;
        try{
            accountingId = Integer.parseInt(accountingIdStr);
        }
        catch(NumberFormatException e){
            accountingId = -1;
        }

        Optional<Accounting> editAccounting = accountingService.getAccountingByIdAndUserId(userId, accountingId);

        // 編輯模式
        if(editAccounting.isPresent()){
            accountingInputDto.setAccountingId(editAccounting.get().getId());
            accountingInputDto.setCaption(editAccounting.get().getCaption());
            accountingInputDto.setAmount(Math.abs(editAccounting.get().getAmount()));
            accountingInputDto.setCategoryName(editAccounting.get().getCategory() == null ? null : editAccounting.get().getCategory().getCategoryName());
            accountingInputDto.setInout(editAccounting.get().getActType() == 0 ? "out" : "in");
            accountingInputDto.setRemark(editAccounting.get().getRemark());
            model.addAttribute("formAction", "/editAccounting");
        }
        // 新增模式
        else{
            if(accountingIdStr != null){

                // URL有CID但該ID不存在，有BindingResult要顯示訊息，並改為新增模式
                if(hasBindingResult){
                    accountingInputDto.setAccountingId(0);
                    accountingInputDto.setCaption(null);
                    accountingInputDto.setAmount(0);
                    accountingInputDto.setCategoryName(null);
                    accountingInputDto.setInout(null);
                    accountingInputDto.setRemark(null);
                }
                else{
                    return "redirect:/accountingDetail";
                }
            }
            model.addAttribute("formAction", "/newAccounting");
        }

        return "SystemAdmin/AccountingDetail";
    }

    // 新增一筆流水帳按鈕
    @PostMapping("/newAccounting")
    public String newAccounting(@Valid @ModelAttribute AccountingInputDto accountingInputDto, 
        BindingResult result, RedirectAttributes redirectAttr) {
        
        // 驗證錯誤
        if(result.hasErrors()){
            redirectAttr.addFlashAttribute("accountingInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("accountingInputDto", accountingInputDto);

            return "redirect:/accountingDetail";
        }
        else{
            List<String> errMessage = new ArrayList<>();
            int newItemId = accountingService.addAccounting((UUID)session.getAttribute("LoginID"), accountingInputDto, errMessage);
            
            // 新增錯誤
            if (newItemId == -1) {
                // 使用Global Error
                result.reject("error.SQLError", errMessage.get(0));

                redirectAttr.addFlashAttribute("accountingInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("accountingInputDto", accountingInputDto); 

                return "redirect:/accountingDetail";  
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true); 
            redirectAttr.addFlashAttribute("successType", "新增"); 
            return "redirect:/accountingDetail?AID=" + newItemId;
        }
    }

    @PostMapping("/editAccounting")
    public String editAccounting(@Valid @ModelAttribute AccountingInputDto accountingDto, BindingResult result, RedirectAttributes redirectAttr) {
        
        // 驗證錯誤
        if(result.hasErrors()){
            redirectAttr.addFlashAttribute("accountingInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("accountingInputDto", accountingDto);

            return "redirect:/accountingDetail?AID=" + accountingDto.getAccountingId();
        }
        else{
            List<String> errMessage = new ArrayList<>();
            boolean isSuccess = accountingService.updateAccounting((UUID)session.getAttribute("LoginID"), accountingDto, errMessage);
            
            // 新增錯誤
            if (!isSuccess) {
                // 使用Global Error
                result.reject("error.SQLError", errMessage.get(0));

                redirectAttr.addFlashAttribute("accountingInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("accountingInputDto", accountingDto); 

                return "redirect:/accountingDetail?AID=" + accountingDto.getAccountingId();  
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true); 
            redirectAttr.addFlashAttribute("successType", "修改"); 
            return "redirect:/accountingDetail?AID=" + accountingDto.getAccountingId();
        }
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
