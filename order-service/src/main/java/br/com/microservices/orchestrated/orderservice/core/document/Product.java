package br.com.microservices.orchestrated.orderservice.core.document;

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
