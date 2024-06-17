package me.macao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.dto.AuthenticationResponse;
import me.macao.dto.UserInitDTO;
import me.macao.dto.UserLoginDTO;
import me.macao.exception.*;
import me.macao.kafka.OwnerProducer;
import me.macao.service.AuthService;
import me.macao.service.UserService;
import org.springframework.web.bind.annotation.*;

import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.msdto.reply.OwnerResponseDTO;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/v0.1.0/auth")
@AllArgsConstructor
public class AuthControllerImpl
    implements AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final OwnerProducer ownerProducer;
    private final ObjectMapper mapper = new ObjectMapper();

    @NonNull
    @PostMapping("signup")
    public final AuthenticationResponse register(
            @NonNull @RequestBody final UserInitDTO regDto
    ) throws InvalidOperationException, DateTimeParseException,
            EmailCreateException, PasswordCreateException {

        AuthenticationResponse authResponse = authService.register(regDto);

        var optionalOwner = ownerProducer
                .kafkaRequestReply(
                        "create_owner",
                        new OwnerCreateDTO(
                                userService.getUserIdByEmail(
                                        regDto.email()
                                ),
                                regDto.email()
                        )
                );

        try {

            mapper.readValue(
                    optionalOwner, OwnerResponseDTO.class
            );

            return authResponse;

        } catch (Exception e) {

            userService.deleteUserById(
                    userService.getUserIdByEmail(
                            regDto.email()
                    )
            );

            throw new DataTransferException("Can not create users; try again later");
        }
    }

    @NonNull
    @GetMapping("login")
    public final AuthenticationResponse login(
            @NonNull @RequestBody final UserLoginDTO loginDto
    ) throws ObjectNotFoundException {

        return authService.authenticate(loginDto);
    }
}
