package me.macao.msdto.request;

import lombok.NonNull;

public record FriendOperation(
        @NonNull Long src,
        @NonNull Long tgt
) {
}
