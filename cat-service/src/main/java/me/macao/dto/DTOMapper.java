package me.macao.dto;

import me.macao.data.CatColor;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.percistence.Cat;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DTOMapper {

    public CatResponseDTO catToResponseDTO(
            final Cat cat,
            final Collection<Cat> reqsIn
    ) {

        return new CatResponseDTO(
                cat.getOwner(),
                cat.getId(),
                cat.getName(),
                cat.getBreed(),
                cat.getColor(),
                cat.getBirthday(),
                cat.getFriends()
                        .stream()
                        .map(Cat::getId)
                        .toList(),
                reqsIn
                        .stream()
                        .map(Cat::getId)
                        .toList(),
                cat.getReqsOut()
                        .stream()
                        .map(Cat::getId)
                        .toList()
        );
    }

    public final CatColor stringToColor(final String color) {

        return switch (color.toLowerCase()) {

            case "red", "r"     -> CatColor.RED;
            case "white", "w"   -> CatColor.WHITE;
            case "grey", "g"    -> CatColor.GREY;
            case "blue", "bl"   -> CatColor.BLUE;
            case "brown", "br"  -> CatColor.BROWN;
            case "orange", "o"  -> CatColor.ORANGE;
            default             -> CatColor.BLACK;
        };
    }
}
