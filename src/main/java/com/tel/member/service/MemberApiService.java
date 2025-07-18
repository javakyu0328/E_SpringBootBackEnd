package com.tel.member.service;

import com.tel.member.dto.DeleteAccountRequestDto;
import com.tel.member.dto.MemberResponseDto;
import com.tel.member.dto.MemberUpdateRequestDto;
import com.tel.member.dto.PasswordChangeRequestDto;
import com.tel.member.entity.MemberEntity;
import com.tel.member.exception.InvalidInputException;
import com.tel.member.exception.InvalidPasswordException;
import com.tel.member.exception.PasswordMismatchException;
import com.tel.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApiService {
    private final MemberRepository memberRepository;

    /**
     * 이메일로 회원 정보 조회
     * @param email 회원 이메일
     * @return 회원 정보 DTO
     * @throws RuntimeException 회원을 찾을 수 없는 경우
     */
    public MemberResponseDto findByEmail(String email) {
        Optional<MemberEntity> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            MemberResponseDto dto = new MemberResponseDto();
            MemberEntity entity = optionalMember.get();
            dto.setId(entity.getId());
            dto.setEmail(entity.getEmail());
            dto.setName(entity.getName());
            dto.setBirth(entity.getBirth());
            return dto;
        } else {
            throw new RuntimeException("회원을 찾을 수 없습니다: " + email);
        }
    }
    
    /**
     * 회원 정보 업데이트
     * @param email 회원 이메일
     * @param updateDto 업데이트할 회원 정보
     * @return 업데이트된 회원 정보 DTO
     * @throws RuntimeException 회원을 찾을 수 없는 경우
     * @throws InvalidInputException 입력값이 유효하지 않은 경우
     */
    @Transactional
    public MemberResponseDto updateMember(String email, MemberUpdateRequestDto updateDto) {
        // 입력값 유효성 검사
        if (updateDto.getName() == null || updateDto.getName().trim().isEmpty()) {
            throw new InvalidInputException("이름은 필수 입력값입니다.");
        }
        
        if (updateDto.getBirth() == null || updateDto.getBirth().trim().isEmpty()) {
            throw new InvalidInputException("생년월일은 필수 입력값입니다.");
        }
        
        // 회원 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + email));
        
        // 회원 정보 업데이트
        memberEntity.setName(updateDto.getName());
        memberEntity.setBirth(updateDto.getBirth());
        
        // 저장
        MemberEntity updatedMember = memberRepository.save(memberEntity);
        
        // 응답 DTO 생성
        MemberResponseDto responseDto = new MemberResponseDto();
        responseDto.setId(updatedMember.getId());
        responseDto.setEmail(updatedMember.getEmail());
        responseDto.setName(updatedMember.getName());
        responseDto.setBirth(updatedMember.getBirth());
        
        return responseDto;
    }
    
    /**
     * 비밀번호 변경
     * @param email 회원 이메일
     * @param passwordChangeDto 비밀번호 변경 요청 DTO
     * @throws RuntimeException 회원을 찾을 수 없는 경우
     * @throws InvalidPasswordException 현재 비밀번호가 일치하지 않는 경우
     * @throws PasswordMismatchException 새 비밀번호와 확인 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void changePassword(String email, PasswordChangeRequestDto passwordChangeDto) {
        // 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new PasswordMismatchException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        
        // 회원 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + email));
        
        // 현재 비밀번호 확인
        if (!memberEntity.getPassword().equals(passwordChangeDto.getCurrentPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 비밀번호 변경
        memberEntity.setPassword(passwordChangeDto.getNewPassword());
        
        // 저장
        memberRepository.save(memberEntity);
    }
    
    /**
     * 회원탈퇴
     * @param loginId 로그인 ID (세션에 저장된 값)
     * @param deleteAccountDto 회원탈퇴 요청 DTO
     * @throws RuntimeException 회원을 찾을 수 없는 경우
     * @throws InvalidPasswordException 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void deleteAccount(String loginId, DeleteAccountRequestDto deleteAccountDto) {
        // 회원 조회 (loginId가 이메일인 경우와 ID인 경우 모두 처리)
        MemberEntity memberEntity;
        
        // 먼저 ID로 조회 시도
        Optional<MemberEntity> byId = memberRepository.findById(loginId);
        if (byId.isPresent()) {
            memberEntity = byId.get();
        } else {
            // ID로 찾지 못한 경우 이메일로 조회 시도
            memberEntity = memberRepository.findByEmail(loginId)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + loginId));
        }
        
        // 비밀번호 확인
        if (!memberEntity.getPassword().equals(deleteAccountDto.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
        log.info("회원 삭제 서비스 로직 memberEntity"+memberEntity.getId());
        // 회원 삭제
        memberRepository.delete(memberEntity);
    }
}