package com.tel.member.repository;

import com.tel.member.entity.MovieEntity;
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
 * Test class for MovieRepository
 * MovieRepository 테스트 클래스
 */
@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    private MovieEntity testMovie1;
    private MovieEntity testMovie2;
    private MovieEntity testMovie3;

    @BeforeEach
    void setUp() {
        // Create test movies
        testMovie1 = MovieEntity.builder()
                .title("The Dark Knight")
                .genre("Action")
                .releaseDate("2008-07-18")
                .description("Batman fights the Joker")
                .posterUrl("https://example.com/dark-knight.jpg")
                .recommendationCount(10)
                .build();

        testMovie2 = MovieEntity.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .releaseDate("2010-07-16")
                .description("Dreams within dreams")
                .posterUrl("https://example.com/inception.jpg")
                .recommendationCount(8)
                .build();

        testMovie3 = MovieEntity.builder()
                .title("The Dark Knight Rises")
                .genre("Action")
                .releaseDate("2012-07-20")
                .description("Batman's final battle")
                .posterUrl("https://example.com/dark-knight-rises.jpg")
                .recommendationCount(6)
                .build();

        // Save test movies
        testMovie1 = entityManager.persistAndFlush(testMovie1);
        testMovie2 = entityManager.persistAndFlush(testMovie2);
        testMovie3 = entityManager.persistAndFlush(testMovie3);
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByTitleContainingIgnoreCase("dark", pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .anyMatch(movie -> movie.getTitle().equals("The Dark Knight")));
        assertTrue(result.getContent().stream()
                .anyMatch(movie -> movie.getTitle().equals("The Dark Knight Rises")));
    }

    @Test
    void testFindByGenre() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByGenre("Action", pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(movie -> movie.getGenre().equals("Action")));
    }

    @Test
    void testFindByTitleOrGenreContaining() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByTitleOrGenreContaining("sci", pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Inception", result.getContent().get(0).getTitle());
    }

    @Test
    void testFindAllByOrderByRecommendationCountDesc() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findAllByOrderByRecommendationCountDesc(pageable);

        // Then
        assertEquals(3, result.getTotalElements());
        List<MovieEntity> movies = result.getContent();
        assertEquals("The Dark Knight", movies.get(0).getTitle());
        assertEquals(10, movies.get(0).getRecommendationCount());
        assertEquals("Inception", movies.get(1).getTitle());
        assertEquals(8, movies.get(1).getRecommendationCount());
        assertEquals("The Dark Knight Rises", movies.get(2).getTitle());
        assertEquals(6, movies.get(2).getRecommendationCount());
    }

    @Test
    void testFindTopMoviesByRecommendationCount() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        List<MovieEntity> result = movieRepository.findTopMoviesByRecommendationCount(pageable);

        // Then
        assertEquals(2, result.size());
        assertEquals("The Dark Knight", result.get(0).getTitle());
        assertEquals("Inception", result.get(1).getTitle());
    }

    @Test
    void testFindAllDistinctGenres() {
        // When
        List<String> genres = movieRepository.findAllDistinctGenres();

        // Then
        assertEquals(2, genres.size());
        assertTrue(genres.contains("Action"));
        assertTrue(genres.contains("Sci-Fi"));
    }

    @Test
    void testCountByGenre() {
        // When
        long actionCount = movieRepository.countByGenre("Action");
        long sciFiCount = movieRepository.countByGenre("Sci-Fi");

        // Then
        assertEquals(2, actionCount);
        assertEquals(1, sciFiCount);
    }

    @Test
    void testExistsByTitleIgnoreCase() {
        // When & Then
        assertTrue(movieRepository.existsByTitleIgnoreCase("the dark knight"));
        assertTrue(movieRepository.existsByTitleIgnoreCase("THE DARK KNIGHT"));
        assertFalse(movieRepository.existsByTitleIgnoreCase("Non-existent Movie"));
    }

    @Test
    void testFindByTitleIgnoreCase() {
        // When
        Optional<MovieEntity> result = movieRepository.findByTitleIgnoreCase("the dark knight");

        // Then
        assertTrue(result.isPresent());
        assertEquals("The Dark Knight", result.get().getTitle());
    }

    @Test
    void testFindByRecommendationCountGreaterThanEqual() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByRecommendationCountGreaterThanEqual(8, pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(movie -> movie.getRecommendationCount() >= 8));
    }

    @Test
    void testFindByReleaseDate() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByReleaseDate("2008-07-18", pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("The Dark Knight", result.getContent().get(0).getTitle());
    }

    @Test
    void testFindByReleaseDateContaining() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<MovieEntity> result = movieRepository.findByReleaseDateContaining("2008", pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("The Dark Knight", result.getContent().get(0).getTitle());
    }
}