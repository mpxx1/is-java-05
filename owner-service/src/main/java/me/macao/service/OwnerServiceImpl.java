package me.macao.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.dto.DTOMapper;
import me.macao.exception.*;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.msdto.request.OwnerUpdateDTO;
import me.macao.percistence.Owner;
import me.macao.percistence.OwnerDao;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OwnerServiceImpl
    implements OwnerService {

    private OwnerDao ownerDao;
    private DTOMapper mapper;

    @Override
    @NonNull
    public final Collection<OwnerResponseDTO> getOwners() {

        return ownerDao
                .findAll()
                .stream()
                .map(mapper::ownerToDTO)
                .toList();
    }

    @Override
    @NonNull
    public final Optional<OwnerResponseDTO> getOwnerById(
            @NonNull final Long id
    ) {

        return ownerDao
                .findById(id)
                .map(mapper::ownerToDTO);
    }

    @Override
    @NonNull
    public final Optional<Long> getOwnerIdByEmail(
            @NonNull final String email
    ) {

        return ownerDao
                .findByEmail(email)
                .map(Owner::getId);
    }

    @Override
    @NonNull
    public final OwnerResponseDTO createOwner(
            @NonNull final OwnerCreateDTO ownerDto
    ) throws InvalidOperationException {

        Optional<Owner> owner = ownerDao.findByEmail(ownerDto.email());

        if (owner.isPresent()) {
            throw new InvalidOperationException("Owner with email " + ownerDto.email() + " already exists");
        }

        return mapper.ownerToDTO(
                ownerDao.create(ownerDto)
        );
    }

    @Override
    @NonNull
    public final OwnerResponseDTO updateOwner(
            @NonNull final OwnerUpdateDTO ownerDto
    ) throws ObjectNotFoundException {

        return mapper.ownerToDTO(
                ownerDao.update(ownerDto)
        );
    }

    @Override
    public final void deleteOwnerById(@NonNull final Long id) {
        ownerDao.deleteById(id);
    }
}
