package com.tel.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Movie Recommendation Entity for storing user movie recommendations
 * 사용자의 영화 추천 정보를 저장하는 엔티티 클래스
 */
@Entity
@Table(name = "movie_recommendations", 
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_recommendation", columnNames = {"movieId", "memberId"})
       },
       indexes = {
           @Index(name = "idx_movie_id", columnList = "movieId"),
           @Index(name = "idx_member_id", columnList = "memberId")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRecommendationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//id자동으로 생성
    private Long id;
    
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
    
    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;
    
    @CreationTimestamp//현재 시간을 자동으로 넣어줌
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Create a new movie recommendation
     * 새로운 영화 추천 생성
     * 
     * @param movieId 영화 ID
     * @param memberId 회원 ID
     * @return MovieRecommendationEntity
     */
    public static MovieRecommendationEntity create(Long movieId, String memberId) {
        return MovieRecommendationEntity.builder()
                .movieId(movieId)
                .memberId(memberId)
                .build();
    }
}