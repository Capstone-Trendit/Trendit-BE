package com.develop25.trendit.dto;

import com.develop25.trendit.domain.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public List<String> tags;
    public String userId;
    public String userPassword;
}