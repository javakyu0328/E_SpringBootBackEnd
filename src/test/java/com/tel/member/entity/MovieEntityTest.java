package com.tel.member.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MovieEntity and MovieRecommendationEntity
 * 영화 엔티티 및 추천 엔티티 테스트 클래스
 */
@SpringBootTest
@ActiveProfiles("test")
class MovieEntityTest {

    @Test
    void testMovieEntityCreation() {
        // Given
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie")
                .genre("Action")
                .releaseDate("2024-01-01")
                .description("Test movie description")
                .posterUrl("https://example.com/poster.jpg")
                .recommendationCount(0)
                .build();

        // Then
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("Action", movie.getGenre());
        assertEquals("2024-01-01", movie.getReleaseDate());
        assertEquals("Test movie description", movie.getDescription());
        assertEquals("https://example.com/poster.jpg", movie.getPosterUrl());
        assertEquals(0, movie.getRecommendationCount());
    }

    @Test
    void testMovieEntityRecommendationCountMethods() {
        // Given
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie")
                .recommendationCount(5)
                .build();

        // When & Then - Increment
        movie.incrementRecommendationCount();
        assertEquals(6, movie.getRecommendationCount());

        // When & Then - Decrement
        movie.decrementRecommendationCount();
        assertEquals(5, movie.getRecommendationCount());

        // When & Then - Decrement to zero
        movie.setRecommendationCount(1);
        movie.decrementRecommendationCount();
        assertEquals(0, movie.getRecommendationCount());

        // When & Then - Decrement below zero (should stay at 0)
        movie.decrementRecommendationCount();
        assertEquals(0, movie.getRecommendationCount());
    }

    @Test
    void testMovieEntityRecommendationCountWithNullValue() {
        // Given
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie")
                .recommendationCount(null)
                .build();

        // When & Then - Increment from null
        movie.incrementRecommendationCount();
        assertEquals(1, movie.getRecommendationCount());

        // When & Then - Decrement from null
        movie.setRecommendationCount(null);
        movie.decrementRecommendationCount();
        assertEquals(0, movie.getRecommendationCount());
    }

    @Test
    void testMovieRecommendationEntityCreation() {
        // Given
        MovieRecommendationEntity recommendation = MovieRecommendationEntity.builder()
                .movieId(1L)
                .memberId("testUser")
                .build();

        // Then
        assertNotNull(recommendation);
        assertEquals(1L, recommendation.getMovieId());
        assertEquals("testUser", recommendation.getMemberId());
    }

    @Test
    void testMovieRecommendationEntityStaticCreateMethod() {
        // When
        MovieRecommendationEntity recommendation = MovieRecommendationEntity.create(1L, "testUser");

        // Then
        assertNotNull(recommendation);
        assertEquals(1L, recommendation.getMovieId());
        assertEquals("testUser", recommendation.getMemberId());
    }
}