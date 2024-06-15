package me.macao.dto;

import lombok.NonNull;

public record DetailsUpdateDTO(
        @NonNull Long id,
        String new_pass_hash,
        String new_salt
) { }
