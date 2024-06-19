package me.macao.dto;

import lombok.NonNull;
import me.macao.data.CatColor;

import java.time.LocalDate;

public record CatInitDTO(
        @NonNull String name,
        @NonNull String breed,
        @NonNull CatColor color,
        @NonNull LocalDate birthday
) { }
