package com.develop25.trendit.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String userId;
    private String password;
    private String name;
    private Integer age;
    private String gender;

    @Column(nullable = false)
    private boolean receiveAlarm = true;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();
}