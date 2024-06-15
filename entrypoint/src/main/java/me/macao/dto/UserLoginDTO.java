package me.macao.dto;

import lombok.NonNull;

public record UserLoginDTO(
        @NonNull String email,
        @NonNull String password
) { }
