package com.tel.member.service;

import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.dto.MovieResponseDto;
import com.tel.member.dto.RecommendationResponseDto;
import com.tel.member.entity.MovieEntity;
import com.tel.member.entity.MovieRecommendationEntity;
import com.tel.member.exception.MovieNotFoundException;
import com.tel.member.repository.MovieRecommendationRepository;
import com.tel.member.repository.MovieRepository;
import com.tel.member.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for MovieService
 * MovieService 테스트 클래스
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieRecommendationRepository recommendationRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private MovieEntity testMovie;
    private MovieCreateRequestDto createRequestDto;
    private final String TEST_MEMBER_ID = "testUser";

    @BeforeEach
    void setUp() {
        // Setup test movie entity
        testMovie = MovieEntity.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .releaseDate("2024-01-01")
                .description("Test description")
                .posterUrl("https://example.com/poster.jpg")
                .recommendationCount(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup create request DTO
        createRequestDto = MovieCreateRequestDto.builder()
                .title("New Movie")
                .genre("Drama")
                .releaseDate("2024-02-01")
                .description("New movie description")
                .posterUrl("https://example.com/new-poster.jpg")
                .build();
    }

    @Test
    void createMovie_Success() {
        // Given
        when(movieRepository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> {
            MovieEntity savedMovie = invocation.getArgument(0);
            savedMovie.setId(1L);
            savedMovie.setCreatedAt(LocalDateTime.now());
            savedMovie.setUpdatedAt(LocalDateTime.now());
            return savedMovie;
        });

        // When
        MovieResponseDto result = movieService.createMovie(createRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(createRequestDto.getTitle(), result.getTitle());
        assertEquals(createRequestDto.getGenre(), result.getGenre());
        assertEquals(0, result.getRecommendationCount());
        verify(movieRepository).save(any(MovieEntity.class));
    }

    @Test
    void createMovie_DuplicateTitle() {
        // Given
        when(movieRepository.existsByTitleIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> movieService.createMovie(createRequestDto));
        verify(movieRepository, never()).save(any(MovieEntity.class));
    }

    @Test
    void getAllMovies() {
        // Given
        List<MovieEntity> movies = Arrays.asList(testMovie);
        Page<MovieEntity> moviePage = new PageImpl<>(movies);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(movieRepository.findAll(pageable)).thenReturn(moviePage);
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(false);

        // When
        Page<MovieResponseDto> result = movieService.getAllMovies(pageable, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMovie.getTitle(), result.getContent().get(0).getTitle());
        verify(movieRepository).findAll(pageable);
    }

    @Test
    void getMoviesByGenre() {
        // Given
        List<MovieEntity> movies = Arrays.asList(testMovie);
        Page<MovieEntity> moviePage = new PageImpl<>(movies);
        Pageable pageable = PageRequest.of(0, 10);
        String genre = "Action";
        
        when(movieRepository.findByGenreContainingIgnoreCase(genre, pageable)).thenReturn(moviePage);
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(false);

        // When
        Page<MovieResponseDto> result = movieService.getMoviesByGenre(genre, pageable, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMovie.getTitle(), result.getContent().get(0).getTitle());
        verify(movieRepository).findByGenreContainingIgnoreCase(genre, pageable);
    }

    @Test
    void searchMovies() {
        // Given
        List<MovieEntity> movies = Arrays.asList(testMovie);
        Page<MovieEntity> moviePage = new PageImpl<>(movies);
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "Test";
        
        when(movieRepository.findByTitleOrGenreContaining(keyword, pageable)).thenReturn(moviePage);
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(false);

        // When
        Page<MovieResponseDto> result = movieService.searchMovies(keyword, pageable, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMovie.getTitle(), result.getContent().get(0).getTitle());
        verify(movieRepository).findByTitleOrGenreContaining(keyword, pageable);
    }

    @Test
    void getRecommendedMovies() {
        // Given
        List<MovieEntity> movies = Arrays.asList(testMovie);
        Page<MovieEntity> moviePage = new PageImpl<>(movies);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(movieRepository.findAllByOrderByRecommendationCountDesc(pageable)).thenReturn(moviePage);
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(false);

        // When
        Page<MovieResponseDto> result = movieService.getRecommendedMovies(pageable, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testMovie.getTitle(), result.getContent().get(0).getTitle());
        verify(movieRepository).findAllByOrderByRecommendationCountDesc(pageable);
    }

    @Test
    void getTopRecommendedMovies() {
        // Given
        List<MovieEntity> movies = Arrays.asList(testMovie);
        int limit = 5;
        
        when(movieRepository.findTopMoviesByRecommendationCount(any(Pageable.class))).thenReturn(movies);
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(false);

        // When
        List<MovieResponseDto> result = movieService.getTopRecommendedMovies(limit, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMovie.getTitle(), result.get(0).getTitle());
        verify(movieRepository).findTopMoviesByRecommendationCount(any(Pageable.class));
    }

    @Test
    void getMovieById_Success() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(recommendationRepository.existsByMovieIdAndMemberId(anyLong(), anyString())).thenReturn(true);

        // When
        MovieResponseDto result = movieService.getMovieById(1L, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(testMovie.getId(), result.getId());
        assertEquals(testMovie.getTitle(), result.getTitle());
        assertTrue(result.isRecommendedByCurrentUser());
        verify(movieRepository).findById(1L);
    }

    @Test
    void getMovieById_NotFound() {
        // Given
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(99L, TEST_MEMBER_ID));
        verify(movieRepository).findById(99L);
    }

    @Test
    void toggleRecommendation_Add() {
        // Given
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(recommendationRepository.findByMovieIdAndMemberId(1L, TEST_MEMBER_ID)).thenReturn(Optional.empty());
        when(recommendationRepository.save(any(MovieRecommendationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(testMovie);

        // When
        RecommendationResponseDto result = movieService.toggleRecommendation(1L, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMovieId());
        assertTrue(result.isRecommended());
        assertEquals(6, testMovie.getRecommendationCount()); // Incremented from 5 to 6
        verify(recommendationRepository).save(any(MovieRecommendationEntity.class));
        verify(movieRepository).save(testMovie);
    }

    @Test
    void toggleRecommendation_Remove() {
        // Given
        MovieRecommendationEntity recommendation = MovieRecommendationEntity.builder()
                .id(1L)
                .movieId(1L)
                .memberId(TEST_MEMBER_ID)
                .build();
        
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(recommendationRepository.findByMovieIdAndMemberId(1L, TEST_MEMBER_ID)).thenReturn(Optional.of(recommendation));
        doNothing().when(recommendationRepository).delete(any(MovieRecommendationEntity.class));
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(testMovie);

        // When
        RecommendationResponseDto result = movieService.toggleRecommendation(1L, TEST_MEMBER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMovieId());
        assertFalse(result.isRecommended());
        assertEquals(4, testMovie.getRecommendationCount()); // Decremented from 5 to 4
        verify(recommendationRepository).delete(recommendation);
        verify(movieRepository).save(testMovie);
    }

    @Test
    void isRecommendedByUser() {
        // Given
        when(recommendationRepository.existsByMovieIdAndMemberId(1L, TEST_MEMBER_ID)).thenReturn(true);
        when(recommendationRepository.existsByMovieIdAndMemberId(2L, TEST_MEMBER_ID)).thenReturn(false);

        // When & Then
        assertTrue(movieService.isRecommendedByUser(1L, TEST_MEMBER_ID));
        assertFalse(movieService.isRecommendedByUser(2L, TEST_MEMBER_ID));
        
        // Null member ID should return false
        assertFalse(movieService.isRecommendedByUser(1L, null));
    }

    @Test
    void getAllGenres() {
        // Given
        List<String> genres = Arrays.asList("Action", "Drama", "Comedy");
        when(movieRepository.findAllDistinctGenres()).thenReturn(genres);

        // When
        List<String> result = movieService.getAllGenres();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Action"));
        assertTrue(result.contains("Drama"));
        assertTrue(result.contains("Comedy"));
        verify(movieRepository).findAllDistinctGenres();
    }
}