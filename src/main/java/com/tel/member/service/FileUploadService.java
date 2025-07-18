package com.tel.member.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 파일 업로드 서비스 인터페이스
 */
public interface FileUploadService {

    /**
     * 이미지 파일을 업로드합니다.
     *
     * @param file 업로드할 이미지 파일
     * @param type 이미지 타입 (예: poster, thumbnail)
     * @return 업로드된 파일의 URL
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    String uploadImage(MultipartFile file, String type) throws IOException;

    /**
     * 외부 이미지 URL이 유효한지 확인합니다.
     *
     * @param url 확인할 이미지 URL
     * @return 유효성 여부
     */
    boolean validateImageUrl(String url);
}