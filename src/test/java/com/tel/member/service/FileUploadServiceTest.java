package com.tel.member.service;

import com.tel.member.service.impl.FileUploadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadServiceTest {

    private FileUploadService fileUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setup() {
        fileUploadService = new FileUploadServiceImpl();
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileUploadService, "uploadUrlPrefix", "/uploads");
    }

    @Test
    public void testUploadImage_Success() throws IOException {
        // 테스트 데이터 준비
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // 테스트 실행
        String uploadedUrl = fileUploadService.uploadImage(file, "poster");

        // 검증
        assertNotNull(uploadedUrl);
        assertTrue(uploadedUrl.startsWith("/uploads/poster/"));
        assertTrue(uploadedUrl.endsWith(".jpg"));

        // 파일이 실제로 저장되었는지 확인
        String filename = uploadedUrl.substring(uploadedUrl.lastIndexOf('/') + 1);
        Path savedFilePath = tempDir.resolve("poster").resolve(filename);
        assertTrue(Files.exists(savedFilePath));
        assertArrayEquals(content, Files.readAllBytes(savedFilePath));
    }

    @Test
    public void testUploadImage_EmptyFile() {
        // 테스트 데이터 준비
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // 테스트 실행 및 검증
        Exception exception = assertThrows(IOException.class, () -> {
            fileUploadService.uploadImage(emptyFile, "poster");
        });

        assertEquals("업로드할 파일이 없습니다.", exception.getMessage());
    }

    @Test
    public void testUploadImage_InvalidFileType() {
        // 테스트 데이터 준비
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "text content".getBytes()
        );

        // 테스트 실행 및 검증
        Exception exception = assertThrows(IOException.class, () -> {
            fileUploadService.uploadImage(textFile, "poster");
        });

        assertTrue(exception.getMessage().contains("지원되지 않는 파일 형식입니다"));
    }

    @Test
    public void testUploadImage_FileTooLarge() {
        // 테스트 데이터 준비 - 6MB 파일 (최대 5MB)
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        // 테스트 실행 및 검증
        Exception exception = assertThrows(IOException.class, () -> {
            fileUploadService.uploadImage(largeFile, "poster");
        });

        assertTrue(exception.getMessage().contains("파일 크기가 너무 큽니다"));
    }

    @Test
    public void testValidateImageUrl_InternalUrl() throws IOException {
        // 테스트 데이터 준비 - 내부 URL
        String type = "poster";
        String filename = "test.jpg";
        Path typeDir = tempDir.resolve(type);
        Files.createDirectories(typeDir);
        Path filePath = typeDir.resolve(filename);
        Files.write(filePath, "test image content".getBytes());

        String internalUrl = "/uploads/" + type + "/" + filename;

        // 테스트 실행 및 검증
        boolean isValid = fileUploadService.validateImageUrl(internalUrl);
        assertTrue(isValid);
    }

    @Test
    public void testValidateImageUrl_InvalidInternalUrl() {
        // 테스트 데이터 준비 - 존재하지 않는 내부 URL
        String invalidInternalUrl = "/uploads/poster/nonexistent.jpg";

        // 테스트 실행 및 검증
        boolean isValid = fileUploadService.validateImageUrl(invalidInternalUrl);
        assertFalse(isValid);
    }

    @Test
    public void testValidateImageUrl_NullOrEmptyUrl() {
        // 테스트 실행 및 검증
        assertFalse(fileUploadService.validateImageUrl(null));
        assertFalse(fileUploadService.validateImageUrl(""));
        assertFalse(fileUploadService.validateImageUrl("   "));
    }
}