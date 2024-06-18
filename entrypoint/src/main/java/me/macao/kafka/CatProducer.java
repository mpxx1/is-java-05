package me.macao.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.macao.exception.DataTransferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CatProducer {

    private final ReplyingKafkaTemplate<String, Object, Object> catReplyTemplate;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public CatProducer(
            @Qualifier("replyingCatKafkaTemplate")
            final ReplyingKafkaTemplate<String, Object, Object> catReplyTemplate
    ) {
        this.catReplyTemplate = catReplyTemplate;
    }

    @Value("${spring.kafka.request.topics[1]}")
    private String catRequestTopic;

    public String kafkaRequestReply(final String key, final Object request)
            throws DataTransferException {

        try {

            ProducerRecord<String, Object> record =
                    new ProducerRecord<>(
                            catRequestTopic,
                            key,
                            mapper.writeValueAsString(request)
                    );

            RequestReplyFuture<String, Object, Object> replyFuture = catReplyTemplate.sendAndReceive(record);

            replyFuture
                    .getSendFuture()
                    .get(10, TimeUnit.SECONDS);

            ConsumerRecord<String, Object> consumerRecord = replyFuture
                    .get(20, TimeUnit.SECONDS);

            return consumerRecord.value().toString();

        } catch (Exception e) {

            throw new DataTransferException("Internal error, something went wrong.\nTry again later");
        }
    }
}
