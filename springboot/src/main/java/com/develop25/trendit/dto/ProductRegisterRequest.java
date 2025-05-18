package com.develop25.trendit.dto;

import com.develop25.trendit.domain.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductRegisterRequest {
    public MultipartFile file;
    public String name;
    public Double price;
    public Long count;
    public String tags;
    public User user;
}