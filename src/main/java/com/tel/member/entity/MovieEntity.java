package com.tel.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Movie Entity for storing movie information
 * 영화 정보를 저장하는 엔티티 클래스
 */
@Entity
@Table(name = "movies", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_genre", columnList = "genre"),
    @Index(name = "idx_recommendation_count", columnList = "recommendationCount"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 100)
    private String genre;
    
    @Column(name = "release_date", length = 20)
    private String releaseDate;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "poster_url", length = 500)
    private String posterUrl;
    
    @Column(name = "recommendation_count", columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer recommendationCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Increment recommendation count
     * 추천 수 증가
     */
    public void incrementRecommendationCount() {
        this.recommendationCount = (this.recommendationCount == null ? 0 : this.recommendationCount) + 1;
    }
    
    /**
     * Decrement recommendation count
     * 추천 수 감소
     */
    public void decrementRecommendationCount() {
        this.recommendationCount = Math.max(0, (this.recommendationCount == null ? 0 : this.recommendationCount) - 1);
    }
}