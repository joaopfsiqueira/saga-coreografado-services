package br.com.microservices.orchestrated.paymentservice.core.service;

import br.com.microservices.orchestrated.paymentservice.core.dto.Event;
import br.com.microservices.orchestrated.paymentservice.core.dto.History;
import br.com.microservices.orchestrated.paymentservice.core.dto.OrderProduct;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.paymentservice.core.model.Payment;
import br.com.microservices.orchestrated.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.core.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus.ROLLBACK_PENDING;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_SUM_VALUE = 0.0;
    private static final Double MIN_AMOUNT_VALUE = 0.1;

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final PaymentRepository paymentRepository;

    public void realizePayment(Event event) {
        try {
            checkCurrentValidation(event);
            createPendingPayment(event);
            var payment = findByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            changePaymentToSuccess(payment);
            handleSuccess(event);
        } catch (Exception e) {
            log.error("Error realizing payment", e);
            handleFailCurrentNotExecuted(event, e.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidateException("There's another transactionId for this validation.");
        }
    }

    private void createPendingPayment(Event event) {
        var payment = Payment.builder()
                .orderId(event.getOrder().getId())
                .transactionId(event.getTransactionId())
                .totalItems(calculateTotalItems(event))
                .totalAmount(calculateAmount(event))
                .build();
        save(payment);
        setEventAmountItems(event, payment);
    }

    // metodo que vai calcular o valor total da compra, o reduce vai somar o valor de cada produto e incrementar
    private double calculateAmount(Event event) {
       return event.getOrder().getProducts().stream()
               .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private int calculateTotalItems(Event event) {
        return event.getOrder().getProducts().stream()
                .map(OrderProduct::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }

    private void setEventAmountItems(Event event, Payment payment) {
        event.getOrder().setTotalAmount(payment.getTotalAmount());
        event.getOrder().setTotalItems(payment.getTotalItems());
    }

    private void changePaymentToSuccess(Payment payment) {
        payment.setPaymentStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Payment realized with success");
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

    private void validateAmount(Double amount) {
        if(amount < MIN_AMOUNT_VALUE) {
            throw new ValidateException("The minimum amount value is 0.1".concat(MIN_AMOUNT_VALUE.toString()));
        }
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to realize payment: ".concat(message));
    }

    public void realizeRefound(Event event) {
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        try {
            changePaymentStatusToRefound(event);
            addHistory(event, "Rollback executed for Payment!");
        } catch (Exception e) {
            addHistory(event, "Rollback not executed for Payment!".concat(e.getMessage()));
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void changePaymentStatusToRefound(Event event) {
        var payment = findByOrderIdAndTransactionId(event);
        payment.setPaymentStatus(EPaymentStatus.REFOUND);
        setEventAmountItems(event, payment);
        save(payment);
    }

    private Payment findByOrderIdAndTransactionId(Event event) {
        return paymentRepository.findByOrderIdAndTransactionId(event.getOrder().getId(), event.getTransactionId())
                .orElseThrow(() -> new ValidateException("Payment not found by OrderId and TransactionId"));
    }

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
