package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.OwnerResponseDTO;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetAllOwnersHandler
        implements MessageHandler {

    private final static String KEY = "get_all_owners";
    private final OwnerService ownerService;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public final String handle(final String value)
            throws JsonProcessingException {

        Collection<OwnerResponseDTO> owners = ownerService.getOwners();

        return mapper.writeValueAsString(owners);
    }

    @Override
    public final boolean canHandle(final String key) {
        return KEY.equals(key);
    }
}
