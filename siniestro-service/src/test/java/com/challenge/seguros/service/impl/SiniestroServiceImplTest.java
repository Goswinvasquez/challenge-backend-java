package com.challenge.seguros.service.impl;

import com.challenge.seguros.dto.SiniestroRequest;
import com.challenge.seguros.exception.PolizaInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiniestroServiceImplTest {

    private static final String NUMERO_POLIZA_VALIDA = "POL-VALIDA";
    private static final String DESCRIPCION_SINIESTRO = "Daño por agua";
    private static final BigDecimal MONTO_RECLAMADO = new BigDecimal("1000");
    private static final String MSG_SINIESTRO_APROBADO_FORMAT = "Siniestro APROBADO. La póliza %s es válida y cubre el monto de %s";

    @Mock
    private WebClient polizaWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SiniestroServiceImpl siniestroService;

    private SiniestroRequest siniestroRequest;

    @BeforeEach
    void setUp() {
        siniestroRequest = new SiniestroRequest(NUMERO_POLIZA_VALIDA, DESCRIPCION_SINIESTRO, MONTO_RECLAMADO);

        when(polizaWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("procesarSiniestro debe devolver APROBADO si la póliza es válida (200 OK)")
    void procesarSiniestro_cuandoPolizaEsValida_debeDevolverAprobado() {
        String expectedMessage = String.format(MSG_SINIESTRO_APROBADO_FORMAT, NUMERO_POLIZA_VALIDA, MONTO_RECLAMADO);
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));

        Mono<String> resultado = siniestroService.procesarSiniestro(siniestroRequest);

        StepVerifier.create(resultado)
                .expectNext(expectedMessage)
                .verifyComplete();
    }

    @Test
    @DisplayName("procesarSiniestro debe devolver PolizaInvalidaException si la póliza no se encuentra (404)")
    void procesarSiniestro_cuandoPolizaNoExiste_debeDevolverError() {
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.error(createWebClientException(HttpStatus.NOT_FOUND)));

        Mono<String> resultado = siniestroService.procesarSiniestro(siniestroRequest);

        StepVerifier.create(resultado)
                .expectError(PolizaInvalidaException.class)
                .verify();
    }

    @Test
    @DisplayName("procesarSiniestro debe propagar WebClientResponseException si el servicio externo falla (500)")
    void procesarSiniestro_cuandoServicioExternoFalla_debePropagarError() {
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.error(createWebClientException(HttpStatus.INTERNAL_SERVER_ERROR)));

        Mono<String> resultado = siniestroService.procesarSiniestro(siniestroRequest);

        StepVerifier.create(resultado)
                .expectError(WebClientResponseException.class)
                .verify();
    }

    private static WebClientResponseException createWebClientException(HttpStatusCode status) {
        return WebClientResponseException.create(status.value(), status.toString(), new HttpHeaders(), new byte[0], null);
    }
}
