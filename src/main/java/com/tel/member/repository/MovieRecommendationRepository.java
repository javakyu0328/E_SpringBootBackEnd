package com.tel.member.repository;

import com.tel.member.entity.MovieRecommendationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Movie Recommendation Repository Interface
 * 영화 추천 데이터 접근을 위한 Repository 인터페이스
 */
@Repository
public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendationEntity, Long> {

    /**
     * Find recommendation by movie ID and member ID
     * 영화 ID와 회원 ID로 추천 조회
     */
    Optional<MovieRecommendationEntity> findByMovieIdAndMemberId(Long movieId, String memberId);

    /**
     * Check if recommendation exists by movie ID and member ID
     * 영화 ID와 회원 ID로 추천 존재 여부 확인
     */
    boolean existsByMovieIdAndMemberId(Long movieId, String memberId);

    /**
     * Find all recommendations by member ID
     * 회원 ID로 모든 추천 조회
     */
    Page<MovieRecommendationEntity> findByMemberId(String memberId, Pageable pageable);

    /**
     * Find all recommendations by movie ID
     * 영화 ID로 모든 추천 조회
     */
    Page<MovieRecommendationEntity> findByMovieId(Long movieId, Pageable pageable);

    /**
     * Count recommendations by movie ID
     * 영화 ID로 추천 수 조회
     */
    long countByMovieId(Long movieId);

    /**
     * Count recommendations by member ID
     * 회원 ID로 추천 수 조회
     */
    long countByMemberId(String memberId);

    /**
     * Delete recommendation by movie ID and member ID
     * 영화 ID와 회원 ID로 추천 삭제
     */
    void deleteByMovieIdAndMemberId(Long movieId, String memberId);

    /**
     * Delete all recommendations by movie ID
     * 영화 ID로 모든 추천 삭제
     */
    void deleteByMovieId(Long movieId);

    /**
     * Delete all recommendations by member ID
     * 회원 ID로 모든 추천 삭제
     */
    void deleteByMemberId(String memberId);

    /**
     * Find member IDs who recommended a specific movie
     * 특정 영화를 추천한 회원 ID 목록 조회
     */
    @Query("SELECT r.memberId FROM MovieRecommendationEntity r WHERE r.movieId = :movieId")
    List<String> findMemberIdsByMovieId(@Param("movieId") Long movieId);

    /**
     * Find movie IDs recommended by a specific member
     * 특정 회원이 추천한 영화 ID 목록 조회
     */
    @Query("SELECT r.movieId FROM MovieRecommendationEntity r WHERE r.memberId = :memberId")
    List<Long> findMovieIdsByMemberId(@Param("memberId") String memberId);

    /**
     * Find top recommended movies with count
     * 추천 수가 많은 영화 목록 조회
     */
    @Query("SELECT r.movieId, COUNT(r) as recommendationCount " +
           "FROM MovieRecommendationEntity r " +
           "GROUP BY r.movieId " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findTopRecommendedMovies(Pageable pageable);

    /**
     * Find movies recommended by member with pagination
     * 회원이 추천한 영화 목록을 페이징으로 조회
     */
    @Query("SELECT r FROM MovieRecommendationEntity r WHERE r.memberId = :memberId ORDER BY r.createdAt DESC")
    Page<MovieRecommendationEntity> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") String memberId, Pageable pageable);

    /**
     * Find recent recommendations for a movie
     * 영화의 최근 추천 목록 조회
     */
    @Query("SELECT r FROM MovieRecommendationEntity r WHERE r.movieId = :movieId ORDER BY r.createdAt DESC")
    Page<MovieRecommendationEntity> findByMovieIdOrderByCreatedAtDesc(@Param("movieId") Long movieId, Pageable pageable);
}