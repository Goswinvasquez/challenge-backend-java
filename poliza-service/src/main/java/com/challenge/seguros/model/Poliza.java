package com.challenge.seguros.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("POLIZAS")
public record Poliza(
        @Id
        Long id,
        String numeroPoliza,
        String dniCliente,
        EstadoPoliza estado,
        BigDecimal montoCobertura
) {
    public boolean isActiva() {
        return EstadoPoliza.ACTIVA.equals(this.estado);
    }
}