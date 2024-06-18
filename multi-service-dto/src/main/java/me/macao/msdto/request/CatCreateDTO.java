package me.macao.msdto.request;

import lombok.NonNull;
import me.macao.data.CatColor;

import java.time.LocalDate;

public record CatCreateDTO(
        @NonNull Long owner,
        @NonNull String name,
        @NonNull String breed,
        @NonNull CatColor color,
        @NonNull LocalDate birthday
) { }
