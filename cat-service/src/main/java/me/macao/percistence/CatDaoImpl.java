package me.macao.percistence;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.macao.msdto.request.CatUpdateDTO;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.request.CatCreateDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CatDaoImpl
    implements CatDao {

    private JpaCatDao catDao;

    @Override
    public Collection<Cat> findAll() {

        return catDao.findAll();
    }

    @Override
    public Optional<Cat> findById(final Long id) {

        return catDao.findById(id);
    }

    @Override
    public Cat create(final CatCreateDTO catDto) {

        return catDao.save(
                Cat
                        .builder()
                        .owner(catDto.owner())
                        .name(catDto.name())
                        .breed(catDto.breed())
                        .color(catDto.color())
                        .birthday(catDto.birthday())
                        .build()
        );
    }

    @Override
    @Transactional
    public Cat update(final CatUpdateDTO catDto)
            throws ObjectNotFoundException {

        Cat cat = catDao
                .findById(catDto.id())
                .orElseThrow(() -> new ObjectNotFoundException("Cat with id " + catDto.id() + " not found"));

        if (catDto.owner() != null)
            cat.setOwner(catDto.owner());

        if (catDto.name() != null)
            cat.setName(catDto.name());

        if (catDto.breed() != null)
            cat.setBreed(catDto.breed());

        if (catDto.color() != null)
            cat.setColor(catDto.color());

        if (catDto.birthday() != null)
            cat.setBirthday(catDto.birthday());

        try {

            if (catDto.friends() != null) {
                Collection<Long> ids = new ArrayList<>(catDto.friends());
                Collection<Cat> friends = ids
                        .stream()
                        .map(c -> catDao
                                .findById(c)
                                .get())
                        .toList();

                cat.setFriends(friends);
            }

            if (catDto.reqsOut() != null) {
                Collection<Long> ids = new ArrayList<>(catDto.reqsOut());
                Collection<Cat> reqsOut = ids
                        .stream()
                        .map(c -> catDao
                                .findById(c)
                                .get())
                        .toList();

                cat.setReqsOut(reqsOut);
            }


        } catch (NoSuchElementException e) {

            throw new ObjectNotFoundException("Can't find cat with requested id");
        }

        return cat;
    }

    @Override
    public void deleteById(final Long id) {
        catDao.deleteById(id);
    }
}
