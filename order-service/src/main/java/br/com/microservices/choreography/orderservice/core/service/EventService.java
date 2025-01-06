package br.com.microservices.choreography.orderservice.core.service;

import br.com.microservices.choreography.orderservice.config.exception.ValidationException;
import br.com.microservices.choreography.orderservice.core.document.Event;
import br.com.microservices.choreography.orderservice.core.document.History;
import br.com.microservices.choreography.orderservice.core.document.Order;
import br.com.microservices.choreography.orderservice.core.dto.EventFilter;
import br.com.microservices.choreography.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.microservices.choreography.orderservice.core.enums.ESagaStatus.SUCCESS;
import static org.springframework.util.ObjectUtils.isEmpty;


@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private static final String CURRENT_SOURCE = "ORDER_SERVICE";
    private final EventRepository repository;

    public void  notifyEnding(Event event){
        // resetando algumas informações para caso tenha perdido
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} has been notified! TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }

    public List<Event> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilter(EventFilter filter) {
        validateEmptyFilters(filter);
        if (!isEmpty(filter.getOrderId())) {
            return findByOrderId(filter.getOrderId());
        } else {
            return findByTransactionId(filter.getTransactionId());
        }

    }

    private Event findByOrderId(String orderId) {
        return repository.findTop1ByOrderIdOrderByCreatedAtDesc(orderId).orElseThrow(() -> new ValidationException("Event not found"));
    }

    private Event findByTransactionId(String transactionId) {
        return repository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId).orElseThrow(() -> new ValidationException("Event not found"));
    }

    private void validateEmptyFilters(EventFilter filter) {
        if (isEmpty(filter.getOrderId()) && isEmpty(filter.getTransactionId())) {
            throw new ValidationException("OrderID or TransactionID must be informed");
        }
    }

    public Event save(Event event) {
        return repository.save(event);
    }

    public Event createEvent(Order order) {
        var event = Event
                .builder()
                .source(CURRENT_SOURCE)
                .status(SUCCESS)
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .order(order)
                .createdAt(LocalDateTime.now())
                .build();
        addHistory(event, "Saga started!");
        return save(event);
    }

    private void addHistory(Event event, String message) {
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }
}
