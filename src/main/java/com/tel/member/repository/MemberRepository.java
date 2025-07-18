package com.tel.member.repository;

import com.tel.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    //이메일로 회원 정보 조회(select * from member_table where member_email=?)
    Optional<MemberEntity> findById(String Id);
    
    //이메일로 회원 정보 조회(select * from member_table where email=?)
    Optional<MemberEntity> findByEmail(String email);
}
