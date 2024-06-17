package me.macao.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.dto.*;
import me.macao.exception.*;
import me.macao.percistence.User;
import me.macao.percistence.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl
    implements UserService {

    private UserDao userDao;
    private DTOMapper dtoMapper;
    private CryptoServiceImpl cryptoService;

    @Override
    @NonNull
    public final Collection<DetailsResponseDTO> getAllUsers() {

        return userDao
                .findAll()
                .stream()
                .map(dtoMapper::userToResponseDTO)
                .toList();
    }

    @Override
    @NonNull
    public final DetailsResponseDTO getUserById(
            @NonNull final Long id
    ) throws ObjectNotFoundException {

        return dtoMapper.userToResponseDTO(
                userDao.findById(id)
        );
    }

    @Override
    @NonNull
    public final DetailsResponseDTO updateUser(
            @NonNull final DetailsUpdateDTO userDto
    ) throws ObjectNotFoundException, InvalidOperationException,
            PasswordCreateException, EmailCreateException {

        String salt = cryptoService.generateSalt();

        if (userDto.newPassword() != null)
            cryptoService.passVerify(userDto.newPassword());

        if (userDto.newUsername() != null)
            cryptoService.mailVerify(userDto.newUsername());

        DetailsInternalUpdDTO uDto = new DetailsInternalUpdDTO(
                userDto.id(),
                userDto.newUsername() != null
                    ? userDto.newUsername()
                    : null,
                userDto.newPassword() != null
                    ? cryptoService.encode(userDto.newPassword(), salt)
                    : null,
                userDto.newPassword() != null ? salt : null
        );

        return dtoMapper.userToResponseDTO(
                userDao.update(uDto)
        );
    }

    @Override
    public final void deleteUserById(@NonNull final Long id) {

        userDao.deleteById(id);
    }

    @Override
    @NonNull
    public final Long getUserIdByEmail(
            @NonNull final String email
    ) throws ObjectNotFoundException {

        Optional<User> user = userDao.findByEmail(email);

        if (user.isEmpty())
            throw new ObjectNotFoundException("User with email " + email + " not found");

        return user
                .get()
                .getId();
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userDao
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
    }
}
