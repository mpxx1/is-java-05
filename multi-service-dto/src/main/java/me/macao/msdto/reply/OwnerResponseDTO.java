package me.macao.msdto.reply;

import lombok.NonNull;

import java.time.LocalDate;

public record OwnerResponseDTO(
        @NonNull Long id,
        String name,
        LocalDate birthday
) {
}
