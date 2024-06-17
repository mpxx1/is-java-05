package me.macao.percistence;

import me.macao.dto.DetailsCreateDTO;
import me.macao.dto.DetailsInternalUpdDTO;
import me.macao.exception.ObjectNotFoundException;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    Optional<User> findByEmail(final String email);

    Collection<User> findAll();

    User findById(final Long id)
            throws ObjectNotFoundException;

    User create(final DetailsCreateDTO userDto);

    User update(final DetailsInternalUpdDTO userDto)
            throws ObjectNotFoundException;

    void deleteById(final Long id);
}
