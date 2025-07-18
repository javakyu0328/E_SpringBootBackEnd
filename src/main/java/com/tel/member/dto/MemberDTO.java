package com.tel.member.dto;

import com.tel.member.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDTO {
    private String id;
    private String name;
    private String email;
    private String birth;
    private String password;


    public static MemberDTO toMemberDTO(MemberEntity memberEntity){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setBirth((memberEntity.getBirth()));
        memberDTO.setPassword(memberEntity.getPassword());


        return memberDTO;
    }
}
