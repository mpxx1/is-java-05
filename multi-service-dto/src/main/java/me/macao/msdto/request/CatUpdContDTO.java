package me.macao.msdto.request;

import me.macao.data.CatColor;

public record CatUpdContDTO(
        String name,
        String breed,
        CatColor color,
        String birthday,
        Long addFriend,
        Long rmFriend
) { }
