package me.macao.dto;

import me.macao.percistence.User;
import me.macao.percistence.UserRole;
import org.springframework.stereotype.Service;

@Service
public class DTOMapper {

    public final DetailsResponseDTO userToResponseDTO(final User user) {

        return new DetailsResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    public final UserRole stringToRole(final String str) {

        return switch (str.toLowerCase()) {
            case "administrator",
                 "admin",
                 "admn",
                 "adm",
                 "ad",
                 "a"        -> UserRole.ADMIN;

            default         -> UserRole.BASE;
        };
    }
}
