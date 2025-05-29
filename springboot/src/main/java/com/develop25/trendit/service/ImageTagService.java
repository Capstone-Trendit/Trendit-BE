package com.develop25.trendit.service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageTagService {
    @Value("${openai.api.key}")
    private String apiKey;

    public List<String> generateTags(byte[] imageBytes) throws IOException, JSONException {
        //String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String imageDataUri = ImageUtil.createDataUriFromImage(imageBytes);

        String apiUrl = "https://api.openai.com/v1/chat/completions";

        OkHttpClient client = new OkHttpClient();

        String payload = "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"You're a helpful assistant that extracts keywords from images.\"},\n" +
                "    {\"role\": \"user\", \"content\": [\n" +
                "      {\"type\": \"image_url\", \"image_url\": {\"url\": \"" + imageDataUri + "\"}},\n" +
                "      {\"type\": \"text\", \"text\": \"이 상품 이미지에서 상품명을 추출하고, 해당 상품의 특징 4가지를 포함해 총 5개의 항목을 [\\\"상품명\\\", \\\"특징1\\\", \\\"특징2\\\", \\\"특징3\\\", \\\"특징4\\\"] 형식의 문자열 배열로 출력해줘. 번호 없이.\"}\n" +
                "    ]}\n" +
                "  ],\n" +
                "  \"max_tokens\": 100\n" +
                "}";

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(payload, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // 응답 JSON 파싱
            JSONObject json = new JSONObject(responseBody);
            String content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            // JSON 배열로 파싱 시도
            if (content.startsWith("[") && content.endsWith("]")) {
                try {
                    JSONArray array = new JSONArray(content);
                    List<String> tags = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        tags.add(array.getString(i).trim());
                    }
                    return tags;
                } catch (JSONException e) {
                    System.err.println("JSON 배열 파싱 실패, fallback 실행");
                    // 아래 fallback 실행
                }
            }

            // fallback: 줄바꿈, 하이픈, 점 등으로 나눠서 처리
            return Arrays.stream(content.split("[\\n,\\-\\•]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}
