package com.challenge.seguros.controller;

import com.challenge.seguros.exception.PolizaNotFoundException;
import com.challenge.seguros.model.EstadoPoliza;
import com.challenge.seguros.model.Poliza;
import com.challenge.seguros.service.PolizaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(PolizaController.class)
class PolizaControllerTest {

    private static final String BASE_URL = "/api/v1/polizas";
    private static final String POLIZA_BY_NUMERO_URL = BASE_URL + "/{numeroPoliza}";
    private static final String POLIZAS_BY_CLIENTE_URL = BASE_URL + "/cliente/{dniCliente}";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PolizaService polizaService;

    private Poliza polizaActiva;

    @BeforeEach
    void setUp() {
        polizaActiva = new Poliza(1L, "POL-1001", "70123456", EstadoPoliza.ACTIVA, new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("GET " + POLIZA_BY_NUMERO_URL + " - Debe devolver 200 OK con la póliza")
    void obtenerPoliza_cuandoExiste_debeDevolverPoliza() {
        given(polizaService.obtenerPolizaActivaPorNumero(polizaActiva.numeroPoliza()))
                .willReturn(Mono.just(polizaActiva));

        webTestClient.get().uri(POLIZA_BY_NUMERO_URL, polizaActiva.numeroPoliza())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Poliza.class)
                .value(polizaResponse -> {
                    assertThat(polizaResponse.numeroPoliza()).isEqualTo(polizaActiva.numeroPoliza());
                    assertThat(polizaResponse.estado()).isEqualTo(EstadoPoliza.ACTIVA);
                });
    }

    @Test
    @DisplayName("GET " + POLIZA_BY_NUMERO_URL + " - Debe devolver 404 Not Found")
    void obtenerPoliza_cuandoNoExiste_debeDevolver404() {
        String numeroPolizaInexistente = "POL-9999";
        String mensajeError = "Póliza no encontrada o inactiva: " + numeroPolizaInexistente;
        given(polizaService.obtenerPolizaActivaPorNumero(numeroPolizaInexistente))
                .willReturn(Mono.error(new PolizaNotFoundException(mensajeError)));

        webTestClient.get().uri(POLIZA_BY_NUMERO_URL, numeroPolizaInexistente)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo(mensajeError);
    }

    @Test
    @DisplayName("GET " + POLIZAS_BY_CLIENTE_URL + " - Debe devolver 200 OK con un stream de pólizas")
    void obtenerPolizasDeCliente_cuandoExisten_debeDevolverFluxDePolizas() {
        given(polizaService.obtenerTodasPorDni(polizaActiva.dniCliente()))
                .willReturn(Flux.just(polizaActiva));

        webTestClient.get().uri(POLIZAS_BY_CLIENTE_URL, polizaActiva.dniCliente())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(Poliza.class)
                .hasSize(1)
                .value(listaPolizas -> assertThat(listaPolizas.get(0).dniCliente()).isEqualTo(polizaActiva.dniCliente()));
    }
}
