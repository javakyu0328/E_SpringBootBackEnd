package com.tel.member.exception;

/**
 * Exception thrown when a movie is not found
 * 영화를 찾을 수 없을 때 발생하는 예외
 */
public class MovieNotFoundException extends RuntimeException {
    
    public MovieNotFoundException(String message) {
        super(message);
    }
    
    public MovieNotFoundException(Long id) {
        super("영화를 찾을 수 없습니다. ID: " + id);
    }
}