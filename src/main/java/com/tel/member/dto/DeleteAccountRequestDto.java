package com.tel.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeleteAccountRequestDto {
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}