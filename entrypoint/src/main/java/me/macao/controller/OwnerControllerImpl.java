package me.macao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.dto.DetailsResponseDTO;
import me.macao.dto.UserResponseDTO;
import me.macao.exception.ObjectNotFoundException;
import me.macao.kafka.OwnerProducer;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v0.1.0/owners")
public class OwnerControllerImpl
    /*implements OwnerController*/ {

    private final UserService userDetailsService;
    private final OwnerProducer ownerProducer;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // get all owners
    // get owner by id
    // get id by email (admin)
    // update1 owner
    // update2 owner
    // delete owner

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
