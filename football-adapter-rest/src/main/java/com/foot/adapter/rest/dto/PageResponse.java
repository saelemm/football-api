package com.foot.adapter.rest.dto;

import java.util.List;

public record PageResponse<T>(List<T> content,
                              int page,
                              int size,
                              long totalElements,
                              int totalPages,
                              boolean first,
                              boolean last,
                              String sortBy,
                              String direction) {
}

