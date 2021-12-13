package com.ubayKyu.accountingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.dto.CategoryInputDto;
import com.ubayKyu.accountingSystem.dto.CategoryRowDto;
import com.ubayKyu.accountingSystem.dto.ErrorCountAndMessageDto;
import com.ubayKyu.accountingSystem.dto.PaginationDto;
import com.ubayKyu.accountingSystem.entity.Category;
import com.ubayKyu.accountingSystem.service.AccountingService;
import com.ubayKyu.accountingSystem.service.CategoryService;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

        UUID uuid = (UUID)session.getAttribute("LoginID");
        if(uuid == null){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(uuid));

        // page參數
        int currentPage;
        try {
            currentPage = Integer.parseInt(currentPageStr);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        List<Category> rawData = categoryService.getCategoriesByUserID(uuid);

        // 處理頁數
        PaginationDto  pagination = new PaginationDto("/categoryList", rawData.size(), currentPage);
        model.addAttribute("pagerObj", pagination);


        // 該使用者的分類做成CategoryRowDto的List，並處理成所在頁
        List<CategoryRowDto> rows = rawData
            .stream()
            .skip((pagination.getCurrentPage() - 1) * pagination.getItemSizeInPage())
            .limit(pagination.getItemSizeInPage())
            .map(item -> new CategoryRowDto(item.createDate, item.categoryName,
                 accountingService.getCountOfCategory(item.categoryID), item.categoryID.toString()))
            .toList();

        model.addAttribute("categoryList", rows);

        // 是否為刪除後重新導向回來
        if(errCountMsgDto.getSuccessCount() == 0 && errCountMsgDto.getFailedCount() == 0){
            model.addAttribute("isDeleteRedirect", false);
        }
        else{
            model.addAttribute("isDeleteRedirect", true);
        }

        return "SystemAdmin/CategoryList";
    }

    // categoryDetail頁面
    @GetMapping("/categoryDetail")
    public String categoryDetailPage(@ModelAttribute CategoryInputDto categoryDto, 
        @RequestParam(value = "CID", required = false) String categoryID, Model model){

        UUID uuid = (UUID)session.getAttribute("LoginID");
        if(uuid == null){
            return "redirect:/loginPage";
        }
        model.addAttribute("isManager", userInfoService.isManager(uuid));

        // check CID
        if(categoryID != null){

        }
        // categoryDto.setCaption("test_caption");
        // categoryDto.setRemark("test_remark");
        model.addAttribute("formAction", "/newCategory");
        return "SystemAdmin/CategoryDetail";
    }

    // delete類別的按鈕
    @PostMapping("/deleteCategories")
    public String deleteCategories(String[] deleteItemsHidden, RedirectAttributes redirectAttr){
        List<String> errMsg = new ArrayList<>();
        boolean userSuccess = categoryService.deleteCategories(deleteItemsHidden, (UUID)session.getAttribute("LoginID"), errMsg);

        // 將結果做成ErrorCountAndMessageDto再重新導向
        ErrorCountAndMessageDto errCountAndMsg = new ErrorCountAndMessageDto();
        if(!userSuccess){
            errCountAndMsg.setSuccessCount(0);
            errCountAndMsg.setFailedCount(deleteItemsHidden.length);
        }
        else{
            errCountAndMsg.setSuccessCount(deleteItemsHidden.length - errMsg.size());
            errCountAndMsg.setFailedCount(errMsg.size());
        }

        errCountAndMsg.setErrMessages(errMsg);
        redirectAttr.addFlashAttribute("errorCountAndMessageDto", errCountAndMsg);

        return "redirect:/categoryList";
    }

    // 新增一筆類別按鈕
    @PostMapping("/newCategory")
    public String newCategory(@ModelAttribute CategoryInputDto categoryDto){
        categoryService.addCategory((UUID)session.getAttribute("LoginID"), categoryDto);
        return "redirect:/categoryDetail";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        StringArrayPropertyEditor stringArrayEditor = new StringArrayPropertyEditor();
        binder.registerCustomEditor(String[].class, "deleteItemsHidden", stringArrayEditor);
    }

}
