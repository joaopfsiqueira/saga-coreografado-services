package br.com.microservices.orchestrated.orchestratorservice.core.enums;

// Enum que representa a origem do evento
public enum EEventSource {
    ORCHESTRATOR,
    PRODUCT_VALIDATION_SERVICE,
    PAYMENT_SERVICE,
    INVENTORY_SERVICE
}
