package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.request.FriendOperation;
import me.macao.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RmFriendHandler
    implements MessageHandler {

    private final static String KEY = "remove_friend";
    private final CatService service;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            FriendOperation operation = mapper.readValue(value, FriendOperation.class);
            service.deleteFriendOrRequest(operation.src(), operation.tgt());

            var cat = service.getCatById(operation.src());

            if (cat.isEmpty())
                return mapper.writeValueAsString(
                        new ErrMap(
                                "Object not found",
                                "Cat with id " + operation.src() + " not found"
                        )
                );

            return mapper.writeValueAsString(cat.get());

        } catch (Exception e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Data transfer",
                            "[CAT SERVICE] BAD REQUEST BODY (possibly sth with json)" +
                                    "\nActual: " + e.getMessage()
                    )
            );
        }
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
