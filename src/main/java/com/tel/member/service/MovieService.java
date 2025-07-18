package com.tel.member.service;

import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.dto.MovieResponseDto;
import com.tel.member.dto.RecommendationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for movie operations
 * 영화 관련 서비스 인터페이스
 */
public interface MovieService {
    
    /**
     * Create a new movie
     * 새로운 영화 생성
     * 
     * @param request 영화 생성 요청 DTO
     * @return 생성된 영화 응답 DTO
     */
    MovieResponseDto createMovie(MovieCreateRequestDto request);
    
    /**
     * Get all movies with pagination
     * 모든 영화 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    Page<MovieResponseDto> getAllMovies(Pageable pageable, String memberId);
    
    /**
     * Get movies by genre with pagination
     * 장르별 영화 조회 (페이징)
     * 
     * @param genre 장르
     * @param pageable 페이징 정보
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    Page<MovieResponseDto> getMoviesByGenre(String genre, Pageable pageable, String memberId);
    
    /**
     * Search movies by keyword with pagination
     * 키워드로 영화 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    Page<MovieResponseDto> searchMovies(String keyword, Pageable pageable, String memberId);
    
    /**
     * Get recommended movies with pagination
     * 추천 수 기준 영화 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 페이지
     */
    Page<MovieResponseDto> getRecommendedMovies(Pageable pageable, String memberId);
    
    /**
     * Get top recommended movies
     * 추천 수 상위 영화 조회
     * 
     * @param limit 조회할 영화 수
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO 리스트
     */
    List<MovieResponseDto> getTopRecommendedMovies(int limit, String memberId);
    
    /**
     * Get movie by ID
     * ID로 영화 조회
     * 
     * @param id 영화 ID
     * @param memberId 현재 회원 ID (추천 여부 확인용)
     * @return 영화 응답 DTO
     */
    MovieResponseDto getMovieById(Long id, String memberId);
    
    /**
     * Toggle movie recommendation (add or remove)
     * 영화 추천 토글 (추가 또는 취소)
     * 
     * @param movieId 영화 ID
     * @param memberId 회원 ID
     * @return 추천 응답 DTO
     */
    RecommendationResponseDto toggleRecommendation(Long movieId, String memberId);
    
    /**
     * Check if movie is recommended by user
     * 사용자가 영화를 추천했는지 확인
     * 
     * @param movieId 영화 ID
     * @param memberId 회원 ID
     * @return 추천 여부
     */
    boolean isRecommendedByUser(Long movieId, String memberId);
    
    /**
     * Get all distinct genres
     * 모든 고유 장르 조회
     * 
     * @return 장르 리스트
     */
    List<String> getAllGenres();
}