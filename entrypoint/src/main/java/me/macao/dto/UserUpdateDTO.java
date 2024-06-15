package me.macao.dto;

public record UserUpdateDTO(
        String password,
        String name,
        String birthday
) { }
