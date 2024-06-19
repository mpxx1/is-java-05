package me.macao.dto;

import me.macao.data.CatColor;

import java.time.LocalDate;

public record CatUpdContDTO(
        Long owner,
        String name,
        String breed,
        CatColor color,
        LocalDate birthday,
        Long addFriend,
        Long rmFriend
) { }
