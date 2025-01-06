package br.com.microservices.choreography.productvalidationservice.core.service;


import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.dto.History;
import br.com.microservices.choreography.productvalidationservice.core.dto.OrderProduct;
import br.com.microservices.choreography.productvalidationservice.core.model.Validation;
import br.com.microservices.choreography.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.choreography.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.choreography.productvalidationservice.core.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {


    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;


    public void validateExistingProducts(Event event) {
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception e) {
            log.error("Error validating existing products", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event), "");
    }

    private void validateProductsInformed(Event event) {
        if (isEmpty(event.getOrder()) || isEmpty(event.getOrder().getProducts())) {
            throw new ValidateException("Product list is empty");
        }

        if (isEmpty(event.getOrder().getId()) || isEmpty(event.getTransactionId())) {
            throw new ValidateException("Order ID or Transaction ID must be informed!");
        }
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidateException("There's another transactionId for this validation.");
        }
        event.getOrder().getProducts().forEach(product -> {
            validateProductInformed(product);
            validateExistingProduct(product.getProduct().getCode());
        });
    }

    private void validateProductInformed(OrderProduct product) {
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())) {
            throw new ValidateException("Product must be informed!");
        }
    }

    private void validateExistingProduct(String code) {
        if (!productRepository.existsByCode(code)) {
            throw new ValidateException("Product not found!");
        }
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation
                .builder()
                .orderId(event.getOrder().getId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products are validated successfully!");
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

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to validate products: ".concat(message));
    }

    // busca no banco de dados a validação e verifica se foi bem sucedida
    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executed on Product Validation Service");
        producer.sendEvent(jsonUtil.toJson(event), "");
    }

    // busca no banco de dados, se não existir insere com status de falha.
    private void changeValidationToFail(Event event) {
       validationRepository.findByOrderIdAndTransactionId(event.getOrder().getId(),
               event.getTransactionId())
               .ifPresentOrElse(validation -> {
           validation.setSuccess(false);
           validationRepository.save(validation);
       }, () -> createValidation(event, false));
    }
}
