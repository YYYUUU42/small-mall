package com.yu.mall.main;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages ={"com.yu.mall.pay"})
@Configurable
@EnableScheduling
public class MallApplication {
	public static void main(String[] args) {
		SpringApplication.run(MallApplication.class);
	}
}
