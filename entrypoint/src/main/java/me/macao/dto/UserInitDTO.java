package me.macao.dto;

import lombok.NonNull;

public record UserInitDTO(
        @NonNull String email,
        @NonNull String password,
        @NonNull String role
) { }
