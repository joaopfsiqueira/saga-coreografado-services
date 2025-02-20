package br.com.microservices.choreography.orderservice.core.service;

import br.com.microservices.choreography.orderservice.core.document.Order;
import br.com.microservices.choreography.orderservice.core.dto.OrderRequest;
import br.com.microservices.choreography.orderservice.core.producer.SagaProducer;
import br.com.microservices.choreography.orderservice.core.repository.OrderRepository;
import br.com.microservices.choreography.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.messaging.rsocket.PayloadUtils.createPayload;

@Service
@AllArgsConstructor
public class OrderService {

    private static final String TRANSACTION_ID_FORMAT = "%s_%s";

    private final EventService eventService; // salvar eventos no banco de dados.
    private final SagaProducer producer; // enviar eventos para o broker.
    private final JsonUtil jsonUtil; // converter objetos em strings.
    private final OrderRepository repository;

    public Order createOrder(OrderRequest orderRequest) {
        var order = Order.builder()
                .products(orderRequest.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(String.format(TRANSACTION_ID_FORMAT, Instant.now().toEpochMilli(), UUID.randomUUID())) //  gerando valor único.
                .build();
        repository.save(order);

        producer.sendEvent(jsonUtil.toJson(eventService.createEvent(order)));
        return order;
    }
}
