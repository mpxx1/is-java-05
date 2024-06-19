package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.EmptyReply;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.request.IdRequest;
import me.macao.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetCatByIdHandler
    implements MessageHandler {

    private final static String KEY = "get_cat_by_id";
    private final CatService service;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            IdRequest request = mapper.readValue(value, IdRequest.class);
            var cat = service.getCatById(request.id());

            if (cat.isEmpty())
                return mapper.writeValueAsString(
                        new ErrMap(
                                "Object not found",
                                "[CAT SERVICE] Cat with id " + request.id() + " not found"
                        )
                );

            return mapper.writeValueAsString(cat.get());

        } catch (Exception e) {

            return mapper.writeValueAsString(
                    new ErrMap(
                            "Data transfer",
                            "[CAT SERVICE] BAD REQUEST BODY (possibly sth with json)"
                    )
            );
        }
    }

    @Override
    public boolean canHandle(String key) {
        return KEY.equals(key);
    }
}
