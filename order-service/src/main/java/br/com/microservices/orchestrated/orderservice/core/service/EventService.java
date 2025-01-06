package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilter;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
@AllArgsConstructor
@Slf4j
public class EventService {

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
}
