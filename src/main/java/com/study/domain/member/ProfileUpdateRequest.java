package com.study.domain.member;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ProfileUpdateRequest {
    private String name;
    private Gender gender;
    private LocalDate birthday;
}
