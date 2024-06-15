package me.macao.dto;

import lombok.NonNull;

public record AuthenticationResponse(
        @NonNull String token
) { }
