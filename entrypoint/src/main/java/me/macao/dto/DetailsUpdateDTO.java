package me.macao.dto;

import lombok.NonNull;

public record DetailsUpdateDTO(
        @NonNull Long id,
        String newUsername,
        String newPassword
) {
}
