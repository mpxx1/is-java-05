package me.macao.msdto.request;

import lombok.NonNull;

import java.time.LocalDate;

public record OwnerUpdateDTO(
        @NonNull Long id,
        String email,
        String name,
        LocalDate birthday
) { }
