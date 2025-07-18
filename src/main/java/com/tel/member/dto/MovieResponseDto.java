package com.tel.member.dto;

import com.tel.member.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for movie response
 * 영화 응답을 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDto {
    
    private Long id;
    private String title;
    private String genre;
    private String releaseDate;
    private String description;
    private String posterUrl;
    private Integer recommendationCount;
    private boolean recommendedByCurrentUser;
    private LocalDateTime createdAt;
    
    /**
     * Convert MovieEntity to MovieResponseDto
     * MovieEntity를 MovieResponseDto로 변환
     * 
     * @param entity MovieEntity
     * @return MovieResponseDto
     */
    public static MovieResponseDto fromEntity(MovieEntity entity) {
        return MovieResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .genre(entity.getGenre())
                .releaseDate(entity.getReleaseDate())
                .description(entity.getDescription())
                .posterUrl(entity.getPosterUrl())
                .recommendationCount(entity.getRecommendationCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    /**
     * Convert MovieEntity to MovieResponseDto with recommendation status
     * MovieEntity를 추천 상태를 포함한 MovieResponseDto로 변환
     * 
     * @param entity MovieEntity
     * @param recommendedByCurrentUser 현재 사용자의 추천 여부
     * @return MovieResponseDto
     */
    public static MovieResponseDto fromEntity(MovieEntity entity, boolean recommendedByCurrentUser) {
        MovieResponseDto dto = fromEntity(entity);
        dto.setRecommendedByCurrentUser(recommendedByCurrentUser);
        return dto;
    }
}