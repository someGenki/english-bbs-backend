package com.yuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@Import({cn.hutool.extra.spring.SpringUtil.class})
public class EnglishBbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglishBbsApplication.class, args);
    }

}
