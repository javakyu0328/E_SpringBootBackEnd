package com.tel.member.service;

import com.tel.member.dto.MemberDTO;
import com.tel.member.dto.PasswordChangeRequestDto;
import com.tel.member.entity.MemberEntity;
import com.tel.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public void save(MemberDTO memberDTO) {
        //1.dto->entity 변환
        //2.repository의 save 메서드 호출
        //repository의 save메서드 호출 (조건. entity객체를 넘겨줘야함)

        //MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO); //비밀번호 암호화로 인한 기존 코드 주석
        //memberRepository.save(memberEntity);                                //비밀번호 암호화로 인한 기존 코드 주석

        // 원래 비밀번호 -> 암호화
        String rawPw = memberDTO.getPassword();
        String encodedPw = passwordEncoder.encode(rawPw);
        memberDTO.setPassword(encodedPw);

        memberDTO.setJoinDate(LocalDate.now()); //배치작업을 위한 가입날짜 설정
        memberDTO.setGrade("C"); //배치작업을 위한 기본 등급 설정

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO); // 변환 메서드 만들기

        log.info("회원가입 memberDTO:"+memberDTO.toString());
        log.info("회원가입 memberDTO:"+memberEntity.toString());

        memberRepository.save(memberEntity);
    }

    public MemberDTO login(MemberDTO memberDTO) {
        /*
            1.회원이 입력한 이메일로 DB에서 조회를 함
            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
         */
        Optional<MemberEntity> byMemberId = memberRepository.findById(memberDTO.getId());
        if(byMemberId.isPresent()){
            //조회할 결과가 있다(해당 아이디를 가진 회원 정보가 있다)
            MemberEntity memberEntity = byMemberId.get();
            //if(memberEntity.getPassword().equals(memberDTO.getPassword())){  //비번암호화에 따른 기존 로직 주석처리
            if(passwordEncoder.matches(memberDTO.getPassword(),memberEntity.getPassword())){
                //비밀번호가 일치 하는 경우
                //entity -> dto 변환 후 리턴
                //MemberDTO dto = MemberDTO.toMemberDTO(memberEntity); //비번암호화에 따른 기존 로직 주석처리
                // return dto; //비번암호화에 따른 기존 로직 주석처리

                // 비밀번호 일치 → Entity를 DTO로 변환해서 리턴
                return MemberDTO.toMemberDTO(memberEntity);

            }else {
            //비밀번호 불일치(로그인실패)
            return null;
            }
        }else {
            //조회 결과가 없다(해당 이메일을 가진 회원이 없다)
            return null;
        }
        
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for(MemberEntity memberEntity:memberEntityList){
            memberDTOList.add(MemberDTO.toMemberDTO(memberEntity));
        }
        return memberDTOList;
    }

    public MemberDTO findById(String id) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
        if(optionalMemberEntity.isPresent()){
            /*
            MemberEntity memberEntity = optionalMemberEntity.get();
            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
            return memberDTO;
            -> 위 3줄을 아래 1줄로 표현
            */
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        }else{
            return null;
        }
    }

    public MemberDTO updateForm(String myId) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(myId);
        if(optionalMemberEntity.isPresent()){
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        }else{
            return null;
        }
    }

    public void update(MemberDTO memberDTO) {
        memberRepository.save(MemberEntity.toUpdateMemberEntity(memberDTO)); //save메서드 쓰는 이유는 DB에 처음 넣을때도 사용하지만 ID가 존재할 경우 자동으로update로 수정해줌
    }

    public void deleteById(String id) {
        memberRepository.deleteById(id);
    }

    //아이디 유효성 검사(ajax)
    public String idCheck(String id) {
        Optional<MemberEntity> byId = memberRepository.findById(id);
        if(byId.isPresent()){
            //조회결과가 있음 -> 사용 불가
            System.out.println("사용불가");
            return "no";
        } else {
            //조회결과 없음 -> 사용 가능
            System.out.println("사용가능");
            return "ok";
        }
    }
    
    /**
     * 비밀번호 변경 기능
     * 1. 현재 비밀번호 확인
     * 2. 새 비밀번호와 확인 비밀번호 일치 여부 확인
     * 3. 새 비밀번호 유효성 검사 (4자 이상)
     * 4. 비밀번호 업데이트
     */
    public String changePassword(String id, PasswordChangeRequestDto passwordChangeRequestDto) {
        // 1. 현재 사용자 정보 조회
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
        if (!optionalMemberEntity.isPresent()) {
            return "사용자를 찾을 수 없습니다";
        }
        
        MemberEntity memberEntity = optionalMemberEntity.get();
        
        // 2. 현재 비밀번호 확인
        if (!memberEntity.getPassword().equals(passwordChangeRequestDto.getCurrentPassword())) {
            return "현재 비밀번호가 일치하지 않습니다";
        }
        
        // 3. 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!passwordChangeRequestDto.getNewPassword().equals(passwordChangeRequestDto.getConfirmPassword())) {
            return "새 비밀번호가 일치하지 않습니다";
        }
        
        // 4. 새 비밀번호 유효성 검사 (4자 이상)
        if (passwordChangeRequestDto.getNewPassword().length() < 4) {
            return "비밀번호는 4자 이상이어야 합니다";
        }
        
        // 5. 비밀번호 업데이트
        memberEntity.setPassword(passwordChangeRequestDto.getNewPassword());
        memberRepository.save(memberEntity);
        
        return "비밀번호가 성공적으로 변경되었습니다";
    }
}
