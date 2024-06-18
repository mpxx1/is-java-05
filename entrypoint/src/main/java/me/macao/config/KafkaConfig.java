package me.macao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.group.id[0]}")
    private String ownerGroupId;

    @Value("${spring.kafka.reply.topics[0]}")
    private String ownerReplyTopic;

    @Value("${spring.kafka.group.id[1]}")
    private String catGroupId;

    @Value("${spring.kafka.reply.topics[1]}")
    private String catReplyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object>
    replyingOwnerKafkaTemplate(
            ProducerFactory<String, Object> pf,
            ConcurrentKafkaListenerContainerFactory<String, Object> factory
    ) {
        ConcurrentMessageListenerContainer<String, Object> replyContainer =
                factory.createContainer(ownerReplyTopic);

        replyContainer
                .getContainerProperties()
                .setMissingTopicsFatal(false);
        replyContainer
                .getContainerProperties()
                .setGroupId(ownerGroupId);

        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesOwnerContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory
    ) {
        ConcurrentMessageListenerContainer<String, Object> repliesContainer =
                containerFactory.createContainer(ownerReplyTopic);

        repliesContainer.getContainerProperties().setGroupId(ownerGroupId);
        repliesContainer.setAutoStartup(false);

        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object>
    replyingCatKafkaTemplate(
            ProducerFactory<String, Object> pf,
            ConcurrentKafkaListenerContainerFactory<String, Object> factory
    ) {
        ConcurrentMessageListenerContainer<String, Object> replyContainer =
                factory.createContainer(catReplyTopic);

        replyContainer
                .getContainerProperties()
                .setMissingTopicsFatal(false);
        replyContainer
                .getContainerProperties()
                .setGroupId(catGroupId);

        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesCatContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory
    ) {
        ConcurrentMessageListenerContainer<String, Object> repliesContainer =
                containerFactory.createContainer(catReplyTopic);

        repliesContainer.getContainerProperties().setGroupId(catGroupId);
        repliesContainer.setAutoStartup(false);

        return repliesContainer;
    }
}
