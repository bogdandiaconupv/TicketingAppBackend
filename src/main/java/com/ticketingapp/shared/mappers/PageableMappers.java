package com.ticketingapp.shared.mappers;

import com.ticketingapp.shared.constants.SortDirectionEnum;
import com.ticketingapp.shared.dto.PageRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableMappers {
    public static PageRequest pageRequestToDtoMapper(PageRequestDto dto) {
        SortDirectionEnum sortDirection = dto.sortDirection() == null ? SortDirectionEnum.asc : dto.sortDirection();
        int pageNumber = dto.pageNumber() == null ? 0 : dto.pageNumber();
        int pageSize = dto.pageSize() == null ? 10 : dto.pageSize();
        Sort direction = (dto.sortBy() == null) ? Sort.unsorted() :
                SortDirectionEnum.asc.equals(sortDirection) ? Sort.by(dto.sortBy()).ascending() : Sort.by(dto.sortBy()).descending();
        return PageRequest.of(pageNumber, pageSize, direction);
    }
}
