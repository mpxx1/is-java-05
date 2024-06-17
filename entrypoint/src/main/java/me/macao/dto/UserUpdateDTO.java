package me.macao.dto;

import java.time.LocalDate;

public record UserUpdateDTO(
        String email,
        String password,
        String name,
        LocalDate birthday
) { }
