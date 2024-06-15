package me.macao.service;

import lombok.NonNull;
import me.macao.exception.*;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.msdto.request.OwnerUpdateDTO;

import java.util.Collection;
import java.util.Optional;

public interface OwnerService {

    @NonNull
    Collection<OwnerResponseDTO> getOwners();

    @NonNull
    Optional<OwnerResponseDTO> getOwnerById(
            @NonNull final Long id
    );

    @NonNull
    Optional<Long> getOwnerIdByEmail(
            @NonNull final String email
    );

    @NonNull
    OwnerResponseDTO createOwner(
            @NonNull final OwnerCreateDTO ownerDto
    ) throws InvalidOperationException;

    @NonNull
    OwnerResponseDTO updateOwner(
            @NonNull final OwnerUpdateDTO ownerDto
    ) throws ObjectNotFoundException;

    void deleteOwnerById(@NonNull final Long id);
}
