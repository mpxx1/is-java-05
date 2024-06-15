package me.macao.dto;

import me.macao.percistence.Owner;
import me.macao.msdto.reply.OwnerResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class DTOMapper {

    public final OwnerResponseDTO ownerToDTO(final Owner owner) {

        return new OwnerResponseDTO(
                owner.getId(),
                owner.getName(),
                owner.getBirthday()
        );
    }
}
