package com.tel.member.exception;

/**
 * Exception thrown when a duplicate recommendation is attempted
 * 중복 추천을 시도할 때 발생하는 예외
 */
public class DuplicateRecommendationException extends RuntimeException {
    
    public DuplicateRecommendationException(String message) {
        super(message);
    }
    
    public DuplicateRecommendationException(Long movieId, String memberId) {
        super("이미 추천한 영화입니다. 영화 ID: " + movieId + ", 회원 ID: " + memberId);
    }
}