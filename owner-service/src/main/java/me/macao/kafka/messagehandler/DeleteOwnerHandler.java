package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.EmptyReply;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.request.IdRequest;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeleteOwnerHandler
    implements MessageHandler {

    private final static String KEY = "delete_owner";
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final OwnerService ownerService;

    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            IdRequest request = mapper.readValue(value, IdRequest.class);
            ownerService.deleteOwnerById(request.id());

            return mapper.writeValueAsString(new EmptyReply());

        } catch (Exception e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Data transfer",
                            "[OWNER SERVICE] BAD REQUEST BODY (possibly sth with json)"
                    )
            );
        }
    }

    @Override
    public boolean canHandle(String key) {

        return KEY.equals(key);
    }
}
