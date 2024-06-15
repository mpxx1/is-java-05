package me.macao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class EntrypointApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntrypointApplication.class, args);
    }

}
