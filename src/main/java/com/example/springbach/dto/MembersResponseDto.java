package com.example.springbach.dto;

import com.example.springbach.entity.Members;
import lombok.Data;

import java.util.Date;

@Data
public class MembersResponseDto {

    private String memberName;
    private Integer age;
    private String birthDay;
    private Date date;

    public MembersResponseDto(Members members) {
        this.memberName = members.getMemberName();
        this.age = members.getAge();
        this.birthDay = members.getBirthDay();
        this.date = new Date();
    }
}
