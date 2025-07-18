package com.tel.member.service.impl;

import com.tel.member.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 업로드 서비스 구현체
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.url.prefix:/uploads}")
    private String uploadUrlPrefix;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public String uploadImage(MultipartFile file, String type) throws IOException {
        // 파일 유효성 검사
        validateFile(file);

        // 업로드 디렉토리 생성
        String typeDir = type != null ? type : "misc";
        File directory = new File(uploadDir + File.separator + typeDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일명 생성 (중복 방지를 위해 UUID 사용)
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // 파일 저장
        Path targetLocation = Paths.get(directory.getAbsolutePath(), newFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 파일 URL 반환
        return uploadUrlPrefix + "/" + typeDir + "/" + newFilename;
    }

    @Override
    public boolean validateImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // 내부 URL인 경우 (서버에서 제공하는 이미지)
        if (url.startsWith(uploadUrlPrefix)) {
            String filePath = uploadDir + url.substring(uploadUrlPrefix.length());
            return new File(filePath).exists();
        }

        // 외부 URL인 경우
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();

            connection.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK && 
                   contentType != null && 
                   contentType.startsWith("image/");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일 유효성을 검사합니다.
     *
     * @param file 검사할 파일
     * @throws IOException 유효하지 않은 파일인 경우
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("업로드할 파일이 없습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IOException("지원되지 않는 파일 형식입니다. JPG, PNG, GIF, WEBP 형식만 업로드 가능합니다.");
        }
    }

    /**
     * 파일 확장자를 추출합니다.
     *
     * @param filename 파일명
     * @return 파일 확장자
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "jpg"; // 기본 확장자
    }
}