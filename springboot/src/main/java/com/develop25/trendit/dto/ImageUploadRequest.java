package com.develop25.trendit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ImageUploadRequest {
    @Schema(description = "업로드할 이미지 파일", type = "string", format = "binary", required = true)
    private MultipartFile file;
    String productName;
}
