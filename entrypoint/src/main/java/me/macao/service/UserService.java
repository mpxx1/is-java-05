package me.macao.service;

import lombok.NonNull;
import me.macao.dto.DetailsResponseDTO;
import me.macao.dto.PasswordUpdateDTO;
import me.macao.exception.InvalidOperationException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.exception.PasswordCreateException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface UserService
    extends UserDetailsService {

    @NonNull
    Collection<DetailsResponseDTO> getAllUsers();

    @NonNull
    DetailsResponseDTO getUserById(@NonNull final Long id)
            throws ObjectNotFoundException;

    @NonNull
    DetailsResponseDTO updateUser(
            @NonNull final PasswordUpdateDTO userDto
    ) throws ObjectNotFoundException, InvalidOperationException,
            PasswordCreateException;

    void deleteUserById(@NonNull final Long id);

    @NonNull
    Long getUserIdByEmail(
            @NonNull final String email
    ) throws ObjectNotFoundException;
}
