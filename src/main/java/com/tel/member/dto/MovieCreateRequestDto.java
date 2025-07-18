package com.tel.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for movie creation request
 * 영화 생성 요청을 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieCreateRequestDto {
    
    @NotBlank(message = "영화 제목은 필수입니다")
    @Size(max = 255, message = "영화 제목은 255자를 초과할 수 없습니다")
    private String title;
    
    @Size(max = 100, message = "장르는 100자를 초과할 수 없습니다")
    private String genre;
    
    @Size(max = 20, message = "개봉일은 20자를 초과할 수 없습니다")
    private String releaseDate;
    
    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    private String description;
    
    @Size(max = 500, message = "포스터 URL은 500자를 초과할 수 없습니다")
    private String posterUrl;
}