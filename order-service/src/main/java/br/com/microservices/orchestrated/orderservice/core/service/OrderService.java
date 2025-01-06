package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.document.Order;
import br.com.microservices.orchestrated.orderservice.core.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.core.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.core.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

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

        producer.sendEvent(jsonUtil.toJson(createPayload(order)));
        return order;
    }

    private Event createPayload(Order order) {
        var event = Event.builder()
                .transactionId(order.getTransactionId())
                .orderId(order.getId())
                .order(order)
                .source("order-service")
                .status("ORDER_CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        eventService.save(event);
        return event;
    }
}
