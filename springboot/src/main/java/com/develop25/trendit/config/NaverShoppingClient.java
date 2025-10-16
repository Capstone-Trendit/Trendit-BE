package com.develop25.trendit.config;

import com.develop25.trendit.dto.ShopSearchResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class NaverShoppingClient {
    private final WebClient webClient;

    public NaverShoppingClient(WebClient naverWebClient) {
        this.webClient = naverWebClient;
    }

    public ShopSearchResponse search(String query, Integer display, Integer start, String sort) {
        int disp = Math.max(1, Math.min(display == null ? 50 : display, 100));
        int st   = Math.max(1, Math.min(start == null ? 1 : start, 1000));
        String s = (sort == null || sort.isBlank()) ? "sim" : sort;

        return webClient.get()
                .uri(uri -> uri.path("/v1/search/shop.json")
                        .queryParam("query", query)
                        .queryParam("display", disp)
                        .queryParam("start", st)
                        .queryParam("sort", s)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class).flatMap(b ->
                                Mono.error(new RuntimeException("Naver 4xx: " + b))))
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class).flatMap(b ->
                                Mono.error(new RuntimeException("Naver 5xx: " + b))))
                .bodyToMono(ShopSearchResponse.class)
                .block();
    }
}
