package me.macao.controller;

import lombok.NonNull;
import me.macao.dto.UserResponseDTO;
import me.macao.dto.UserUpdateDTO;
import me.macao.exception.DataTransferException;
import me.macao.exception.InvalidOperationException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.exception.PasswordCreateException;
import me.macao.msdto.request.EmailRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;

public interface UserController {

    @NonNull
    Collection<UserResponseDTO> getAllUsers()
            throws AccessDeniedException, DataTransferException;

    @NonNull
    UserResponseDTO getUserById(@NonNull final Long id)
            throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException;

    @NonNull
    UserResponseDTO getUserByEmail(@NonNull final EmailRequest request)
            throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException;

    @NonNull
    UserResponseDTO updateUser(
            @NonNull final Long id,
            @NonNull final UserUpdateDTO updateDTO
    ) throws InvalidOperationException, ObjectNotFoundException,
            AccessDeniedException, PasswordCreateException, DataTransferException;

    void deleteUser(@NonNull final Long id)
            throws AccessDeniedException, DataTransferException;
}
