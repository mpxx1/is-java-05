package me.macao.msdto.reply;

import lombok.NonNull;
import me.macao.data.CatColor;

import java.time.LocalDate;
import java.util.Collection;

public record CatResponseDTO(
        @NonNull Long owner,
        @NonNull Long catId,
        @NonNull String name,
        @NonNull String breed,
        @NonNull CatColor catColor,
        @NonNull LocalDate birthday,
        @NonNull Collection<Long> friends,
        @NonNull Collection<Long> reqsIn,
        @NonNull Collection<Long> reqsOut
) {
}
