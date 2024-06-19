package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.msdto.reply.ErrMap;
import me.macao.msdto.request.IdRequest;
import me.macao.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetCatsByUserIdHandler
    implements MessageHandler {

    private final static String KEY = "get_cats_by_user_id";
    private final CatService service;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String handle(String value)
            throws JsonProcessingException {

        try {

            IdRequest request = mapper.readValue(value, IdRequest.class);
            Collection<CatResponseDTO> cats = service
                    .getCatsByUserId(request.id());

            return mapper.writeValueAsString(cats);

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
