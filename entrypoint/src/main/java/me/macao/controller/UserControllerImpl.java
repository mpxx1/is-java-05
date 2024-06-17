package me.macao.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.macao.dto.DetailsResponseDTO;
import me.macao.dto.DetailsUpdateDTO;
import me.macao.dto.UserResponseDTO;
import me.macao.dto.UserUpdateDTO;
import me.macao.exception.DataTransferException;
import me.macao.exception.InvalidOperationException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.exception.PasswordCreateException;
import me.macao.kafka.OwnerProducer;
import me.macao.msdto.reply.EmptyReply;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.EmailRequest;
import me.macao.msdto.request.IdRequest;
import me.macao.msdto.request.OwnerUpdateDTO;
import me.macao.percistence.UserRole;
import me.macao.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v0.1.0/owners")
public class UserControllerImpl
    implements UserController {

    private final UserService userDetailsService;
    private final OwnerProducer ownerProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @NonNull
    @GetMapping("all")
    public Collection<UserResponseDTO> getAllUsers()
            throws AccessDeniedException, DataTransferException {

        var user = getUserFromSecurityContext();

        if (user.role() != UserRole.ADMIN)
            throw new AccessDeniedException("Access denied");

        var userDetails = userDetailsService.getAllUsers();
        var optionalOwners = ownerProducer
                .kafkaRequestReply("get_all_owners", "");

        Collection<OwnerResponseDTO> owners;

        try {

            owners = mapper.readValue(
                    optionalOwners, new TypeReference<>() {}
            );
        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalOwners,
                        ErrMap.class
                );

                throw new DataTransferException(
                        "Internal error: " + err.reason() +
                                "\nmessage: " + err.message()
                );
            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }

        return mergeToResponse(owners, userDetails);
    }

    @NonNull
    @GetMapping(path = "{id}")
    public final UserResponseDTO getUserById(
            @NonNull @PathVariable("id") final Long id
    ) throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException {

        var curUser = getUserFromSecurityContext();

        if (curUser.role() != UserRole.ADMIN && !curUser.id().equals(id))
            throw new AccessDeniedException("Access denied");

        var userDetails = userDetailsService.getUserById(id);
        var optionalOwner = ownerProducer
                .kafkaRequestReply("get_owner_by_id", new IdRequest(id));

        return mergeToResponse(getOwner(optionalOwner), userDetails);
    }

    @NonNull
    @GetMapping("em")
    public final UserResponseDTO getUserByEmail(
            @NonNull @RequestBody final EmailRequest request
    ) throws AccessDeniedException, ObjectNotFoundException,
            DataTransferException {

        var curUser = getUserFromSecurityContext();

        if (curUser.role() != UserRole.ADMIN)
            throw new AccessDeniedException("Access denied");

        return getUserById(
                userDetailsService
                        .getUserIdByEmail(
                                request.email()
                        )
        );
    }

    @NonNull
    @PatchMapping("{id}")
    public UserResponseDTO updateUser(
            @NonNull @PathVariable("id") final Long id,
            @NonNull @RequestBody final UserUpdateDTO updateDTO
    ) throws InvalidOperationException, ObjectNotFoundException,
            AccessDeniedException, PasswordCreateException, DataTransferException {

        var curUser = getUserFromSecurityContext();

        if (curUser.role() != UserRole.ADMIN && !curUser.id().equals(id))
            throw new AccessDeniedException("Access denied");

        var userDetails = userDetailsService.getUserById(id);

        userDetailsService.updateUser(
                new DetailsUpdateDTO(
                        userDetails.id(),
                        updateDTO.email(),
                        updateDTO.password()
                )
        );

        var optionalOwner = ownerProducer
                .kafkaRequestReply(
                        "update_owner",
                            new OwnerUpdateDTO(
                                    userDetails.id(),
                                    updateDTO.email(),
                                    updateDTO.name(),
                                    updateDTO.birthday()
                            )
                        );

        return mergeToResponse(getOwner(optionalOwner), userDetails);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@NonNull @PathVariable("id") final Long id)
            throws AccessDeniedException, DataTransferException {

        var curUser = getUserFromSecurityContext();

        if (curUser.role() != UserRole.ADMIN && !curUser.id().equals(id))
            throw new AccessDeniedException("Access denied");

        var optionalEmptyReply = ownerProducer
                .kafkaRequestReply("delete_owner", new IdRequest(id));

        try {

            mapper.readValue(
                    optionalEmptyReply, EmptyReply.class
            );

            userDetailsService.deleteUserById(id);
        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalEmptyReply,
                        ErrMap.class
                );

                throw new DataTransferException(
                        "Internal error: " + err.reason() +
                        "\nmessage: " + err.message() +
                        "\nCan not delete user, try again later"
                );
            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }
    }

    private OwnerResponseDTO getOwner(String optionalOwner)
            throws DataTransferException {

        try {

            return mapper.readValue(
                    optionalOwner, OwnerResponseDTO.class
            );
        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalOwner,
                        ErrMap.class
                );

                throw new DataTransferException(
                        "Internal error: " + err.reason() +
                                "\nmessage: " + err.message()
                );
            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }
    }

    private UserResponseDTO mergeToResponse(
            final OwnerResponseDTO owner,
            final DetailsResponseDTO details
    ) {
        return new UserResponseDTO(
                details.id(),
                details.username(),
                details.role(),
                owner.name(),
                owner.birthday(),
                new ArrayList<>() // todo
        );
    }

    private Collection<UserResponseDTO> mergeToResponse(
            final Collection<OwnerResponseDTO> owners,
            final Collection<DetailsResponseDTO> userDetails
    ) {

        Map<Long, OwnerResponseDTO> ownersMap = new HashMap<>();
        Collection<UserResponseDTO> users = new ArrayList<>();

        for (var owner : owners) { ownersMap.put(owner.id(), owner); }

        for (var details : userDetails) {
            users.add(
                    new UserResponseDTO(
                            details.id(),
                            details.username(),
                            details.role(),
                            ownersMap.get(
                                    details.id()
                            ).name(),
                            ownersMap.get(
                                    details.id()
                            ).birthday(),
                            new ArrayList<>() // todo
                    )
            );
        }

        return users;
    }

    private DetailsResponseDTO getUserFromSecurityContext()
            throws ObjectNotFoundException {

        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        var username = userDetails.getUsername();
        var uid = userDetailsService.getUserIdByEmail(username);

        return userDetailsService.getUserById(uid);
    }
}
