package com.tel.member.controller;

import com.tel.member.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileUploadControllerTest {

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private FileUploadController fileUploadController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileUploadController).build();
    }

    @Test
    public void testUploadImage_Success() throws Exception {
        // 테스트 데이터 준비
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String uploadedUrl = "/uploads/poster/test.jpg";
        when(fileUploadService.uploadImage(any(), eq("poster"))).thenReturn(uploadedUrl);

        // 테스트 실행 및 검증
        mockMvc.perform(multipart("/api/upload/image")
                .file(file)
                .param("type", "poster"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(uploadedUrl))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUploadImage_Failure() throws Exception {
        // 테스트 데이터 준비
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String errorMessage = "파일 업로드 실패";
        when(fileUploadService.uploadImage(any(), any())).thenThrow(new IOException(errorMessage));

        // 테스트 실행 및 검증
        mockMvc.perform(multipart("/api/upload/image")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    public void testValidateImageUrl_Valid() throws Exception {
        // 테스트 데이터 준비
        String imageUrl = "https://example.com/image.jpg";
        when(fileUploadService.validateImageUrl(imageUrl)).thenReturn(true);

        // 테스트 실행 및 검증
        mockMvc.perform(get("/api/upload/validate-image")
                .param("url", imageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    public void testValidateImageUrl_Invalid() throws Exception {
        // 테스트 데이터 준비
        String imageUrl = "https://example.com/invalid.jpg";
        when(fileUploadService.validateImageUrl(imageUrl)).thenReturn(false);

        // 테스트 실행 및 검증
        mockMvc.perform(get("/api/upload/validate-image")
                .param("url", imageUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}