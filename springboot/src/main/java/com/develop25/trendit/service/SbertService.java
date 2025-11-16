package com.develop25.trendit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SbertService {

    // 내 노트북에서 돌고 있는 모델 서버 주소
    @Value("${sbert.remote.base-url}")
    private String baseUrl;

    private final RestClient restClient;

    public SbertService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    // 노트북 쪽 EmbeddingController와 JSON 형식을 맞춰야 함
    public record EmbedReq(String text) {}
    public record EmbedRes(String text, float[] vector, int dim) {}

    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be null/blank");
        }

        EmbedRes res = restClient.post()
                .uri(baseUrl + "/model/embed")   // 노트북의 컨트롤러 주소
                .body(new EmbedReq(text))
                .retrieve()
                .body(EmbedRes.class);

        if (res == null || res.vector == null) {
            throw new IllegalStateException("임베딩 서버에서 유효한 응답을 받지 못했습니다.");
        }

        return res.vector();
    }

    public static double cosine(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
