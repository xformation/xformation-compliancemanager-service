/*
 * */
package com.synectiks.process.server.rest.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.database.PaginatedList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

@JsonAutoDetect
public class PaginatedResponse<T> {
    private final String listKey;
    private final PaginatedList<T> paginatedList;
    private final String query;
    private final Map<String, Object> context;

    private PaginatedResponse(String listKey, PaginatedList<T> paginatedList, @Nullable String query, @Nullable Map<String, Object> context) {
        this.listKey = listKey;
        this.paginatedList = paginatedList;
        this.query = query;
        this.context = context;
    }

    @JsonValue
    public Map<String, Object> jsonValue() {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .putAll(paginatedList.pagination().asMap())
                .put(listKey, new ArrayList<>(paginatedList));

        if (query != null) {
            builder.put("query", query);
        }

        if (paginatedList.grandTotal().isPresent()) {
            builder.put("grand_total", paginatedList.grandTotal().get());
        }

        if (context != null && !context.isEmpty()) {
            builder.put("context", context);
        }

        return builder.build();
    }

    public static <T> PaginatedResponse<T> create(String listKey, PaginatedList<T> paginatedList) {
        return new PaginatedResponse<>(listKey, paginatedList, null, null);
    }

    public static <T> PaginatedResponse<T> create(String listKey, PaginatedList<T> paginatedList, Map<String, Object> context) {
        return new PaginatedResponse<>(listKey, paginatedList, null, context);
    }

    public static <T> PaginatedResponse<T> create(String listKey, PaginatedList<T> paginatedList, String query) {
        return new PaginatedResponse<>(listKey, paginatedList, query, null);
    }

    public static <T> PaginatedResponse<T> create(String listKey, PaginatedList<T> paginatedList, String query, Map<String, Object> context) {
        return new PaginatedResponse<>(listKey, paginatedList, query, context);
    }
}
