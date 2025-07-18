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
public class MemberResponseDto {
    private String id;
    private String email;
    private String name;
    private String birth;
}