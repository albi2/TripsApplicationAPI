package com.lhind.tripapp.dto.pagination;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SearchRequest {
    private int size;
    private int page;
}
