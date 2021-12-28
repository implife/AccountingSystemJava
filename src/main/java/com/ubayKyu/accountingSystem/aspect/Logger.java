package com.ubayKyu.accountingSystem.aspect;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.ubayKyu.accountingSystem.entity.UserInfo;
import com.ubayKyu.accountingSystem.service.UserInfoService;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Logger {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private HttpSession session;
    
    // 管理者刪除使用者時的log
    @AfterReturning(value = "execution(* com.ubayKyu.accountingSystem.service.UserInfoService.deleteUserTransaction(..)) && args(deleteUser)")
    public void logUserDeleteMessage(JoinPoint joinPoint, UserInfo deleteUser) {

        // session中取得現在使用者
        Optional<UserInfo> currentUser = userInfoService.getUserById((UUID)session.getAttribute("LoginID"));

        if(currentUser.isPresent() && deleteUser != null){

            // 路徑設為現在的working directory
            String logPath = Paths.get("", "deleteLog.log").toAbsolutePath().toString();

            try (FileWriter fw = new FileWriter(logPath, StandardCharsets.UTF_8, true)){

                fw.write("管理者 " + currentUser.get().getName() + " 於 " + LocalDate.now() + 
                    " 刪除使用者 " + deleteUser.getName() + "\r\n");

            } catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }

    }
}
