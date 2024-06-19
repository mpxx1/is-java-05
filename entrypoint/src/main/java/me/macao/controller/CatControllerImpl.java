package me.macao.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.macao.dto.CatInitDTO;
import me.macao.dto.CatUpdContDTO;
import me.macao.dto.DetailsResponseDTO;
import me.macao.exception.*;
import me.macao.kafka.CatProducer;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.msdto.reply.EmptyReply;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.request.CatCreateDTO;
import me.macao.msdto.request.CatUpdateDTO;
import me.macao.msdto.request.FriendOperation;
import me.macao.msdto.request.IdRequest;
import me.macao.percistence.UserRole;
import me.macao.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v0.1.0/cats")
public class CatControllerImpl
    implements CatController {

    private final UserService userDetailsService;
    private final CatProducer catProducer;
    private final ExceptionTranslator translator;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    @NonNull
    @GetMapping("all")
    public Collection<CatResponseDTO> getAllCats()
        throws AccessDeniedException {

        var user = getUserFromSecurityContext();

        if (user.role() != UserRole.ADMIN)
            throw new AccessDeniedException("Access denied");

        return proceedCats(
                catProducer
                        .kafkaRequestReply("get_all_cats", "")
        );
    }

    @Override
    @NonNull
    @GetMapping("my")
    public Collection<CatResponseDTO> getMyCats()
            throws AccessDeniedException {

        var user = getUserFromSecurityContext();

        if (user.role() != UserRole.BASE)
            throw new AccessDeniedException("Access denied");

        return proceedCats(
                catProducer
                        .kafkaRequestReply(
                                "get_cats_by_user_id",
                                new IdRequest(user.id())
                        )
        );
    }

    @Override
    @NonNull
    @GetMapping("{id}")
    public CatResponseDTO getCatById(@PathVariable("id") final long id)
            throws AccessDeniedException, ObjectNotFoundException {

        var user = getUserFromSecurityContext();

        CatResponseDTO response = proceedCat(
                catProducer
                        .kafkaRequestReply(
                                "get_cat_by_id",
                                new IdRequest(id)
                        )
        );

        if (user.role() != UserRole.ADMIN ||
                !(user.id().equals(response.owner())))
            throw new AccessDeniedException("Access denied");

        return response;
    }

    @Override
    @NonNull
    @PostMapping
    public CatResponseDTO createCat(@NonNull @RequestBody CatInitDTO initDTO) {

        var user = getUserFromSecurityContext();

        if (user.role() == UserRole.ADMIN)
            throw new AccessDeniedException("Access denied");

        return proceedCat(
                catProducer
                        .kafkaRequestReply(
                                "create_cat",
                                new CatCreateDTO(
                                        user.id(),
                                        initDTO.name(),
                                        initDTO.breed(),
                                        initDTO.color(),
                                        initDTO.birthday()
                                )
                        )
        );
    }

    @Override
    @NonNull
    @PatchMapping("{id}")
    public CatResponseDTO updateCat(
            @PathVariable("id") long id,
            @NonNull @RequestBody CatUpdContDTO updateDTO
    ) throws AccessDeniedException, ObjectNotFoundException {

        var user = getUserFromSecurityContext();

        CatResponseDTO tgt = proceedCat(
                catProducer
                        .kafkaRequestReply(
                                "get_cat_by_id",
                                new IdRequest(id)
                        )
        );

        if (user.role() != UserRole.ADMIN &&
                !(tgt.owner().equals(user.id())))
            throw new AccessDeniedException("Access denied");

        if (updateDTO.addFriend() != null)
            catProducer.kafkaRequestReply(
                    "add_friend",
                    new FriendOperation(
                            id, updateDTO.addFriend()
                    )
            );

        if (updateDTO.rmFriend() != null)
            catProducer.kafkaRequestReply(
                    "remove_friend",
                    new FriendOperation(
                            id, updateDTO.rmFriend()
                    )
            );

        return proceedCat(
                catProducer
                        .kafkaRequestReply(
                                "update_cat",
                                new CatUpdateDTO(
                                        id,
                                        updateDTO.owner(),
                                        updateDTO.name(),
                                        updateDTO.breed(),
                                        updateDTO.color(),
                                        updateDTO.birthday(),
                                        null,
                                        null
                                )
                        )
        );
    }

    @Override
    public void deleteCat(long id)
            throws AccessDeniedException {

        var user = getUserFromSecurityContext();

        CatResponseDTO tgt = proceedCat(
                catProducer
                        .kafkaRequestReply(
                                "get_cat_by_id",
                                new IdRequest(id)
                        )
        );

        if (user.role() != UserRole.ADMIN &&
                !(tgt.owner().equals(user.id())))
            throw new AccessDeniedException("Access denied");

        var reply = catProducer.kafkaRequestReply(
                "delete_cat", new IdRequest(id)
        );

        try {

            mapper.readValue(reply, EmptyReply.class);

        } catch (Exception e) {

            throw new InvalidOperationException("Something went wrong, try again later");
        }
    }

    private Collection<CatResponseDTO> proceedCats(String optionalCats) {

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

        return new ArrayList<>();
    }

    private CatResponseDTO proceedCat(String optionalCat) {

        try {

            return mapper.readValue(
                    optionalCat, CatResponseDTO.class
            );

        } catch (Exception e) {

            try {

                var err = mapper.readValue(
                        optionalCat, ErrMap.class
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
