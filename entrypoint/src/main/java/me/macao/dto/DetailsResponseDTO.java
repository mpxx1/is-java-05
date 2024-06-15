package me.macao.dto;

import lombok.NonNull;
import me.macao.percistence.UserRole;

public record DetailsResponseDTO(
        @NonNull Long id,
        @NonNull String username,
        @NonNull UserRole role
) { }
