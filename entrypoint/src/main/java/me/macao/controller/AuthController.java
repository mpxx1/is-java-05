package me.macao.controller;

import lombok.NonNull;
import me.macao.dto.AuthenticationResponse;
import me.macao.dto.UserInitDTO;
import me.macao.dto.UserLoginDTO;
import me.macao.exception.*;

import java.time.format.DateTimeParseException;

public interface AuthController {

    @NonNull
    AuthenticationResponse register(@NonNull final UserInitDTO regDto)
            throws InvalidOperationException, DateTimeParseException,
            EmailCreateException, PasswordCreateException;

    @NonNull
    AuthenticationResponse login(@NonNull final UserLoginDTO loginDto)
            throws ObjectNotFoundException;
}
