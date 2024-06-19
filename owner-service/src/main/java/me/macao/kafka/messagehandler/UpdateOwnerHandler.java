package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.exception.ObjectNotFoundException;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.OwnerUpdateDTO;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UpdateOwnerHandler
    implements MessageHandler {

    private final static String KEY = "update_owner";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            OwnerUpdateDTO updateDTO = mapper.readValue(value, OwnerUpdateDTO.class);
            OwnerResponseDTO responseDTO = ownerService.updateOwner(updateDTO);

            return mapper.writeValueAsString(responseDTO);

        } catch (ObjectNotFoundException e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Object not found",
                            "[OWNER SERVICE] " + e.getMessage()
                    )
            );
        }
    }

    @Override
    public boolean canHandle(String key) {

        return KEY.equals(key);
    }
}
