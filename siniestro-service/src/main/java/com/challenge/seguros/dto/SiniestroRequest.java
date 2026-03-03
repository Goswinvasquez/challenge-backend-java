package com.challenge.seguros.dto;

import java.math.BigDecimal;

public record SiniestroRequest(
        String numeroPoliza,
        String descripcion,
        BigDecimal montoReclamado
) {
}