package com.tel.member;

import com.tel.member.entity.MovieEntity;
import com.tel.member.entity.MovieRecommendationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for database schema and entity persistence
 * 데이터베이스 스키마 및 엔티티 영속성 통합 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
class DatabaseSchemaIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testMovieEntityPersistence() {
        // Given
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie")
                .genre("Action")
                .releaseDate("2024-01-01")
                .description("A test movie for integration testing")
                .posterUrl("https://example.com/test-poster.jpg")
                .recommendationCount(0)
                .build();

        // When
        MovieEntity savedMovie = entityManager.persistAndFlush(movie);

        // Then
        assertNotNull(savedMovie.getId());
        assertEquals("Test Movie", savedMovie.getTitle());
        assertEquals("Action", savedMovie.getGenre());
        assertEquals("2024-01-01", savedMovie.getReleaseDate());
        assertEquals("A test movie for integration testing", savedMovie.getDescription());
        assertEquals("https://example.com/test-poster.jpg", savedMovie.getPosterUrl());
        assertEquals(0, savedMovie.getRecommendationCount());
        assertNotNull(savedMovie.getCreatedAt());
        assertNotNull(savedMovie.getUpdatedAt());
    }

    @Test
    void testMovieRecommendationEntityPersistence() {
        // Given - First create and save a movie
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie for Recommendation")
                .genre("Drama")
                .releaseDate("2024-01-01")
                .description("A test movie")
                .recommendationCount(0)
                .build();
        
        MovieEntity savedMovie = entityManager.persistAndFlush(movie);

        // Given - Create recommendation
        MovieRecommendationEntity recommendation = MovieRecommendationEntity.builder()
                .movieId(savedMovie.getId())
                .memberId("testUser123")
                .build();

        // When
        MovieRecommendationEntity savedRecommendation = entityManager.persistAndFlush(recommendation);

        // Then
        assertNotNull(savedRecommendation.getId());
        assertEquals(savedMovie.getId(), savedRecommendation.getMovieId());
        assertEquals("testUser123", savedRecommendation.getMemberId());
        assertNotNull(savedRecommendation.getCreatedAt());
    }

    @Test
    void testUniqueConstraintOnMovieRecommendation() {
        // Given - Create and save a movie
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie for Unique Constraint")
                .genre("Comedy")
                .releaseDate("2024-01-01")
                .description("A test movie")
                .recommendationCount(0)
                .build();
        
        MovieEntity savedMovie = entityManager.persistAndFlush(movie);

        // Given - Create first recommendation
        MovieRecommendationEntity firstRecommendation = MovieRecommendationEntity.builder()
                .movieId(savedMovie.getId())
                .memberId("testUser123")
                .build();

        // When - Save first recommendation
        entityManager.persistAndFlush(firstRecommendation);

        // Given - Create duplicate recommendation
        MovieRecommendationEntity duplicateRecommendation = MovieRecommendationEntity.builder()
                .movieId(savedMovie.getId())
                .memberId("testUser123")
                .build();

        // Then - Should throw exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateRecommendation);
        });
    }

    @Test
    void testMovieEntityRecommendationCountMethods() {
        // Given
        MovieEntity movie = MovieEntity.builder()
                .title("Test Movie for Count Methods")
                .genre("Thriller")
                .releaseDate("2024-01-01")
                .description("A test movie")
                .recommendationCount(5)
                .build();

        MovieEntity savedMovie = entityManager.persistAndFlush(movie);

        // When - Increment recommendation count
        savedMovie.incrementRecommendationCount();
        entityManager.persistAndFlush(savedMovie);

        // Then
        MovieEntity updatedMovie = entityManager.find(MovieEntity.class, savedMovie.getId());
        assertEquals(6, updatedMovie.getRecommendationCount());

        // When - Decrement recommendation count
        updatedMovie.decrementRecommendationCount();
        entityManager.persistAndFlush(updatedMovie);

        // Then
        MovieEntity finalMovie = entityManager.find(MovieEntity.class, savedMovie.getId());
        assertEquals(5, finalMovie.getRecommendationCount());
    }

    @Test
    void testDatabaseTablesExist() {
        // Test that we can query the tables (this will fail if tables don't exist)
        
        // Test movies table
        Long movieCount = em.createQuery("SELECT COUNT(m) FROM MovieEntity m", Long.class)
                .getSingleResult();
        assertNotNull(movieCount);
        assertTrue(movieCount >= 0);

        // Test movie_recommendations table
        Long recommendationCount = em.createQuery("SELECT COUNT(r) FROM MovieRecommendationEntity r", Long.class)
                .getSingleResult();
        assertNotNull(recommendationCount);
        assertTrue(recommendationCount >= 0);
    }

    @Test
    void testMovieEntityWithNullValues() {
        // Given - Movie with minimal required fields
        MovieEntity movie = MovieEntity.builder()
                .title("Minimal Movie")
                .build();

        // When
        MovieEntity savedMovie = entityManager.persistAndFlush(movie);

        // Then
        assertNotNull(savedMovie.getId());
        assertEquals("Minimal Movie", savedMovie.getTitle());
        assertEquals(0, savedMovie.getRecommendationCount()); // Should default to 0
        assertNotNull(savedMovie.getCreatedAt());
        assertNotNull(savedMovie.getUpdatedAt());
    }
}