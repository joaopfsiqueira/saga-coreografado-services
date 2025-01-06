package br.com.microservices.orchestrated.productvalidationservice.core.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "validation")
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gera um valor automaticamente
    private Integer id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist // Executa antes de salvar
    public void prePersist() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate // caso ja exista, só atualiza o update
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
