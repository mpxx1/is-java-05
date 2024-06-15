package me.macao.percistence;

import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.msdto.request.OwnerUpdateDTO;

import java.util.Collection;
import java.util.Optional;

public interface OwnerDao {

    Optional<Owner> findByEmail(final String email);

    Collection<Owner> findAll();

    Optional<Owner> findById(final Long id);

    Owner create(final OwnerCreateDTO ownerDto);

    Owner update(final OwnerUpdateDTO ownerDto)
            throws ObjectNotFoundException;

    void deleteById(final Long id);
}
