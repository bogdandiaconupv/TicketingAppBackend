package com.ticketingapp.shared.dto;

import jakarta.validation.constraints.NotNull;

public record SuccessDto(
        @NotNull
        boolean success
) {
}
