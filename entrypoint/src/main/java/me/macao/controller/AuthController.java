package me.macao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.macao.dto.AuthenticationResponse;
import me.macao.dto.UserInitDTO;
import me.macao.dto.UserLoginDTO;
import me.macao.exception.*;

import java.time.format.DateTimeParseException;

public interface AuthController {

    AuthenticationResponse register(final UserInitDTO regDto)
            throws InvalidOperationException, DateTimeParseException,
            EmailCreateException, PasswordCreateException,
            JsonProcessingException;

    AuthenticationResponse login(final UserLoginDTO loginDto)
            throws ObjectNotFoundException;
}
