package com.develop25.trendit.service;



import com.develop25.trendit.config.NaverShoppingClient;
import com.develop25.trendit.dto.ShopSearchItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryTagService {

    private final NaverShoppingClient client;

    public CategoryTagService(NaverShoppingClient client) {
        this.client = client;
    }

    /** 컨트롤러에서 부르는 진입점 */
    public CategoryTagResult categoryTagForProductName(String productName) {
        var resp = client.search(productName, 50, 1, "sim");
        return pickCategoryTag(resp.items(), 4, 5, true);
    }

    /** 파이썬 로직 이식: 대표 제외 + 대안 5개 + 한 경로로만 몰리면 roll-up */
    public CategoryTagResult pickCategoryTag(List<ShopSearchItem> items, int preferDepth, int altK, boolean rollupIfSingle) {
        List<List<String>> paths = new ArrayList<>();
        for (var it : items) {
            String c1 = it.category1(), c2 = it.category2(), c3 = it.category3(), c4 = it.category4();
            if (nonEmpty(c4)) paths.add(List.of(c1, c2, c3, c4));
            else if (nonEmpty(c3)) paths.add(List.of(c1, c2, c3));
            else if (nonEmpty(c2)) paths.add(List.of(c1, c2));
            else if (nonEmpty(c1)) paths.add(List.of(c1));
        }
        if (paths.isEmpty()) {
            return new CategoryTagResult(null, List.of(), new Counts(0, 0, 0, 0, null));
        }

        int maxDepth = paths.stream().mapToInt(List::size).max().orElse(1);
        int targetDepth = Math.min(preferDepth, maxDepth);

        var rankedAtDepth = rankAtDepth(paths, targetDepth);

        if (rankedAtDepth.size() == 1 && rollupIfSingle) {
            int rolledFrom = targetDepth;
            for (int d = targetDepth - 1; d >= 1; d--) {
                var ranked = rankAtDepth(paths, d);
                if (ranked.size() > 1) {
                    return buildResult(ranked, items.size(), d, altK, rolledFrom);
                }
            }
        }
        return buildResult(rankedAtDepth, items.size(), targetDepth, altK, null);
    }

    private boolean nonEmpty(String s) { return s != null && !s.isBlank(); }

    private List<Map.Entry<List<String>, Long>> rankAtDepth(List<List<String>> paths, int depth) {
        Map<List<String>, Long> freq = paths.stream()
                .filter(p -> p.size() >= depth)
                .map(p -> p.subList(0, depth))
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        return freq.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .toList();
    }

    private CategoryTagResult buildResult(
            List<Map.Entry<List<String>, Long>> ranked,
            int totalItems, int depth, int altK, Integer rolledFrom
    ) {
        var best = ranked.get(0);

        // 이미 List<String> 형태이므로 그대로 사용
        List<String> tag = new ArrayList<>(best.getKey());
        long topCount = best.getValue();

        var alts = ranked.stream().skip(1).limit(altK)
                .map(e -> new Alternative(new ArrayList<>(e.getKey()), e.getValue()))
                .toList();

        var counts = new Counts(
                totalItems,
                depth,
                topCount,
                ranked.size(),
                rolledFrom
        );

        return new CategoryTagResult(tag, alts, counts);
    }

    /* ===== 반환 모델(심플 record) ===== */
    public record CategoryTagResult(
            List<String> tag,                 // ["식품","농산물","과일","사과"]
            List<Alternative> alternatives,
            Counts counts
    ) {}

    public record Alternative(
            List<String> path,                // ["식품","농산물","과일"]
            long count
    ) {}
    public record Counts(
            int totalItemsConsidered,
            int depthUsed,
            long topCount,
            int uniquePathsAtDepth,
            Integer rolledUpFromDepth
    ) {}
}
