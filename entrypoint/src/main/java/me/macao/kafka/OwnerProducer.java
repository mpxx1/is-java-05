package me.macao.kafka;

import me.macao.exception.DataTransferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OwnerProducer {

    private final ReplyingKafkaTemplate<String, Object, Object> ownerReplyTemplate;

    public OwnerProducer(
            @Qualifier("replyingOwnerKafkaTemplate")
            final ReplyingKafkaTemplate<String, Object, Object> ownerReplyTemplate
    ) {
        this.ownerReplyTemplate = ownerReplyTemplate;
    }

    @Value("${spring.kafka.request.topics[0]}")
    private String ownerRequestTopic;

    public Object kafkaRequestReply(final String key, final Object request)
            throws DataTransferException {

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(ownerRequestTopic, key, request);

        RequestReplyFuture<String, Object, Object> replyFuture = ownerReplyTemplate.sendAndReceive(record);

        try {

            SendResult<String, Object> sendResult = replyFuture
                    .getSendFuture()
                    .get(10, TimeUnit.SECONDS);

            ConsumerRecord<String, Object> consumerRecord = replyFuture
                    .get(20, TimeUnit.SECONDS);

            return consumerRecord.value();

        } catch (Exception e) {

            throw new DataTransferException("Internal error, something went wrong.\nTry again later");
        }
    }
}
