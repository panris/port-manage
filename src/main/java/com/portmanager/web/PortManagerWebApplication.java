package com.portmanager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Port Manage Web Application
 * 端口管理工具 - 网页版启动类
 */
@SpringBootApplication
@EnableScheduling
public class PortManagerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortManagerWebApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Port Manage Web Application Started!");
        System.out.println("Access: http://127.0.0.1:9527");
        System.out.println("========================================\n");
    }
}