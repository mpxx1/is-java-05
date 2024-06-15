package me.macao.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.macao.kafka.messagehandler.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final List<MessageHandler> handlerList;

    @SneakyThrows
    @KafkaListener(
            groupId="${spring.kafka.group.id[0]}",
            topics = "${spring.kafka.request.topics[0]}"
    )
    @SendTo
    public Message<?> listen(ConsumerRecord<String, Object> rec) {
        String key = rec.key();
        String body = rec.value().toString();

        String response = "";
        for (MessageHandler handler : handlerList) {
            if (handler.canHandle(key)) {
                response = handler.handle(body);
                break;
            }
        }

        return MessageBuilder
                .withPayload(response)
                .build();
    }
}
