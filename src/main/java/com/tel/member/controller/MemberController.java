package com.tel.member.controller;

import com.tel.member.dto.*;
import com.tel.member.exception.InvalidPasswordException;
import com.tel.member.service.MemberApiService;
import com.tel.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 통합된 회원 컨트롤러
 * MemberController와 MemberApiController의 기능을 통합하여 일관된 API 경로 제공
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    // 서비스 주입
    private final MemberService memberService;
    private final MemberApiService memberApiService;

    /**
     * 회원 가입 API
     */
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody MemberDTO memberDTO) {
        log.info("회원가입 요청: {}", memberDTO);
        memberService.save(memberDTO);
        return ResponseEntity.ok("ok");
    }

    /**
     * 로그인 처리 API
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("로그인 요청: {}", memberDTO);
        MemberDTO loginResult = memberService.login(memberDTO);

        if (loginResult != null) {
            // 로그인 성공
            log.info("로그인 성공: {}", loginResult.getId());
            // 세션에 로그인 ID 저장
            session.setAttribute("loginId", loginResult.getId());
            session.setAttribute("loginEmail", loginResult.getEmail()); // MemberApiController와의 호환성을 위해 추가
            
            Map<String, Object> response = new HashMap<>();
            response.put("result", "ok");
            response.put("user", loginResult);
            
            return ResponseEntity.ok(response);
        } else {
            // 로그인 실패
            log.info("로그인 실패");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", "fail");
            errorResponse.put("message", "아이디 또는 비밀번호가 틀렸습니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * 로그인 상태 확인 API
     */
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        if (loginId != null) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("user", loginId);
            return ResponseEntity.ok(userMap);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthenticated");
        }
    }

    /**
     * 현재 로그인한 사용자 ID 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<String> getLoginId(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        if (loginId != null) {
            return ResponseEntity.ok(loginId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthenticated");
        }
    }

    /**
     * 회원 정보 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> findById(@PathVariable String id) {
        MemberDTO memberDTO = memberService.findById(id);

        if (memberDTO != null) {
            return ResponseEntity.ok(memberDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 모든 회원 정보 목록 불러오기
     */
    @GetMapping("/all")
    public ResponseEntity<List<MemberDTO>> findAll() {
        List<MemberDTO> memberDTOList = memberService.findAll();

        if (memberDTOList != null) {
            return ResponseEntity.ok(memberDTOList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 회원 정보 수정 API
     */
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody MemberDTO memberDTO) {
        log.info("회원 정보 수정 요청: {}", memberDTO);
        memberService.update(memberDTO);
        return ResponseEntity.ok("updated");
    }

    /**
     * 회원 탈퇴 API
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id, @RequestBody DeleteAccountRequestDto deleteAccountDto) {
        log.info("회원 탈퇴 요청: {}", id);
        
        try {
            // 비밀번호 확인 후 회원 탈퇴 처리
            memberApiService.deleteAccount(id, deleteAccountDto);
            return ResponseEntity.ok("deleted");
        } catch (InvalidPasswordException e) {
            log.error("비밀번호 불일치: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다.");
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 로그아웃 API
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("logout");
    }

    /**
     * 아이디 중복 확인 API
     */
    @PostMapping("/id-check")
    public ResponseEntity<String> idCheck(@RequestParam("id") String memberId) {
        log.info("아이디 중복 확인: {}", memberId);
        String checkResult = memberService.idCheck(memberId);

        if (checkResult.equals("ok")) {
            return ResponseEntity.ok("ok"); // 사용 가능
        } else {
            return ResponseEntity.ok("no"); // 사용 불가
        }
    }

    /**
     * 비밀번호 변경 API
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            HttpSession session,
            @RequestBody PasswordChangeRequestDto passwordChangeRequestDto) {
        
        // 세션에서 로그인된 사용자 ID 가져오기
        String loginId = (String) session.getAttribute("loginId");
        Map<String, String> response = new HashMap<>();
        

        // 로그인 상태 확인
        if (loginId == null) {
            response.put("result", "fail");
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // 비밀번호 변경 서비스 호출
        String result = memberService.changePassword(loginId, passwordChangeRequestDto);
        
        // 결과에 따른 응답 생성
        if (result.equals("비밀번호가 성공적으로 변경되었습니다")) {
            response.put("result", "ok");
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("result", "fail");
            response.put("message", result);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 오류 응답을 위한 내부 클래스
     */
    private static class ErrorResponse {
        private final String message;
        private final String code;
        private final boolean success = false;
        
        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getCode() {
            return code;
        }
        
        public boolean isSuccess() {
            return success;
        }
    }
    
    /**
     * 유효성 검사 오류 응답을 위한 내부 클래스
     */
    private static class ValidationErrorResponse {
        private final boolean success = false;
        private final String code = "INVALID_INPUT";
        private final Map<String, String> errors;
        
        public ValidationErrorResponse(Map<String, String> errors) {
            this.errors = errors;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getCode() {
            return code;
        }
        
        public Map<String, String> getErrors() {
            return errors;
        }
    }
}