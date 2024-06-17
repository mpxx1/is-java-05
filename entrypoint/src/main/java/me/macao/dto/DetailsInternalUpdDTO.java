package me.macao.dto;

import lombok.NonNull;

public record DetailsInternalUpdDTO(
        @NonNull Long id,
        String new_username,
        String new_pass_hash,
        String new_salt
) { }
