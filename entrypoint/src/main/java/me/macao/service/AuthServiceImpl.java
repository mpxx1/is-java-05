package me.macao.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.dto.*;
import me.macao.exception.EmailCreateException;
import me.macao.exception.InvalidOperationException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.exception.PasswordCreateException;
import me.macao.percistence.User;
import me.macao.percistence.UserDao;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl
    implements AuthService {

    private UserDao userDao;
    private DTOMapper mapper;
    private JwtServiceImpl jwtService;
    private CryptoServiceImpl cryptoService;

    @NonNull
    public final AuthenticationResponse register(
            @NonNull final UserInitDTO regDto
    ) throws PasswordCreateException, EmailCreateException,
            InvalidOperationException {

        if (userDao.findByEmail(regDto.email()).isPresent())
            throw new InvalidOperationException("Email is already taken");

        var salt = cryptoService.generateSalt();

        cryptoService.passVerify(regDto.password());
        cryptoService.mailVerify(regDto.email());

        User user = userDao.create(
                new DetailsCreateDTO(
                        regDto.email(),
                        cryptoService.encode(regDto.password(), salt),
                        salt,
                        mapper.stringToRole(regDto.role())
                )
        );
        var token = jwtService.generateToken(user, new HashMap<>());

        return new AuthenticationResponse(token);
    }

    @NonNull
    public final AuthenticationResponse authenticate(
            @NonNull final UserLoginDTO loginDto
    ) throws ObjectNotFoundException {

        Optional<User> user = userDao.findByEmail(loginDto.email());

        if (user.isEmpty())
            throw new ObjectNotFoundException("Wrong email or password");

        var salt = user.get().getSalt();

        if (!cryptoService.verify(loginDto.password(), salt, user.get().getPassword()))
            throw new ObjectNotFoundException("Wrong email or password");

        var token = jwtService.generateToken(user.get(), new HashMap<>());

        return new AuthenticationResponse(token);
    }
}
