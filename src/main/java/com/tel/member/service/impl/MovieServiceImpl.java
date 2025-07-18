package com.tel.member.service.impl;

import com.tel.member.dto.MovieCreateRequestDto;
import com.tel.member.dto.MovieResponseDto;
import com.tel.member.dto.RecommendationResponseDto;
import com.tel.member.entity.MovieEntity;
import com.tel.member.entity.MovieRecommendationEntity;
import com.tel.member.exception.DuplicateRecommendationException;
import com.tel.member.exception.MovieNotFoundException;
import com.tel.member.repository.MovieRecommendationRepository;
import com.tel.member.repository.MovieRepository;
import com.tel.member.service.MovieService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MovieService
 * MovieService 구현 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieRecommendationRepository recommendationRepository;

    @Override
    @Transactional
    public MovieResponseDto createMovie(MovieCreateRequestDto request) {
        log.info("Creating new movie: {}", request.getTitle());
        
        // Check if movie with same title already exists
        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            log.warn("Movie with title '{}' already exists", request.getTitle());
            throw new IllegalArgumentException("동일한 제목의 영화가 이미 존재합니다: " + request.getTitle());
        }
        
        // Create and save movie entity
        MovieEntity movieEntity = MovieEntity.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .releaseDate(request.getReleaseDate())
                .description(request.getDescription())
                .posterUrl(request.getPosterUrl())
                .recommendationCount(0)
                .build();
        
        MovieEntity savedMovie = movieRepository.save(movieEntity);
        log.info("Movie created successfully with ID: {}", savedMovie.getId());
        
        return MovieResponseDto.fromEntity(savedMovie);
    }

    @Override
    public Page<MovieResponseDto> getAllMovies(Pageable pageable, String memberId) {
        log.info("Getting all movies with pagination: {}", pageable);
        Page<MovieEntity> moviePage = movieRepository.findAll(pageable);
        
        return moviePage.map(movie -> convertToDto(movie, memberId));
    }

    @Override
    public Page<MovieResponseDto> getMoviesByGenre(String genre, Pageable pageable, String memberId) {
        log.info("Getting movies by genre: {}", genre);
        Page<MovieEntity> moviePage = movieRepository.findByGenreContainingIgnoreCase(genre, pageable);
        
        return moviePage.map(movie -> convertToDto(movie, memberId));
    }

    @Override
    public Page<MovieResponseDto> searchMovies(String keyword, Pageable pageable, String memberId) {
        log.info("Searching movies with keyword: {}", keyword);
        Page<MovieEntity> moviePage = movieRepository.findByTitleOrGenreContaining(keyword, pageable);
        
        return moviePage.map(movie -> convertToDto(movie, memberId));
    }

    @Override
    public Page<MovieResponseDto> getRecommendedMovies(Pageable pageable, String memberId) {
        log.info("Getting recommended movies");
        Page<MovieEntity> moviePage = movieRepository.findAllByOrderByRecommendationCountDesc(pageable);
        
        return moviePage.map(movie -> convertToDto(movie, memberId));
    }

    @Override
    public List<MovieResponseDto> getTopRecommendedMovies(int limit, String memberId) {
        log.info("Getting top {} recommended movies", limit);
        List<MovieEntity> movies = movieRepository.findTopMoviesByRecommendationCount(PageRequest.of(0, limit));
        
        return movies.stream()
                .map(movie -> convertToDto(movie, memberId))
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponseDto getMovieById(Long id, String memberId) {
        log.info("Getting movie by ID: {}", id);
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
        
        return convertToDto(movie, memberId);
    }

    @Override
    @Transactional
    public RecommendationResponseDto toggleRecommendation(Long movieId, String memberId) {
        log.info("Toggling recommendation for movie ID: {} by member ID: {}", movieId, memberId);
        
        // Check if movie exists
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));
        
        // Check if recommendation exists
        Optional<MovieRecommendationEntity> existingRecommendation = 
                recommendationRepository.findByMovieIdAndMemberId(movieId, memberId);
        
        if (existingRecommendation.isPresent()) {
            // Remove recommendation
            recommendationRepository.delete(existingRecommendation.get());
            movie.decrementRecommendationCount();
            movieRepository.save(movie);
            
            log.info("Recommendation removed for movie ID: {} by member ID: {}", movieId, memberId);
            return RecommendationResponseDto.removed(movieId, movie.getRecommendationCount());
        } else {
            // Add recommendation
            try {
                MovieRecommendationEntity recommendation = MovieRecommendationEntity.create(movieId, memberId);
                recommendationRepository.save(recommendation);
                
                movie.incrementRecommendationCount();
                movieRepository.save(movie);
                
                log.info("Recommendation added for movie ID: {} by member ID: {}", movieId, memberId);
                return RecommendationResponseDto.added(movieId, movie.getRecommendationCount());
            } catch (Exception e) {
                log.error("Error adding recommendation", e);
                throw new DuplicateRecommendationException(movieId, memberId);
            }
        }
    }

    @Override
    public boolean isRecommendedByUser(Long movieId, String memberId) {
        if (memberId == null) {
            return false;
        }
        return recommendationRepository.existsByMovieIdAndMemberId(movieId, memberId);
    }

    @Override
    public List<String> getAllGenres() {
        log.info("Getting all distinct genres");
        return movieRepository.findAllDistinctGenres();
    }
    
    /**
     * Convert MovieEntity to MovieResponseDto with recommendation status
     * MovieEntity를 추천 상태를 포함한 MovieResponseDto로 변환
     * 
     * @param movie MovieEntity
     * @param memberId 회원 ID
     * @return MovieResponseDto
     */
    private MovieResponseDto convertToDto(MovieEntity movie, String memberId) {
        boolean isRecommended = false;
        if (memberId != null) {
            isRecommended = isRecommendedByUser(movie.getId(), memberId);
        }
        return MovieResponseDto.fromEntity(movie, isRecommended);
    }
}