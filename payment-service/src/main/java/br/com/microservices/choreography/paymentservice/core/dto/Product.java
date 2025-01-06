package br.com.microservices.choreography.paymentservice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // criar getters e setters
@NoArgsConstructor // criar construtor vazio
@AllArgsConstructor // criar construtor com todos os atributos
public class Product {

    private String code;
    private double unitValue;
}
