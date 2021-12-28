package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ubayKyu.accountingSystem.dto.CategoryInputDto;
import com.ubayKyu.accountingSystem.dto.CategoryRowDto;
import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.CategoryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {
    
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private HttpSession session;

    // categoryList頁面
    @GetMapping("/categoryList")
    public String categoryListPage(@RequestParam(value = "page", required = false) String currentPageStr,
        @ModelAttribute ErrorCountAndMessageDto errCountMsgDto, Model model) {

        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
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
        PaginationDto pagination = new PaginationDto("/categoryList", categoryService.getCategoriesCountByUserId(userId), currentPage);
        model.addAttribute("pagerObj", pagination);

        // 取得所在頁資料並做成CategoryRowDto的List
        List<CategoryRowDto> rows = categoryService
            .getCategoriesByUserIdAndPages(userId, pagination.getCurrentPage(), pagination.getItemSizeInPage())
            .stream()
            .map(item -> new CategoryRowDto(item.getCreateDate(), item.getCategoryName(),
                accountingService.getCountOfCategory(userId, item.getCategoryID()), item.getCategoryID().toString()))
            .toList();

        model.addAttribute("categoryList", rows);

        // 是否為刪除後重新導向回來
        model.addAttribute("isDeleteRedirect", !(errCountMsgDto.getSuccessCount() == 0 && errCountMsgDto.getFailedCount() == 0));

        return "SystemAdmin/CategoryList";
    }

    // categoryDetail頁面
    @GetMapping("/categoryDetail")
    public String categoryDetailPage(@ModelAttribute CategoryInputDto categoryDto, 
        @RequestParam(value = "CID", required = false) String categoryId, Model model){

        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(userId));

        // 如果categoryInputDtoBindingResult 存在表示驗證不過重新導向回來
        boolean hasBindingResult = model.containsAttribute("categoryInputDtoBindingResult");
        if(hasBindingResult){
            BindingResult result = (BindingResult)model.getAttribute("categoryInputDtoBindingResult");

            // 使用者或資料庫錯誤
            if(result.hasGlobalErrors()){
                model.addAttribute("databaseAddError", result.getGlobalError().getDefaultMessage());
            }

            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "categoryInputDto", result);
        }

        Optional<Category> editCategory = categoryService.getCategoryByCategoryIDStr(userId, categoryId);

        // 編輯模式
        if(editCategory.isPresent()){
            categoryDto.setCategoryId(editCategory.get().getCategoryID());
            categoryDto.setCaption(editCategory.get().getCategoryName());
            categoryDto.setRemark(editCategory.get().getRemark());
            model.addAttribute("formAction", "/editCategory");
        }
        // 新增模式
        else{
            if(categoryId != null){

                // URL有CID但該ID不存在，有BindingResult要顯示訊息，並改為新增模式
                if(hasBindingResult){
                    categoryDto.setCategoryId(null);
                    categoryDto.setCaption(null);
                    categoryDto.setRemark(null);
                }
                else{
                    return "redirect:/categoryDetail";
                }
            }
            model.addAttribute("formAction", "/newCategory");
        }

        return "SystemAdmin/CategoryDetail";
    }

    // 刪除多筆類別按鈕
    @PostMapping("/deleteCategories")
    public String deleteCategories(String[] delCheckBoxes, RedirectAttributes redirectAttr){
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }
        
        List<String> errMsg = new ArrayList<>();
        boolean userSuccess = categoryService.deleteCategories(delCheckBoxes, (UUID)session.getAttribute("LoginID"), errMsg);

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

        return "redirect:/categoryList";
    }

    // 新增一筆類別按鈕
    @PostMapping("/newCategory")
    public String newCategory(@Valid @ModelAttribute CategoryInputDto categoryDto, BindingResult result, RedirectAttributes redirectAttr){
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }
        
        // 驗證錯誤
        if(result.hasErrors()){
            redirectAttr.addFlashAttribute("categoryInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("categoryInputDto", categoryDto);

            return "redirect:/categoryDetail";
        }
        else{
            List<String> errMessage = new ArrayList<>();
            UUID newItemId = categoryService.addCategory((UUID)session.getAttribute("LoginID"), categoryDto, errMessage);
            
            // 新增錯誤
            if (newItemId == null) {
                if(errMessage.get(0).startsWith("*")){
                    result.rejectValue("caption", "categoryInputDto.caption.sameName", errMessage.get(0));
                }
                else{
                    // 使用Global Error
                    result.reject("error.SQLError", errMessage.get(0));
                }

                redirectAttr.addFlashAttribute("categoryInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("categoryInputDto", categoryDto); 

                return "redirect:/categoryDetail";  
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true); 
            redirectAttr.addFlashAttribute("successType", "新增"); 
            return "redirect:/categoryDetail?CID=" + newItemId.toString();
        }
    }

    // 修改類別按鈕
    @PostMapping("/editCategory")
    public String editCategory(@Valid @ModelAttribute CategoryInputDto categoryDto, BindingResult result, RedirectAttributes redirectAttr){
        // Check Login
        UUID userId = (UUID)session.getAttribute("LoginID");
        if(userInfoService.getUserById(userId).isEmpty()){
            return "redirect:/loginPage";
        }
        
        // categoryId不可為null (ID不合法Binding時也會變null)
        if(categoryDto.getCategoryId() == null){
            result.reject("error.categoryId.nullError", "類別ID錯誤");
        }
        
        // 驗證錯誤
        if(result.hasErrors()){
            redirectAttr.addFlashAttribute("categoryInputDtoBindingResult", result);
            redirectAttr.addFlashAttribute("categoryInputDto", categoryDto);

            return "redirect:/categoryDetail?CID=" + categoryDto.getCategoryId();
        }
        else{
            List<String> errMessage = new ArrayList<>();
            boolean isSuccess = categoryService.updateCategory((UUID)session.getAttribute("LoginID"), categoryDto, errMessage);
            
            // 新增錯誤
            if (!isSuccess) {
                if(errMessage.get(0).startsWith("*")){
                    result.rejectValue("caption", "categoryInputDto.caption.sameName", errMessage.get(0));
                }
                else{
                    // 使用Global Error
                    result.reject("error.SQLError", errMessage.get(0));
                }

                redirectAttr.addFlashAttribute("categoryInputDtoBindingResult", result);
                redirectAttr.addFlashAttribute("categoryInputDto", categoryDto); 

                return "redirect:/categoryDetail?CID=" + categoryDto.getCategoryId();  
            }

            // success
            redirectAttr.addFlashAttribute("isSuccessModalShow", true); 
            redirectAttr.addFlashAttribute("successType", "修改"); 
            return "redirect:/categoryDetail?CID=" + categoryDto.getCategoryId();
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        // 字串trim
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, "caption", trimmerEditor);
    }

}
