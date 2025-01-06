package br.com.microservices.orchestrated.orchestratorservice.core.dto;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data // criar getters e setters
@NoArgsConstructor // criar construtor vazio
@AllArgsConstructor // criar construtor com todos os atributos
@Builder //
public class History {

    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
