package com.lhind.tripapp.converter;

import com.lhind.tripapp.dto.pagination.SearchRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SearchToPageConverter {
    public static final int DEFAULT_PAGE_SIZE=100;
    public PageRequest toPageRequest(SearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.ASC,"id");

        if(searchRequest == null)
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, sort);

        final int requestedSize = searchRequest.getSize();
        return PageRequest.of(searchRequest.getPage(), requestedSize == 0 ? DEFAULT_PAGE_SIZE : requestedSize,sort);
    }
}
