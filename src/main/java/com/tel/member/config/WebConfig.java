package com.tel.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS 정책 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")   // /api/ 로 시작하는 경로에 CORS 적용
                .allowedOrigins("http://localhost:8082", "http://localhost:3000")  // 특정 오리진만 허용 8082:VUE 3000:REACT
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용 메서드
                .allowCredentials(true);  // 쿠키 포함 요청 허용
    }
}
