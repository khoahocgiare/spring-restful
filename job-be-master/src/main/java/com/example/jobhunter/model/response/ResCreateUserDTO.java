package com.example.jobhunter.model.response;

import java.time.Instant;

import com.example.jobhunter.util.constant.GenderEnum;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private CompanyUser company;

    @Data
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
