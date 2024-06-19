package me.macao.controller;

import lombok.NonNull;
import me.macao.dto.CatInitDTO;
import me.macao.dto.CatUpdContDTO;
import me.macao.exception.AccessDeniedException;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.reply.CatResponseDTO;

import java.util.Collection;

public interface CatController {

    @NonNull
    Collection<CatResponseDTO> getAllCats()
            throws AccessDeniedException;

    @NonNull
    Collection<CatResponseDTO> getMyCats()
            throws AccessDeniedException;

    @NonNull
    CatResponseDTO getCatById(final long id)
            throws AccessDeniedException, ObjectNotFoundException;

    @NonNull
    CatResponseDTO createCat(@NonNull final CatInitDTO initDTO);

    @NonNull
    CatResponseDTO updateCat(
            final long id, @NonNull final CatUpdContDTO updateDTO
    ) throws AccessDeniedException, ObjectNotFoundException;

    void deleteCat(final long id)
            throws AccessDeniedException;
}
