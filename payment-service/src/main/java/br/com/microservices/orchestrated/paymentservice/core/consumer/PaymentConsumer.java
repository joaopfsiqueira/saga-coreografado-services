package br.com.microservices.orchestrated.paymentservice.core.consumer;

import br.com.microservices.orchestrated.paymentservice.core.service.PaymentService;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumePaymentValidationSuccessEvent(String payload) {
        log.info("Receiving success event {} from payment-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizePayment(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-failure}"
    ) //esse metodo é um kafka listerner! uma função que consome do kafka
    public void ConsumePaymentValidationFailureEvent(String payload) {
        log.info("Receiving rollback event {} from payment-failure topic", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizeRefound(event);
    }


}
