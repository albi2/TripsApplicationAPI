package com.lhind.tripapp.dto.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedResponse<DTO> {
    private List<DTO> content;
    private int count;
    private long totalCount;


    public PagedResponse(final List<DTO> content,final int count,final long totalCount) {
        this.content = content;
        this.count = count;
        this.totalCount = totalCount;
    }
}
