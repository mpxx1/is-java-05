package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import me.macao.msdto.reply.CatResponseDTO;
import me.macao.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GetAllCatsHandler
    implements MessageHandler {

    private final static String KEY = "get_all_cats";
    private final CatService service;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String handle(final String value)
            throws JsonProcessingException {

        Collection<CatResponseDTO> cats = service.getAllCats();

        return mapper.writeValueAsString(cats);
    }

    @Override
    public final boolean canHandle(final String key) {
        return KEY.equals(key);
    }
}
