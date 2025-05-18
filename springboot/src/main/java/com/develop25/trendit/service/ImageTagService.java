package com.develop25.trendit.service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String apiUrl = "https://api.openai.com/v1/chat/completions";

        OkHttpClient client = new OkHttpClient();

        String payload = "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"You're a helpful assistant that extracts keywords from images.\"},\n" +
                "    {\"role\": \"user\", \"content\": [\n" +
                "      {\"type\": \"image_url\", \"image_url\": {\"url\": \"data:image/png;base64," + base64Image + "\"}},\n" +
                "      {\"type\": \"text\", \"text\": \"이미지에서 태그(명사 위주)를 5개 뽑아줘. 리스트 형태로.\"}\n" +
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
            JSONObject json = new JSONObject(responseBody);
            String content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return Arrays.stream(content.split("[\\n,\\-\\•]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}
