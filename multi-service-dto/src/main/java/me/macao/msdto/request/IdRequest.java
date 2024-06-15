package me.macao.msdto.request;

import lombok.NonNull;

public record IdRequest(
        @NonNull Long id
) {
}
