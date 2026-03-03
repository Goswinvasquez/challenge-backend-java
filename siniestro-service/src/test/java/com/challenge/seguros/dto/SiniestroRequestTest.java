package com.challenge.seguros.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SiniestroRequestTest {

    @Test
    @DisplayName("Debe crear un SiniestroRequest y exponer sus datos correctamente")
    void siniestroRequest_debeCrearseCorrectamente() {
        String numeroPoliza = "POL-456";
        String descripcion = "Rotura de luna delantera";
        BigDecimal montoReclamado = new BigDecimal("2500.75");

        SiniestroRequest request = new SiniestroRequest(numeroPoliza, descripcion, montoReclamado);

        assertThat(request.numeroPoliza()).isEqualTo(numeroPoliza);
        assertThat(request.descripcion()).isEqualTo(descripcion);
        assertThat(request.montoReclamado()).isEqualTo(montoReclamado);
    }
}
