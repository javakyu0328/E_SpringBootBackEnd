package com.tel.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recommendation response
 * 추천 응답을 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponseDto {
    
    private Long movieId;
    private Integer recommendationCount;
    private boolean recommended;
    private String message;
    
    /**
     * Create a successful recommendation response
     * 성공적인 추천 응답 생성
     * 
     * @param movieId 영화 ID
     * @param recommendationCount 추천 수
     * @param recommended 추천 여부
     * @param action 수행된 작업 (추가 또는 취소)
     * @return RecommendationResponseDto
     */
    public static RecommendationResponseDto success(Long movieId, Integer recommendationCount, boolean recommended, String action) {
        return RecommendationResponseDto.builder()
                .movieId(movieId)
                .recommendationCount(recommendationCount)
                .recommended(recommended)
                .message("영화 추천이 " + action + "되었습니다.")
                .build();
    }
    
    /**
     * Create a recommendation response for adding a recommendation
     * 추천 추가에 대한 응답 생성
     * 
     * @param movieId 영화 ID
     * @param recommendationCount 추천 수
     * @return RecommendationResponseDto
     */
    public static RecommendationResponseDto added(Long movieId, Integer recommendationCount) {
        return success(movieId, recommendationCount, true, "추가");
    }
    
    /**
     * Create a recommendation response for removing a recommendation
     * 추천 취소에 대한 응답 생성
     * 
     * @param movieId 영화 ID
     * @param recommendationCount 추천 수
     * @return RecommendationResponseDto
     */
    public static RecommendationResponseDto removed(Long movieId, Integer recommendationCount) {
        return success(movieId, recommendationCount, false, "취소");
    }
}