package me.macao.service;

import lombok.NonNull;
import me.macao.dto.AuthenticationResponse;
import me.macao.dto.UserInitDTO;
import me.macao.dto.UserLoginDTO;
import me.macao.exception.EmailCreateException;
import me.macao.exception.InvalidOperationException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.exception.PasswordCreateException;

public interface AuthService {

    @NonNull
    AuthenticationResponse register(
            @NonNull final UserInitDTO regDto
    ) throws PasswordCreateException, EmailCreateException,
            InvalidOperationException;

    @NonNull
    AuthenticationResponse authenticate(
            @NonNull final UserLoginDTO loginDto
    ) throws ObjectNotFoundException;
}
