package br.com.microservices.choreography.orderservice.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // permite que tenha handle global para todos os controllers
public class ExceptionGlobalHandler {

    @ExceptionHandler(ValidationException.class) //sempre vai capturar uma exceção do tipo ValidationException
    public ResponseEntity<?> handleValidationException(ValidationException validationException){
        // quando no código for lançado uma exceção do tipo ValidationException, o metodo handleValidationException será chamado
        // e ai ele vai retornar um ResponseEntity com o status 400 e a mensagem da exceção
        var details = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), validationException.getMessage());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}
