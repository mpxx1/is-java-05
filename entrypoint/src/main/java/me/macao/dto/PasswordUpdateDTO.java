package me.macao.dto;

import lombok.NonNull;

public record PasswordUpdateDTO(
        @NonNull Long id,
        String password
) { }
