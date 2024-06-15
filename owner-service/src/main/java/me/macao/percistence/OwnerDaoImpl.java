package me.macao.percistence;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.msdto.request.OwnerUpdateDTO;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class OwnerDaoImpl
    implements OwnerDao {

    private JpaOwnerDao ownerDao;

    @Override
    public Optional<Owner> findByEmail(
            @NonNull final String email
    ) {
        return ownerDao
                .findAll()
                .stream()
                .filter(x -> x.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Collection<Owner> findAll() {
        return ownerDao.findAll();
    }

    @Override
    public Optional<Owner> findById(final Long id) {
        return ownerDao
                .findAll()
                .stream()
                .filter(x -> Objects.equals(x.getId(), id))
                .findFirst();
    }

    @Override
    public Owner create(
            final OwnerCreateDTO ownerDto
    ) {
        return ownerDao.save(
                Owner
                        .builder()
                        .id(ownerDto.id())
                        .email(ownerDto.email())
                        .build()
        );
    }

    @Override
    @Transactional
    public Owner update(final OwnerUpdateDTO ownerDto)
            throws ObjectNotFoundException {

        Owner owner = ownerDao
                .findById(ownerDto.id())
                .orElseThrow(() -> new ObjectNotFoundException("Owner with id " + ownerDto.id() + " not found"));

        if (ownerDto.email() != null)
            owner.setEmail(ownerDto.email());

        if (ownerDto.name() != null)
            owner.setName(ownerDto.name());

        if (ownerDto.birthday() != null)
            owner.setBirthday(ownerDto.birthday());

        return owner;
    }

    @Override
    public void deleteById(final Long id) {
        ownerDao.deleteById(id);
    }
}
