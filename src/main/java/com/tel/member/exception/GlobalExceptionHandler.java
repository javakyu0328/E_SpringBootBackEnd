package com.tel.member.exception;

import com.tel.member.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler
 * 전역 예외 처리기
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle MovieNotFoundException
     * 영화를 찾을 수 없을 때 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMovieNotFoundException(
            MovieNotFoundException e, HttpServletRequest request) {
        
        log.error("Movie not found: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "MOVIE_NOT_FOUND",
                e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle DuplicateRecommendationException
     * 중복 추천을 시도할 때 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(DuplicateRecommendationException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateRecommendationException(
            DuplicateRecommendationException e, HttpServletRequest request) {
        
        log.error("Duplicate recommendation: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "DUPLICATE_RECOMMENDATION",
                e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle IllegalArgumentException
     * 잘못된 인자를 전달했을 때 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        
        log.error("Illegal argument: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "INVALID_ARGUMENT",
                e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle MethodArgumentNotValidException
     * 유효성 검사 실패 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        log.error("Validation error: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "VALIDATION_ERROR",
                "입력값 검증에 실패했습니다: " + errors,
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle BindException
     * 바인딩 실패 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(
            BindException e, HttpServletRequest request) {
        
        log.error("Binding error: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "BINDING_ERROR",
                "입력값 바인딩에 실패했습니다: " + errors,
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle ConstraintViolationException
     * 제약 조건 위반 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        
        log.error("Constraint violation: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "CONSTRAINT_VIOLATION",
                "제약 조건 위반: " + e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle DataIntegrityViolationException
     * 데이터 무결성 위반 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, HttpServletRequest request) {
        
        log.error("Data integrity violation: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "DATA_INTEGRITY_VIOLATION",
                "데이터 무결성 위반: 중복된 데이터가 존재하거나 참조 무결성이 깨졌습니다",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle MissingServletRequestParameterException
     * 필수 요청 파라미터가 누락되었을 때 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        
        log.error("Missing parameter: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "MISSING_PARAMETER",
                "필수 파라미터 누락: " + e.getParameterName(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle MethodArgumentTypeMismatchException
     * 메서드 인자 타입 불일치 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        log.error("Type mismatch: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "TYPE_MISMATCH",
                "타입 불일치: " + e.getName() + "는 " + e.getRequiredType().getSimpleName() + " 타입이어야 합니다",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle MaxUploadSizeExceededException
     * 파일 업로드 크기 제한 초과 시 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        
        log.error("File size exceeded: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "FILE_SIZE_EXCEEDED",
                "파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }
    
    /**
     * Handle MultipartException
     * 멀티파트 요청 처리 중 발생하는 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipartException(
            MultipartException e, HttpServletRequest request) {
        
        log.error("Multipart error: {}", e.getMessage());
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "MULTIPART_ERROR",
                "파일 업로드 중 오류가 발생했습니다: " + e.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle all other exceptions
     * 기타 모든 예외 처리
     * 
     * @param e 예외
     * @param request HTTP 요청
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception e, HttpServletRequest request) {
        
        log.error("Unhandled exception: ", e);
        
        ErrorResponseDto errorResponse = ErrorResponseDto.of(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}