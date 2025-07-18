package com.tel.member.exception;

import com.tel.member.controller.MovieController;
import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for GlobalExceptionHandler
 * GlobalExceptionHandler 테스트 클래스
 */
@WebMvcTest(MovieController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void handleMovieNotFoundException() throws Exception {
        // Given
        when(movieService.getMovieById(eq(99L), anyString()))
                .thenThrow(new MovieNotFoundException(99L));

        // When & Then
        mockMvc.perform(get("/api/movies/99")
                .param("memberId", "testUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("MOVIE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("영화를 찾을 수 없습니다")));
    }

    @Test
    void handleDuplicateRecommendationException() throws Exception {
        // Given
        when(movieService.toggleRecommendation(eq(1L), eq("testUser")))
                .thenThrow(new DuplicateRecommendationException(1L, "testUser"));

        // When & Then
        mockMvc.perform(post("/api/movies/1/recommend")
                .param("memberId", "testUser"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("DUPLICATE_RECOMMENDATION")))
                .andExpect(jsonPath("$.message", containsString("이미 추천한 영화입니다")));
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        // Given
        MovieCreateRequestDto requestDto = MovieCreateRequestDto.builder()
                .title("Existing Movie")
                .genre("Action")
                .build();

        when(movieService.createMovie(any(MovieCreateRequestDto.class)))
                .thenThrow(new IllegalArgumentException("동일한 제목의 영화가 이미 존재합니다: Existing Movie"));

        // When & Then
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Existing Movie\",\"genre\":\"Action\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_ARGUMENT")))
                .andExpect(jsonPath("$.message", containsString("동일한 제목의 영화가 이미 존재합니다")));
    }

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        // When & Then - Empty title should fail validation
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"genre\":\"Action\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", containsString("입력값 검증에 실패했습니다")));
    }

    @Test
    void handleMissingServletRequestParameterException() throws Exception {
        // When & Then - Missing required memberId parameter
        mockMvc.perform(post("/api/movies/1/recommend"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("MISSING_PARAMETER")))
                .andExpect(jsonPath("$.message", containsString("필수 파라미터 누락")));
    }

    @Test
    void handleMethodArgumentTypeMismatchException() throws Exception {
        // When & Then - Invalid movie ID (not a number)
        mockMvc.perform(get("/api/movies/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("TYPE_MISMATCH")))
                .andExpect(jsonPath("$.message", containsString("타입 불일치")));
    }
}