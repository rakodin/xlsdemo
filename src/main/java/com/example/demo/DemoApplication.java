package com.example.demo;

import com.example.demo.configuration.StorageProperties;
import com.example.demo.model.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(StorageService storageService) {
//		return (args) -> {
//			if (args != null && args[0].equals("clean")) {
//				storageService.deleteAll();
//				storageService.init();
//			}
//		};
//	}

	@Bean
	//force UTF-8 encoding
	public FilterRegistrationBean<CharacterEncodingFilter> filterRegistrationBean() {
		FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(
				new CharacterEncodingFilter
						(
								"UTF-8"
								,true
								,true
						)
		);
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}

