package com.tel.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.dto.MovieResponseDto;
import com.tel.member.dto.RecommendationResponseDto;
import com.tel.member.exception.MovieNotFoundException;
import com.tel.member.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for MovieController
 * MovieController 테스트 클래스
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    private MovieResponseDto testMovieDto;
    private MovieCreateRequestDto createRequestDto;
    private final String TEST_MEMBER_ID = "testUser";

    @BeforeEach
    void setUp() {
        // Setup test movie response DTO
        testMovieDto = MovieResponseDto.builder()
                .id(1L)
                .title("Test Movie")
                .genre("Action")
                .releaseDate("2024-01-01")
                .description("Test description")
                .posterUrl("https://example.com/poster.jpg")
                .recommendationCount(5)
                .recommendedByCurrentUser(false)
                .createdAt(LocalDateTime.now())
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
    void createMovie_Success() throws Exception {
        // Given
        when(movieService.createMovie(any(MovieCreateRequestDto.class))).thenReturn(testMovieDto);

        // When & Then
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Movie")))
                .andExpect(jsonPath("$.genre", is("Action")))
                .andExpect(jsonPath("$.recommendationCount", is(5)));

        verify(movieService).createMovie(any(MovieCreateRequestDto.class));
    }

    @Test
    void createMovie_ValidationFailure() throws Exception {
        // Given
        MovieCreateRequestDto invalidDto = MovieCreateRequestDto.builder()
                .title("")  // Empty title should fail validation
                .genre("Drama")
                .build();

        // When & Then
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).createMovie(any(MovieCreateRequestDto.class));
    }

    @Test
    void getAllMovies() throws Exception {
        // Given
        List<MovieResponseDto> movies = Arrays.asList(testMovieDto);
        Page<MovieResponseDto> moviePage = new PageImpl<>(movies);
        
        when(movieService.getAllMovies(any(Pageable.class), anyString())).thenReturn(moviePage);

        // When & Then
        mockMvc.perform(get("/api/movies")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title")
                .param("direction", "asc")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Test Movie")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(movieService).getAllMovies(any(Pageable.class), eq(TEST_MEMBER_ID));
    }

    @Test
    void getMoviesByGenre() throws Exception {
        // Given
        List<MovieResponseDto> movies = Arrays.asList(testMovieDto);
        Page<MovieResponseDto> moviePage = new PageImpl<>(movies);
        
        when(movieService.getMoviesByGenre(eq("Action"), any(Pageable.class), anyString())).thenReturn(moviePage);

        // When & Then
        mockMvc.perform(get("/api/movies/genre/Action")
                .param("page", "0")
                .param("size", "10")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].genre", is("Action")));

        verify(movieService).getMoviesByGenre(eq("Action"), any(Pageable.class), eq(TEST_MEMBER_ID));
    }

    @Test
    void searchMovies() throws Exception {
        // Given
        List<MovieResponseDto> movies = Arrays.asList(testMovieDto);
        Page<MovieResponseDto> moviePage = new PageImpl<>(movies);
        
        when(movieService.searchMovies(eq("Test"), any(Pageable.class), anyString())).thenReturn(moviePage);

        // When & Then
        mockMvc.perform(get("/api/movies/search")
                .param("keyword", "Test")
                .param("page", "0")
                .param("size", "10")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Test Movie")));

        verify(movieService).searchMovies(eq("Test"), any(Pageable.class), eq(TEST_MEMBER_ID));
    }

    @Test
    void getRecommendedMovies() throws Exception {
        // Given
        List<MovieResponseDto> movies = Arrays.asList(testMovieDto);
        Page<MovieResponseDto> moviePage = new PageImpl<>(movies);
        
        when(movieService.getRecommendedMovies(any(Pageable.class), anyString())).thenReturn(moviePage);

        // When & Then
        mockMvc.perform(get("/api/movies/recommended")
                .param("page", "0")
                .param("size", "10")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].recommendationCount", is(5)));

        verify(movieService).getRecommendedMovies(any(Pageable.class), eq(TEST_MEMBER_ID));
    }

    @Test
    void getTopRecommendedMovies() throws Exception {
        // Given
        List<MovieResponseDto> movies = Arrays.asList(testMovieDto);
        
        when(movieService.getTopRecommendedMovies(anyInt(), anyString())).thenReturn(movies);

        // When & Then
        mockMvc.perform(get("/api/movies/top-recommended")
                .param("limit", "5")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Movie")));

        verify(movieService).getTopRecommendedMovies(eq(5), eq(TEST_MEMBER_ID));
    }

    @Test
    void getMovieById_Success() throws Exception {
        // Given
        when(movieService.getMovieById(eq(1L), anyString())).thenReturn(testMovieDto);

        // When & Then
        mockMvc.perform(get("/api/movies/1")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Movie")));

        verify(movieService).getMovieById(eq(1L), eq(TEST_MEMBER_ID));
    }

    @Test
    void getMovieById_NotFound() throws Exception {
        // Given
        when(movieService.getMovieById(eq(99L), anyString())).thenThrow(new MovieNotFoundException(99L));

        // When & Then
        mockMvc.perform(get("/api/movies/99")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isNotFound());

        verify(movieService).getMovieById(eq(99L), eq(TEST_MEMBER_ID));
    }

    @Test
    void getAllGenres() throws Exception {
        // Given
        List<String> genres = Arrays.asList("Action", "Drama", "Comedy");
        
        when(movieService.getAllGenres()).thenReturn(genres);

        // When & Then
        mockMvc.perform(get("/api/movies/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", containsInAnyOrder("Action", "Drama", "Comedy")));

        verify(movieService).getAllGenres();
    }
    
    @Test
    void recommendMovie() throws Exception {
        // Given
        RecommendationResponseDto recommendationDto = RecommendationResponseDto.builder()
                .movieId(1L)
                .recommendationCount(6)
                .recommended(true)
                .message("영화 추천이 추가되었습니다.")
                .build();
        
        when(movieService.toggleRecommendation(eq(1L), eq(TEST_MEMBER_ID))).thenReturn(recommendationDto);

        // When & Then
        mockMvc.perform(post("/api/movies/1/recommend")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId", is(1)))
                .andExpect(jsonPath("$.recommendationCount", is(6)))
                .andExpect(jsonPath("$.recommended", is(true)))
                .andExpect(jsonPath("$.message", containsString("추가")));

        verify(movieService).toggleRecommendation(eq(1L), eq(TEST_MEMBER_ID));
    }
    
    @Test
    void isMovieRecommendedByUser() throws Exception {
        // Given
        when(movieService.isRecommendedByUser(eq(1L), eq(TEST_MEMBER_ID))).thenReturn(true);
        when(movieService.isRecommendedByUser(eq(2L), eq(TEST_MEMBER_ID))).thenReturn(false);

        // When & Then - Movie is recommended
        mockMvc.perform(get("/api/movies/1/recommend/check")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // When & Then - Movie is not recommended
        mockMvc.perform(get("/api/movies/2/recommend/check")
                .param("memberId", TEST_MEMBER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(movieService).isRecommendedByUser(eq(1L), eq(TEST_MEMBER_ID));
        verify(movieService).isRecommendedByUser(eq(2L), eq(TEST_MEMBER_ID));
    }
}