package me.macao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class OwnerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OwnerServiceApplication.class, args);
    }

}
