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
                .allowedOrigins("http://localhost:8081")  // Vue 개발서버 주소 입력
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용 메서드
                .allowCredentials(true);  // 쿠키나 인증정보 허용 시 true
    }
}
