package br.com.microservices.choreography.productvalidationservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j // logar
@RequiredArgsConstructor // apenas gera no constructor dos atributos o que é final.
@Component // converte em um singleton para sempre podermos injetar a dependencia dessa classe
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String payload, String topic){
        try {
            log.info("Sending event to topic {} with data {}", topic, payload);
            kafkaTemplate.send(topic, payload);
        } catch (Exception e) {
            log.error("Error sending event to topic {} with data {}", topic, payload, e);
        }
    }
}
