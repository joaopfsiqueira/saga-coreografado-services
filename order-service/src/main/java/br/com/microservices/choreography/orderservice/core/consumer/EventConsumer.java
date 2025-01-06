package br.com.microservices.choreography.orderservice.core.consumer;

import br.com.microservices.choreography.orderservice.core.service.EventService;
import br.com.microservices.choreography.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final EventService service;
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notify-ending}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumeNotifyEndingEvent(String payload) {
        log.info("Receiving ending notification event {} from notify-ending topic", payload);
        var event = jsonUtil.toEvent(payload);
        service.notifyEnding(event);
    }


}
