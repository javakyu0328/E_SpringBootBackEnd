package com.tel.member.repository;

import com.tel.member.entity.MovieEntity;
import com.tel.member.entity.MovieRecommendationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MovieRecommendationRepository
 * MovieRecommendationRepository 테스트 클래스
 */
@DataJpaTest
@ActiveProfiles("test")
class MovieRecommendationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRecommendationRepository recommendationRepository;

    private MovieEntity testMovie1;
    private MovieEntity testMovie2;
    private MovieRecommendationEntity recommendation1;
    private MovieRecommendationEntity recommendation2;
    private MovieRecommendationEntity recommendation3;

    @BeforeEach
    void setUp() {
        // Create test movies
        testMovie1 = MovieEntity.builder()
                .title("Test Movie 1")
                .genre("Action")
                .releaseDate("2024-01-01")
                .description("Test movie 1")
                .recommendationCount(0)
                .build();

        testMovie2 = MovieEntity.builder()
                .title("Test Movie 2")
                .genre("Drama")
                .releaseDate("2024-01-02")
                .description("Test movie 2")
                .recommendationCount(0)
                .build();

        testMovie1 = entityManager.persistAndFlush(testMovie1);
        testMovie2 = entityManager.persistAndFlush(testMovie2);

        // Create test recommendations
        recommendation1 = MovieRecommendationEntity.builder()
                .movieId(testMovie1.getId())
                .memberId("user1")
                .build();

        recommendation2 = MovieRecommendationEntity.builder()
                .movieId(testMovie1.getId())
                .memberId("user2")
                .build();

        recommendation3 = MovieRecommendationEntity.builder()
                .movieId(testMovie2.getId())
                .memberId("user1")
                .build();

        recommendation1 = entityManager.persistAndFlush(recommendation1);
        recommendation2 = entityManager.persistAndFlush(recommendation2);
        recommendation3 = entityManager.persistAndFlush(recommendation3);
    }

    @Test
    void testFindByMovieIdAndMemberId() {
        // When
        Optional<MovieRecommendationEntity> result = 
                recommendationRepository.findByMovieIdAndMemberId(testMovie1.getId(), "user1");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMovie1.getId(), result.get().getMovieId());
        assertEquals("user1", result.get().getMemberId());
    }

    @Test
    void testExistsByMovieIdAndMemberId() {
        // When & Then
        assertTrue(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user1"));
        assertTrue(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user2"));
        assertFalse(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user3"));
    }

    @Test
    void testFindByMemberId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieRecommendationEntity> result = recommendationRepository.findByMemberId("user1", pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(rec -> rec.getMemberId().equals("user1")));
    }

    @Test
    void testFindByMovieId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieRecommendationEntity> result = recommendationRepository.findByMovieId(testMovie1.getId(), pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(rec -> rec.getMovieId().equals(testMovie1.getId())));
    }

    @Test
    void testCountByMovieId() {
        // When
        long count1 = recommendationRepository.countByMovieId(testMovie1.getId());
        long count2 = recommendationRepository.countByMovieId(testMovie2.getId());

        // Then
        assertEquals(2, count1);
        assertEquals(1, count2);
    }

    @Test
    void testCountByMemberId() {
        // When
        long count1 = recommendationRepository.countByMemberId("user1");
        long count2 = recommendationRepository.countByMemberId("user2");

        // Then
        assertEquals(2, count1);
        assertEquals(1, count2);
    }

    @Test
    void testFindMemberIdsByMovieId() {
        // When
        List<String> memberIds = recommendationRepository.findMemberIdsByMovieId(testMovie1.getId());

        // Then
        assertEquals(2, memberIds.size());
        assertTrue(memberIds.contains("user1"));
        assertTrue(memberIds.contains("user2"));
    }

    @Test
    void testFindMovieIdsByMemberId() {
        // When
        List<Long> movieIds = recommendationRepository.findMovieIdsByMemberId("user1");

        // Then
        assertEquals(2, movieIds.size());
        assertTrue(movieIds.contains(testMovie1.getId()));
        assertTrue(movieIds.contains(testMovie2.getId()));
    }

    @Test
    void testFindTopRecommendedMovies() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        List<Object[]> result = recommendationRepository.findTopRecommendedMovies(pageable);

        // Then
        assertFalse(result.isEmpty());
        // First result should be testMovie1 with 2 recommendations
        Object[] topMovie = result.get(0);
        assertEquals(testMovie1.getId(), topMovie[0]);
        assertEquals(2L, topMovie[1]);
    }

    @Test
    void testFindByMemberIdOrderByCreatedAtDesc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieRecommendationEntity> result = 
                recommendationRepository.findByMemberIdOrderByCreatedAtDesc("user1", pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(rec -> rec.getMemberId().equals("user1")));
    }

    @Test
    void testFindByMovieIdOrderByCreatedAtDesc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieRecommendationEntity> result = 
                recommendationRepository.findByMovieIdOrderByCreatedAtDesc(testMovie1.getId(), pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(rec -> rec.getMovieId().equals(testMovie1.getId())));
    }

    @Test
    void testDeleteByMovieIdAndMemberId() {
        // Given
        assertTrue(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user1"));

        // When
        recommendationRepository.deleteByMovieIdAndMemberId(testMovie1.getId(), "user1");
        entityManager.flush();

        // Then
        assertFalse(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user1"));
        // Other recommendations should still exist
        assertTrue(recommendationRepository.existsByMovieIdAndMemberId(testMovie1.getId(), "user2"));
    }

    @Test
    void testUniqueConstraint() {
        // Given - Try to create duplicate recommendation
        MovieRecommendationEntity duplicate = MovieRecommendationEntity.builder()
                .movieId(testMovie1.getId())
                .memberId("user1")
                .build();

        // When & Then - Should throw exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicate);
        });
    }
}