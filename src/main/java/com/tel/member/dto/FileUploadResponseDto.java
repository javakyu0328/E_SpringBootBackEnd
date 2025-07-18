package com.tel.member.dto;

/**
 * 파일 업로드 응답 DTO
 */
public class FileUploadResponseDto {

    private String url;
    private boolean success;
    private String message;

    /**
     * 성공 응답 생성자
     *
     * @param url 업로드된 파일의 URL
     */
    public FileUploadResponseDto(String url) {
        this.url = url;
        this.success = true;
        this.message = "파일이 성공적으로 업로드되었습니다.";
    }

    /**
     * 실패 응답 생성자
     *
     * @param url 업로드된 파일의 URL (null일 수 있음)
     * @param message 오류 메시지
     */
    public FileUploadResponseDto(String url, String message) {
        this.url = url;
        this.success = false;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}