package br.com.microservices.orchestrated.orchestratorservice.core.consumer;

import br.com.microservices.orchestrated.orchestratorservice.core.service.OrchestratorService;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {

    private final JsonUtil jsonUtil;
    private final OrchestratorService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumeStartSagaEvent(String payload) {
        log.info("Receiving  event {} from start-saga topic", payload);
        var event = jsonUtil.toEvent(payload);
        service.startSaga(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.orchestrator}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumeOrchestratorEvent(String payload) {
        log.info("Receiving event {} from orchestrator topic", payload);
        var event = jsonUtil.toEvent(payload);
        service.continueSaga(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumeFinishSuccessEvent(String payload) {
        log.info("Receiving event {} from finish-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        service.finishSagaSuccess(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-failure}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumeFinishFailureEvent(String payload) {
        log.info("Receiving event {} from finish-failure topic", payload);
        var event = jsonUtil.toEvent(payload);
        service.finishSagaFail(event);
    }


}
