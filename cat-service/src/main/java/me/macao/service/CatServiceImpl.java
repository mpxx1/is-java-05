package me.macao.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.macao.msdto.request.CatUpdateDTO;
import me.macao.dto.DTOMapper;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.msdto.request.CatCreateDTO;
import me.macao.percistence.Cat;
import me.macao.percistence.CatDao;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CatServiceImpl
    implements CatService {

    private CatDao catDao;
    private DTOMapper dtoMapper;

    @Override
    @NonNull
    public Collection<CatResponseDTO> getAllCats() {

        return catDao
                .findAll()
                .stream()
                .map(
                        cat -> dtoMapper.catToResponseDTO(
                                cat, getReqsIn(cat)
                        )
                )
                .toList();
    }

    @Override
    @NonNull
    public Collection<CatResponseDTO> getCatsByUserId(
            @NonNull final Long id
    ) {

        return catDao
                .findAll()
                .stream()
                .filter(x -> Objects.equals(x.getOwner(), id))
                .map(
                        cat -> dtoMapper.catToResponseDTO(
                                cat, getReqsIn(cat)
                        )
                )
                .toList();
    }

    @Override
    @NonNull
    public Optional<CatResponseDTO> getCatById(
            @NonNull final Long id
    ) {

        Optional<Cat> cat = catDao.findById(id);

        if (cat.isEmpty())
            return Optional.empty();

        return cat
                .map(
                        c -> dtoMapper
                                .catToResponseDTO(c, getReqsIn(c))
                );
    }

    @Override
    @NonNull
    public CatResponseDTO createCat(
            @NonNull final CatCreateDTO catDto
    ) {

        Cat cat = catDao.create(catDto);

        return dtoMapper.catToResponseDTO(cat, getReqsIn(cat));
    }

    @Override
    @NonNull
    public CatResponseDTO updateCat(
            @NonNull final CatUpdateDTO catDto
    ) throws ObjectNotFoundException {

        Cat cat = catDao.update(catDto);

        return dtoMapper.catToResponseDTO(cat, getReqsIn(cat));
    }

    @Override
    public void deleteCatById(@NonNull final Long id) {
        catDao.deleteById(id);
    }

    @Override
    public void deleteCatsByUserId(@NonNull final Long userId) {

        var cats = getCatsByUserId(userId);

        if (!cats.isEmpty()) {
            cats
                    .forEach(
                            c -> catDao.deleteById(c.catId())
                    );
        }
    }

    @Override
    public void addFriendOrRequest(
            @NonNull final Long srcId,
            @NonNull final Long dstId
    ) throws ObjectNotFoundException {

        if (Objects.equals(srcId, dstId)) return;

        Cat srcCat = catDao
                .findById(srcId)
                .orElseThrow(
                        () -> new ObjectNotFoundException("Cat with id " + srcId + " not found")
                );

        Cat dstCat = catDao
                .findById(dstId)
                .orElseThrow(
                        () -> new ObjectNotFoundException("Cat with id " + dstId + " not found")
                );

        if (getReqsIn(srcCat).contains(dstCat)) {

            Collection<Long> reqsOut = new ArrayList<>(dstCat
                    .getReqsOut()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            Collection<Long> friends = new ArrayList<>(dstCat
                    .getFriends()
                    .stream()
                    .map(Cat::getId)
                    .toList());

            friends.add(srcId);
            reqsOut.remove(srcId);

            CatUpdateDTO catDto = new CatUpdateDTO(
                    dstId,
                    null, null, null, null, null,
                    friends,
                    reqsOut
            );

            catDao.update(catDto);

            friends = new ArrayList<>(srcCat
                    .getFriends()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            friends.add(dstId);

            catDto = new CatUpdateDTO(
                    srcId,
                    null, null, null, null, null,
                    friends,
                    null
            );

            catDao.update(catDto);
        } else {

            Collection<Long> reqsOut = new ArrayList<>(srcCat
                    .getReqsOut()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            reqsOut.add(dstId);

            CatUpdateDTO catDto = new CatUpdateDTO(
                    srcId,
                    null, null, null, null, null, null,
                    reqsOut
            );

            catDao.update(catDto);
        }
    }

    @Override
    public void deleteFriendOrRequest(
            @NonNull final Long srcId,
            @NonNull final Long dstId
    ) throws ObjectNotFoundException {

        if (Objects.equals(srcId, dstId)) return;

        Cat srcCat = catDao
                .findById(srcId)
                .orElseThrow(
                        () -> new ObjectNotFoundException("Cat with id " + srcId + " not found")
                );

        Cat dstCat = catDao
                .findById(dstId)
                .orElseThrow(
                        () -> new ObjectNotFoundException("Cat with id " + dstId + " not found")
                );

        if (srcCat.getFriends().contains(dstCat)) {

            Collection<Long> reqsOut = new ArrayList<>(dstCat
                    .getReqsOut()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            Collection<Long> friends = new ArrayList<>(dstCat
                    .getFriends()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            friends.remove(srcId);

            if (!reqsOut.contains(srcId))
                reqsOut.add(srcId);

            CatUpdateDTO catDto = new CatUpdateDTO(
                    dstId,
                    null, null, null, null, null,
                    friends,
                    reqsOut
            );

            catDao.update(catDto);

            friends = new ArrayList<>(srcCat
                    .getFriends()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            friends.remove(dstId);
            catDto = new CatUpdateDTO(
                    srcId,
                    null, null, null, null, null,
                    friends,
                    null
            );

            catDao.update(catDto);
        } else {

            Collection<Long> reqsOut = new ArrayList<>(srcCat
                    .getReqsOut()
                    .stream()
                    .map(Cat::getId)
                    .toList());
            reqsOut.remove(dstId);

            CatUpdateDTO catDto = new CatUpdateDTO(
                    srcId,
                    null, null, null, null, null, null,
                    reqsOut
            );

            catDao.update(catDto);
        }
    }

    @NonNull
    private Collection<Cat> getReqsIn(@NonNull Cat cat) {

        return new ArrayList<>(
                catDao
                        .findAll()
                        .stream()
                        .filter(c -> c.getReqsOut().contains(cat))
                        .toList()
        );
    }
}
