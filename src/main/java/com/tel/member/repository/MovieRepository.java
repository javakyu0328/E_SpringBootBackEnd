package com.tel.member.repository;

import com.tel.member.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Movie Repository Interface
 * 영화 데이터 접근을 위한 Repository 인터페이스
 */
@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    /**
     * Find movies by title containing keyword (case insensitive)
     * 제목에 키워드가 포함된 영화 검색 (대소문자 구분 없음)
     */
    Page<MovieEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find movies by genre
     * 장르별 영화 검색
     */
    Page<MovieEntity> findByGenre(String genre, Pageable pageable);

    /**
     * Find movies by genre containing keyword (case insensitive)
     * 장르에 키워드가 포함된 영화 검색 (대소문자 구분 없음)
     */
    Page<MovieEntity> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    /**
     * Find movies by title or genre containing keyword
     * 제목 또는 장르에 키워드가 포함된 영화 검색
     */
    @Query("SELECT m FROM MovieEntity m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MovieEntity> findByTitleOrGenreContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find all movies ordered by recommendation count descending
     * 추천 수 내림차순으로 모든 영화 조회
     */
    Page<MovieEntity> findAllByOrderByRecommendationCountDesc(Pageable pageable);

    /**
     * Find top N movies by recommendation count
     * 추천 수 상위 N개 영화 조회
     */
    @Query("SELECT m FROM MovieEntity m ORDER BY m.recommendationCount DESC, m.createdAt DESC")
    List<MovieEntity> findTopMoviesByRecommendationCount(Pageable pageable);

    /**
     * Find movies by release date
     * 개봉일별 영화 검색
     */
    Page<MovieEntity> findByReleaseDate(String releaseDate, Pageable pageable);

    /**
     * Find movies by release date containing year
     * 연도가 포함된 개봉일로 영화 검색
     */
    Page<MovieEntity> findByReleaseDateContaining(String year, Pageable pageable);

    /**
     * Find all distinct genres
     * 모든 고유 장르 조회
     */
    @Query("SELECT DISTINCT m.genre FROM MovieEntity m WHERE m.genre IS NOT NULL ORDER BY m.genre")
    List<String> findAllDistinctGenres();

    /**
     * Count movies by genre
     * 장르별 영화 수 조회
     */
    long countByGenre(String genre);

    /**
     * Find movies with recommendation count greater than or equal to specified count
     * 지정된 추천 수 이상의 영화 조회
     */
    Page<MovieEntity> findByRecommendationCountGreaterThanEqual(Integer count, Pageable pageable);

    /**
     * Check if movie exists by title (case insensitive)
     * 제목으로 영화 존재 여부 확인 (대소문자 구분 없음)
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Find movie by title (case insensitive)
     * 제목으로 영화 조회 (대소문자 구분 없음)
     */
    Optional<MovieEntity> findByTitleIgnoreCase(String title);
}