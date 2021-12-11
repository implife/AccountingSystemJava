package com.ubayKyu.accountingSystem;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class AccountingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountingSystemApplication.class, args);
	}

	// 引用外部錯誤訊息檔案
	@Bean
	public MessageSource messageSource(){
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBasename("messages");
		return messageSource;
	}

}
