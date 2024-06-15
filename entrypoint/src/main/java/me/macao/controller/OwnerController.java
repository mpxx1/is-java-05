package me.macao.controller;

import me.macao.dto.UserResponseDTO;
import me.macao.dto.UserUpdateDTO;
import me.macao.exception.*;
import me.macao.msdto.request.EmailRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;

public interface OwnerController {

    Collection<UserResponseDTO> getAllUsers()
            throws AccessDeniedException, DataTransferException;

    UserResponseDTO getUserById(final Long id)
            throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException;

    UserResponseDTO getUserByEmail(final EmailRequest request)
            throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException;

    UserResponseDTO updateUser(     // base
            final UserUpdateDTO updateDTO
    ) throws InvalidOperationException, ObjectNotFoundException,
            AccessDeniedException, PasswordCreateException, DataTransferException;

    UserResponseDTO updateUser(     // admin
            final Long id,
            final UserUpdateDTO updateDTO
    ) throws InvalidOperationException, ObjectNotFoundException,
            AccessDeniedException, PasswordCreateException, DataTransferException;

    void deleteUser(final Long id)
            throws AccessDeniedException, DataTransferException;
}
