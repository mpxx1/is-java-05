package me.macao.dto;

import me.macao.percistence.UserRole;

public record DetailsCreateDTO(
    String username,
    String pass_hash,
    String salt,
    UserRole role
) { }
