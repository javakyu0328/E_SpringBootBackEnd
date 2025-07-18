package com.tel.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberUpdateRequestDto {
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    private String birth;
}