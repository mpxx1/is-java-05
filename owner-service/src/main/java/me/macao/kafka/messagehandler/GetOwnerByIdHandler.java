package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.msdto.request.IdRequest;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetOwnerByIdHandler
    implements MessageHandler {

    private final static String KEY = "get_owner_by_id";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final OwnerService ownerService;

    @Override
    public final String handle(final String value)
        throws JsonProcessingException {

        try {

            IdRequest request = mapper.readValue(value, IdRequest.class);
            Optional<OwnerResponseDTO> owner = ownerService.getOwnerById(request.id());

            if (owner.isEmpty())
                return mapper.writeValueAsString(
                        new ErrMap(
                                "Object not found",
                                "Owner with id " + request.id() + " not found"
                        )
                );

            return mapper.writeValueAsString(owner.get());

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
    public final boolean canHandle(final String key) {

        return KEY.equals(key);
    }
}
