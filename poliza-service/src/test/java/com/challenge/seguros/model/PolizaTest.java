package com.challenge.seguros.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PolizaTest {

    @Test
    @DisplayName("isActiva debe devolver true cuando el estado es ACTIVA")
    void isActiva_cuandoEstadoEsActiva_debeDevolverTrue() {
        Poliza poliza = new Poliza(1L, "P-001", "12345", EstadoPoliza.ACTIVA, BigDecimal.TEN);
        assertThat(poliza.isActiva()).isTrue();
    }

    @Test
    @DisplayName("isActiva debe devolver false cuando el estado es VENCIDA")
    void isActiva_cuandoEstadoEsVencida_debeDevolverFalse() {
        Poliza poliza = new Poliza(1L, "P-001", "12345", EstadoPoliza.VENCIDA, BigDecimal.TEN);
        assertThat(poliza.isActiva()).isFalse();
    }

    @Test
    @DisplayName("isActiva debe devolver false cuando el estado es CANCELADA")
    void isActiva_cuandoEstadoEsCancelada_debeDevolverFalse() {
        Poliza poliza = new Poliza(1L, "P-001", "12345", EstadoPoliza.CANCELADA, BigDecimal.TEN);
        assertThat(poliza.isActiva()).isFalse();
    }
}
