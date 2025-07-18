package com.tel.member.entity;

import com.tel.member.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="member_table")
public class MemberEntity {
/*    @GeneratedValue(strategy = GenerationType.IDENTITY)  // MySQL에서 auto_increment
    private long memberNum;*/

    @Id
    @Column(unique = true)  //unique 제약조건을 추가
    private String id;

    @Column
    private String name;

    @Column
    private String birth;

    @Column
    private String email;

    @Column
    private String password;


    public static MemberEntity toMemberEntity(MemberDTO memberDTO){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(memberDTO.getId());
        memberEntity.setName(memberDTO.getName());
        memberEntity.setBirth(memberDTO.getBirth());
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setPassword(memberDTO.getPassword());
        return memberEntity;
    }

    public static MemberEntity toUpdateMemberEntity(MemberDTO memberDTO){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(memberDTO.getId());
        memberEntity.setName(memberDTO.getName());
        memberEntity.setBirth(memberDTO.getBirth());
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setPassword(memberDTO.getPassword());
        return memberEntity;
    }
}
