package me.macao.percistence;

import me.macao.msdto.request.CatUpdateDTO;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.request.CatCreateDTO;

import java.util.Collection;
import java.util.Optional;

public interface CatDao {

    Collection<Cat> findAll();

    Optional<Cat> findById(final Long id);

    Cat create(final CatCreateDTO catDto);

    Cat update(final CatUpdateDTO catDto)
            throws ObjectNotFoundException;

    void deleteById(final Long id);
}
