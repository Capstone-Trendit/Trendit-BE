package com.develop25.trendit.service;

import com.develop25.trendit.repository.BasicTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasicTagService {

    @Autowired
    private BasicTagRepository basicTagRepository;

    public List<String> getTagsByProductName(String productName) {
        return basicTagRepository.findTagsByProductName(productName);
    }
}