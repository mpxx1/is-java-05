package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.reply.IdReply;
import me.macao.msdto.request.EmailRequest;
import me.macao.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetOwnerIdByEmailHandler
    implements MessageHandler {

    private final static String KEY = "get_owner_id_by_email";
    private final ObjectMapper mapper = new ObjectMapper();
    private final OwnerService ownerService;

    @Override
    public final String handle(final String value)
            throws JsonProcessingException {

        try {

            EmailRequest request = mapper.readValue(value, EmailRequest.class);
            Optional<Long> id = ownerService.getOwnerIdByEmail(request.email());

            if (id.isEmpty())
                return mapper.writeValueAsString(
                        new ErrMap(
                                "Object not found",
                                "Owner with email " + request.email() + " not found"
                        )
                );

            return mapper.writeValueAsString(
                    new IdReply(id.get())
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
    public final boolean canHandle(final String key) {

        return KEY.equals(key);
    }
}
