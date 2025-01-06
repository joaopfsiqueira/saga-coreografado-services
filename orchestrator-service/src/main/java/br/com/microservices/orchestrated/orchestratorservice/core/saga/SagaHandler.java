package br.com.microservices.orchestrated.orchestratorservice.core.saga;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;

// classe utilitária, por isso final. Quando eu digo que é final eu estou dizendo que não pode ser extendida, então só vai ter métodos estáticos
public final class SagaHandler {

    private SagaHandler() {

    }

    // matriz de objetos, onde cada objeto é um array de objetos
    public static final Object[][] SAGA_HANDLER = {
            {EEventSource.ORCHESTRATOR, ESagaStatus.SUCCESS, ETopics.PRODUCT_VALIDATION_SUCCESS},
            {EEventSource.ORCHESTRATOR, ESagaStatus.FAIL, ETopics.FINISH_FAILURE},

            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.PRODUCT_VALIDATION_FAILURE},
            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAILURE},
            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.SUCCESS, ETopics.PAYMENT_SUCCESS},

            {EEventSource.PAYMENT_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.PAYMENT_FAILURE},
            {EEventSource.PAYMENT_SERVICE, ESagaStatus.FAIL, ETopics.PRODUCT_VALIDATION_FAILURE},
            {EEventSource.PAYMENT_SERVICE, ESagaStatus.SUCCESS, ETopics.INVENTORY_SUCCESS},

            {EEventSource.INVENTORY_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.INVENTORY_FAILURE},
            {EEventSource.INVENTORY_SERVICE, ESagaStatus.FAIL, ETopics.PAYMENT_FAILURE},
            {EEventSource.INVENTORY_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS},
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;

}
