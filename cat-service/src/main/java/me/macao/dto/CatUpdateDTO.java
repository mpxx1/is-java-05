package me.macao.dto;

import lombok.NonNull;
import me.macao.data.CatColor;

import java.time.LocalDate;
import java.util.Collection;

public record CatUpdateDTO(
        @NonNull Long id,
        Long owner,
        String name,
        String breed,
        CatColor color,
        LocalDate birthday,
        Collection<Long> friends,
        Collection<Long> reqsOut
) { }