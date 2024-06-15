package me.macao.msdto.request;

import lombok.NonNull;

public record OwnerCreateDTO(
        @NonNull Long id,
        @NonNull String email
) { }
