package com.lhind.tripapp.converter;

import com.lhind.tripapp.dto.pagination.SearchRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SearchToPageConverter {
    public static final int DEFAULT_PAGE_SIZE=100;
    private static final Logger logger = LogManager.getLogger("com.lhind.tripapp.service");
    public PageRequest toPageRequest(SearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.ASC,"id");

        if(searchRequest == null) {
            logger.warn("Page size and index were either not provided: Defaults will be used(100 and 0 respectively)!");
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, sort);
        }
        if(searchRequest.getSize() == 0){
            logger.warn("Page size and index were either not provided or page size had value 0: Defaults will be used(100 and 0 respectively)!");
        }

        final int requestedSize = searchRequest.getSize();

        return PageRequest.of(searchRequest.getPage(), requestedSize == 0 ? DEFAULT_PAGE_SIZE : requestedSize,sort);
    }
}
