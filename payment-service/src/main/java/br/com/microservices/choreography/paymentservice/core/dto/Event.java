package br.com.microservices.choreography.paymentservice.core.dto;


import br.com.microservices.choreography.paymentservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Data // criar getters e setters
@NoArgsConstructor // criar construtor vazio
@AllArgsConstructor // criar construtor com todos os atributos
@Builder //
public class Event {

    private String id;
    private String transactionId;
    private String orderId;
    private Order order;
    private String source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addToHistory(History history) {
        if (isEmpty(eventHistory)) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }
}
