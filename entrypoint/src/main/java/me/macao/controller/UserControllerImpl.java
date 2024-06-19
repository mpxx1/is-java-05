package me.macao.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.macao.dto.*;
import me.macao.exception.*;
import me.macao.kafka.CatProducer;
import me.macao.kafka.OwnerProducer;
import me.macao.msdto.reply.*;
import me.macao.msdto.request.*;
import me.macao.percistence.UserRole;
import me.macao.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v0.1.0/owners")
public class UserControllerImpl
    implements UserController {

    private final UserService userDetailsService;
    private final OwnerProducer ownerProducer;
    private final CatProducer catProducer;
    private final ExceptionTranslator translator;
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
        var optionalCats = catProducer
                .kafkaRequestReply("get_all_cats", "");

        Collection<OwnerResponseDTO> owners = new ArrayList<>();

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

                translator.exec(err);

            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }

        return mergeToResponse(
                owners,
                userDetails,
                getCats(optionalCats)
        );
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
        var optionalCats = catProducer
                .kafkaRequestReply(
                        "get_cats_by_user_id", new IdRequest(id)
                );

        return mergeToResponse(
                getOwner(optionalOwner),
                userDetails,
                getCats(optionalCats)
        );
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

        var optionalCats = catProducer
                .kafkaRequestReply(
                        "get_cats_by_user_id", new IdRequest(id)
                );

        return mergeToResponse(
                getOwner(optionalOwner),
                userDetails,
                getCats(optionalCats)
        );
    }

    @DeleteMapping("{id}")
    public void deleteUser(@NonNull @PathVariable("id") final Long id)
            throws AccessDeniedException, DataTransferException {

        var curUser = getUserFromSecurityContext();

        if (curUser.role() != UserRole.ADMIN && !curUser.id().equals(id))
            throw new AccessDeniedException("Access denied");

        var optionalEmptyReply = ownerProducer
                .kafkaRequestReply("delete_owner", new IdRequest(id));

        var optionalCatEmptyReply = catProducer
                .kafkaRequestReply(
                        "delete_cats_by_user_id", new IdRequest(id)
                );

        try {

            mapper.readValue(
                    optionalEmptyReply, EmptyReply.class
            );

            mapper.readValue(
                    optionalCatEmptyReply, EmptyReply.class
            );

            userDetailsService.deleteUserById(id);
        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalEmptyReply,
                        ErrMap.class
                );

                translator.exec(err);

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

                translator.exec(err);

            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }

        return null;
    }

    private Collection<CatResponseDTO> getCats(String optionalCats) {

        try {

            return mapper.readValue(
                    optionalCats, new TypeReference<>() {}
            );
        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalCats, ErrMap.class
                );

                translator.exec(err);
            } catch (Exception e1) {

                throw new DataTransferException(
                        "Something went wrong: " + e1.getMessage()
                );
            }
        }

        return null;
    }

    private UserResponseDTO mergeToResponse(
            final OwnerResponseDTO owner,
            final DetailsResponseDTO details,
            final Collection<CatResponseDTO> cats
    ) {
        return new UserResponseDTO(
                details.id(),
                details.username(),
                details.role(),
                owner.name(),
                owner.birthday(),
                cats
                        .stream()
                        .map(CatResponseDTO::catId)
                        .toList()
        );
    }

    private Collection<UserResponseDTO> mergeToResponse(
            final Collection<OwnerResponseDTO> owners,
            final Collection<DetailsResponseDTO> userDetails,
            final Collection<CatResponseDTO> cats
    ) {

        Map<Long, OwnerResponseDTO> ownersMap = new HashMap<>();
        Map<Long, List<Long>> catsMap = new HashMap<>();
        Collection<UserResponseDTO> users = new ArrayList<>();

        for (var owner : owners) { ownersMap.put(owner.id(), owner); }
        for (var cat : cats) {
            catsMap
                    .computeIfAbsent(
                            cat.owner(),
                            k -> new ArrayList<>()
                    )
                    .add(cat.catId());
        }

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
                            catsMap.containsKey(details.id())
                                    ? catsMap.get(details.id())
                                    : new ArrayList<>()
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
