package com.develop25.trendit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    private String userId;
    private String password;
    private String name;
    private Integer age;
    private String gender;
    private boolean receiveAlarm;

    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();
}