package com.tel.member.controller;

import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.dto.MovieResponseDto;
import com.tel.member.dto.RecommendationResponseDto;
import com.tel.member.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for movie operations
 * 영화 관련 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;
    
    /**
     * Create a new movie
     * 새로운 영화 생성
     * 
     * @param request 영화 생성 요청 DTO
     * @return 생성된 영화 응답 DTO
     */
    @PostMapping
    public ResponseEntity<MovieResponseDto> createMovie(@Valid @RequestBody MovieCreateRequestDto request) {
        log.info("Creating new movie: {}", request.getTitle());
        MovieResponseDto createdMovie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }
    
    /**
     * Get all movies with pagination
     * 모든 영화 조회 (페이징)
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 기준 (title, genre, releaseDate, recommendationCount, createdAt)
     * @param direction 정렬 방향 (asc, desc)
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    @GetMapping
    public ResponseEntity<Page<MovieResponseDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String memberId) {
        
        log.info("Getting all movies - page: {}, size: {}, sort: {}, direction: {}", page, size, sort, direction);
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<MovieResponseDto> movies = movieService.getAllMovies(pageable, memberId);
        return ResponseEntity.ok(movies);
    }
    
    /**
     * Get movies by genre with pagination
     * 장르별 영화 조회 (페이징)
     * 
     * @param genre 장르
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<MovieResponseDto>> getMoviesByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String memberId) {
        
        log.info("Getting movies by genre: {}", genre);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDto> movies = movieService.getMoviesByGenre(genre, pageable, memberId);
        
        return ResponseEntity.ok(movies);
    }
    
    /**
     * Search movies by keyword with pagination
     * 키워드로 영화 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponseDto>> searchMovies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String memberId) {
        
        log.info("Searching movies with keyword: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDto> movies = movieService.searchMovies(keyword, pageable, memberId);
        
        return ResponseEntity.ok(movies);
    }
    
    /**
     * Get recommended movies with pagination
     * 추천 수 기준 영화 조회 (페이징)
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    @GetMapping("/recommended")
    public ResponseEntity<Page<MovieResponseDto>> getRecommendedMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String memberId) {
        
        log.info("Getting recommended movies");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDto> movies = movieService.getRecommendedMovies(pageable, memberId);
        
        return ResponseEntity.ok(movies);
    }
    
    /**
     * Get top recommended movies
     * 추천 수 상위 영화 조회
     * 
     * @param limit 조회할 영화 수
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 리스트
     */
    @GetMapping("/top-recommended")
    public ResponseEntity<List<MovieResponseDto>> getTopRecommendedMovies(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String memberId) {
        
        log.info("Getting top {} recommended movies", limit);
        
        List<MovieResponseDto> movies = movieService.getTopRecommendedMovies(limit, memberId);
        
        return ResponseEntity.ok(movies);
    }
    
    /**
     * Get movie by ID
     * ID로 영화 조회
     * 
     * @param id 영화 ID
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> getMovieById(
            @PathVariable Long id,
            @RequestParam(required = false) String memberId) {
        
        log.info("Getting movie by ID: {}", id);
        
        MovieResponseDto movie = movieService.getMovieById(id, memberId);
        
        return ResponseEntity.ok(movie);
    }
    
    /**
     * Get all distinct genres
     * 모든 고유 장르 조회
     * 
     * @return 장르 리스트
     */
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        log.info("Getting all distinct genres");
        
        List<String> genres = movieService.getAllGenres();
        
        return ResponseEntity.ok(genres);
    }
    
    /**
     * Toggle movie recommendation (add or remove)
     * 영화 추천 토글 (추가 또는 취소)
     * 
     * @param id 영화 ID
     * @param memberId 회원 ID
     * @return 추천 응답 DTO
     */
    @PostMapping("/{id}/recommend")
    public ResponseEntity<RecommendationResponseDto> recommendMovie(
            @PathVariable Long id,
            @RequestParam String memberId) {
        
        log.info("Toggling recommendation for movie ID: {} by member ID: {}", id, memberId);
        
        RecommendationResponseDto result = movieService.toggleRecommendation(id, memberId);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Check if movie is recommended by user
     * 사용자가 영화를 추천했는지 확인
     * 
     * @param id 영화 ID
     * @param memberId 회원 ID
     * @return 추천 여부
     */
    @GetMapping("/{id}/recommend/check")
    public ResponseEntity<Boolean> isMovieRecommendedByUser(
            @PathVariable Long id,
            @RequestParam String memberId) {
        
        log.info("Checking if movie ID: {} is recommended by member ID: {}", id, memberId);
        
        boolean isRecommended = movieService.isRecommendedByUser(id, memberId);
        
        return ResponseEntity.ok(isRecommended);
    }
}