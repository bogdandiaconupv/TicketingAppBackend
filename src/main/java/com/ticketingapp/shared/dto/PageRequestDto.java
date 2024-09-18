package com.ticketingapp.shared.dto;

import com.ticketingapp.shared.constants.SortDirectionEnum;
import lombok.Builder;

@Builder
public record PageRequestDto(Integer pageNumber, Integer pageSize, SortDirectionEnum sortDirection, String sortBy) {
}
