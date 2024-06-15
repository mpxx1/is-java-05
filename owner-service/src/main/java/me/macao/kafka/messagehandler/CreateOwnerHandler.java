package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.exception.InvalidOperationException;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.OwnerCreateDTO;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CreateOwnerHandler
    implements MessageHandler {

    private final static String KEY = "create_owner";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final OwnerService ownerService;

    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            OwnerCreateDTO createDto = mapper.readValue(value, OwnerCreateDTO.class);
            OwnerResponseDTO responseDto = ownerService.createOwner(createDto);

            return mapper.writeValueAsString(responseDto);

        } catch (InvalidOperationException e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Invalid operation",
                            e.getMessage()
                    )
            );
        } catch (Exception e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Data transfer",
                            "BAD REQUEST BODY (possibly sth with json)"
                    )
            );
        }
    }

    @Override
    public boolean canHandle(String key) {

        return KEY.equals(key);
    }
}
