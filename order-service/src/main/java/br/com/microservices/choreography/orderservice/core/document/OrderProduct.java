package br.com.microservices.choreography.orderservice.core.document;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // criar getters e setters
@NoArgsConstructor // criar construtor vazio
@AllArgsConstructor // criar construtor com todos os atributos
public class OrderProduct {

    private Product product;
    private int quantity;
}
