package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public class PageResponseMapper {

    public static <T> PageResponseDto<T> fromSlice(Slice<T> slice){
        return new PageResponseDto<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    public static <T> PageResponseDto<T> fromPage(Page<T> page){
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
