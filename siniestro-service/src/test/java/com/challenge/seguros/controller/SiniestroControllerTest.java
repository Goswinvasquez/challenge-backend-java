package com.challenge.seguros.controller;

import com.challenge.seguros.dto.SiniestroRequest;
import com.challenge.seguros.exception.PolizaInvalidaException;
import com.challenge.seguros.service.SiniestroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(SiniestroController.class)
class SiniestroControllerTest {

    private static final String BASE_URL = "/api/v1/siniestros";
    private static final String MSG_SINIESTRO_RECHAZADO = "Siniestro RECHAZADO: Póliza no encontrada o inactiva.";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SiniestroService siniestroService;

    private SiniestroRequest siniestroRequestValido;

    @BeforeEach
    void setUp() {
        siniestroRequestValido = new SiniestroRequest("POL-123", "Colisión leve", BigDecimal.valueOf(1500));
    }

    @Test
    @DisplayName("POST " + BASE_URL + " - Debe devolver 200 OK con mensaje de éxito")
    void reportarSiniestro_cuandoEsValido_debeDevolverOk() {
        String mensajeExito = "Siniestro APROBADO.";
        given(siniestroService.procesarSiniestro(any(SiniestroRequest.class)))
                .willReturn(Mono.just(mensajeExito));

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(siniestroRequestValido))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> assertThat(responseBody).isEqualTo(mensajeExito));
    }

    @Test
    @DisplayName("POST " + BASE_URL + " - Debe devolver 400 Bad Request si la póliza es inválida")
    void reportarSiniestro_cuandoPolizaEsInvalida_debeDevolverBadRequest() {
        given(siniestroService.procesarSiniestro(any(SiniestroRequest.class)))
                .willReturn(Mono.error(new PolizaInvalidaException(MSG_SINIESTRO_RECHAZADO)));

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(siniestroRequestValido))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo(MSG_SINIESTRO_RECHAZADO);
    }
}
