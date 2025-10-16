package com.develop25.trendit.service;

import ai.djl.Application;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SbertService {

    @Value("${sbert.modelDir}") private String modelDir; // 현재는 미사용(로컬로 바꿀 때 씀)
    @Value("${sbert.maxLength:24}") private int maxLength;
    @Value("${sbert.normalize:true}") private boolean normalize;

    private ZooModel<String, float[]> model;

    @PostConstruct
    public void init() {
        // (선택) DJL 캐시 고정: System.setProperty("ai.djl.cacheDir", "C:/djl-cache");

        // 현재 외부 모델 사용
        String modelUrl = "djl://ai.djl.huggingface.pytorch/sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2";

        try {
            var criteria = Criteria.builder()
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .setTypes(String.class, float[].class)
                    .optModelUrls(modelUrl)
                    .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                    .optEngine("PyTorch")
                    .optArgument("pipeline", "feature-extraction")
                    .optArgument("maxLength", String.valueOf(maxLength))
                    .optArgument("normalize", String.valueOf(normalize))
                    .build();

            model = criteria.loadModel();

            // 웜업: predictor는 짧게 만들고 닫기
            try (Predictor<String, float[]> p = model.newPredictor()) {
                float[] ping = p.predict("ping");
                System.out.println("[SBERT] model loaded. dim=" + ping.length +
                        " first5=" + Arrays.toString(Arrays.copyOf(ping, Math.min(5, ping.length))));
            }
        } catch (Exception e) {
            throw new IllegalStateException("[SBERT] init failed: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage(), e);
        }
    }

    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be null/blank");
        }

        try (Predictor<String, float[]> p = model.newPredictor()) {
            return p.predict(text);
        } catch (TranslateException e) {
            // 원인 로깅/전달: 실제 근본 원인은 e.getCause()에 있는 경우가 많음
            Throwable root = (e.getCause() != null) ? e.getCause() : e;
            throw new InferenceRuntimeException("Embedding 실패: " + root.getMessage(), e);
        }
    }

    public static double cosine(float[] a, float[] b) {
        double dot=0, na=0, nb=0;
        for (int i=0;i<a.length;i++){ dot+=a[i]*b[i]; na+=a[i]*a[i]; nb+=b[i]*b[i]; }
        return dot / (Math.sqrt(na)*Math.sqrt(nb));
    }

    @PreDestroy
    public void close() {
        if (model != null) model.close();
    }
}

