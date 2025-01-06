package br.com.microservices.choreography.orderservice.config.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
public class ValidationException extends  RuntimeException {

    public ValidationException(String message) {
        super(message); // Passa a mensagem para a superclasse do RuntimeException

    }
}
