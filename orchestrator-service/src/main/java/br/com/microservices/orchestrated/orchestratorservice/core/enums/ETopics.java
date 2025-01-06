package br.com.microservices.orchestrated.orchestratorservice.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // retornar só os atributos
@AllArgsConstructor // gera constructor com todos os argumetos
public enum ETopics {

    START_SAGA("start-saga"),
    BASE_ORCHESTRATOR("orchestrator"),
    FINISH_SUCCESS("finish-success"),
    FINISH_FAILURE("finish-failure"),
    PRODUCT_VALIDATION_SUCCESS("product-validation-success"),
    PRODUCT_VALIDATION_FAILURE("product-validation-failure"),
    INVENTORY_SUCCESS("inventory-success"),
    INVENTORY_FAILURE("inventory-failure"),
    PAYMENT_SUCCESS("payment-success"),
    PAYMENT_FAILURE("payment-failure"),
    NOTIFY_ENDING("notify-ending");


    private final String Topic;
}
