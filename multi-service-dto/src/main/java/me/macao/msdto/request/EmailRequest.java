package me.macao.msdto.request;

import lombok.NonNull;

public record EmailRequest(
        @NonNull String email
) {
}
