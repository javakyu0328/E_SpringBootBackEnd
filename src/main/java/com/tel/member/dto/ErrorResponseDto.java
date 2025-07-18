package com.tel.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for error response
 * 에러 응답을 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    
    private String code;
    private String message;
    private String path;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Create an error response
     * 에러 응답 생성
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param path 요청 경로
     * @return ErrorResponseDto
     */
    public static ErrorResponseDto of(String code, String message, String path) {
        return ErrorResponseDto.builder()
                .code(code)
                .message(message)
                .path(path)
                .build();
    }
}