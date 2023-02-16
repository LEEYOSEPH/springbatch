package com.example.springbach.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberName;
    private int age;
    private String birthDay;

    private Date date;

    public Accounts(Members members) {
        this.id = members.getId();
        this.memberName = members.getMemberName();
        this.age = members.getAge();
        this.birthDay = members.getBirthDay();
        this.date = new Date();
    }
}
