package me.macao.service;

import lombok.NonNull;
import me.macao.dto.CatUpdateDTO;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.msdto.request.CatCreateDTO;

import java.util.Collection;
import java.util.Optional;

public interface CatService {

    @NonNull
    Collection<CatResponseDTO> getAllCats();

    @NonNull
    Collection<CatResponseDTO> getCatsByUserId(@NonNull final Long id);

    @NonNull
    Optional<CatResponseDTO> getCatById(@NonNull final Long id);

    @NonNull
    CatResponseDTO createCat(@NonNull final CatCreateDTO userDto);

    @NonNull
    CatResponseDTO updateCat(
            @NonNull final CatUpdateDTO catDto
    ) throws ObjectNotFoundException;

    void deleteCatById(@NonNull final Long id);

    void addFriendOrRequest(
            @NonNull final Long srcId,
            @NonNull final Long dstId
    ) throws ObjectNotFoundException;

    void deleteFriendOrRequest(
            @NonNull final Long srcId,
            @NonNull final Long dstId
    ) throws ObjectNotFoundException;
}
