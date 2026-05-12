package com.study.domain.member.dto;

import com.study.domain.member.Gender;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ProfileUpdateRequest {
    private String name;
    private Gender gender;
    private LocalDate birthday;
}
