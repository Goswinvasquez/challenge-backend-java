package com.challenge.seguros.service.impl;

import com.challenge.seguros.exception.PolizaNotFoundException;
import com.challenge.seguros.model.EstadoPoliza;
import com.challenge.seguros.model.Poliza;
import com.challenge.seguros.repository.PolizaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PolizaServiceImplTest {

    @Mock
    private PolizaRepository polizaRepository;

    @InjectMocks
    private PolizaServiceImpl polizaService;

    private Poliza polizaActiva;
    private Poliza polizaVencida;

    @BeforeEach
    void setUp() {
        polizaActiva = new Poliza(1L, "POL-1001", "70123456", EstadoPoliza.ACTIVA, new BigDecimal("50000.00"));
        polizaVencida = new Poliza(2L, "POL-1002", "70987654", EstadoPoliza.VENCIDA, new BigDecimal("25000.00"));
    }

    @Test
    @DisplayName("obtenerPolizaActivaPorNumero debe devolver una póliza si existe y está activa")
    void obtenerPolizaActiva_cuandoExisteYEsActiva_debeDevolverMonoConPoliza() {
        given(polizaRepository.findByNumeroPoliza(polizaActiva.numeroPoliza())).willReturn(Mono.just(polizaActiva));

        Mono<Poliza> polizaMono = polizaService.obtenerPolizaActivaPorNumero(polizaActiva.numeroPoliza());

        StepVerifier.create(polizaMono)
                .expectNext(polizaActiva)
                .verifyComplete();
    }

    @Test
    @DisplayName("obtenerPolizaActivaPorNumero debe devolver error si la póliza no se encuentra")
    void obtenerPolizaActiva_cuandoNoExiste_debeDevolverError() {
        given(polizaRepository.findByNumeroPoliza(anyString())).willReturn(Mono.empty());

        Mono<Poliza> polizaMono = polizaService.obtenerPolizaActivaPorNumero("POL-9999");

        StepVerifier.create(polizaMono)
                .expectError(PolizaNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("obtenerPolizaActivaPorNumero debe devolver error si la póliza está vencida")
    void obtenerPolizaActiva_cuandoExistePeroNoEsActiva_debeDevolverError() {
        given(polizaRepository.findByNumeroPoliza(polizaVencida.numeroPoliza())).willReturn(Mono.just(polizaVencida));

        Mono<Poliza> polizaMono = polizaService.obtenerPolizaActivaPorNumero(polizaVencida.numeroPoliza());

        StepVerifier.create(polizaMono)
                .expectError(PolizaNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("obtenerTodasPorDni debe devolver un Flux de pólizas")
    void obtenerTodasPorDni_cuandoExisten_debeDevolverFluxDePolizas() {
        given(polizaRepository.findByDniCliente(polizaActiva.dniCliente())).willReturn(Flux.just(polizaActiva, polizaVencida));

        Flux<Poliza> polizasFlux = polizaService.obtenerTodasPorDni(polizaActiva.dniCliente());

        StepVerifier.create(polizasFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("obtenerTodasPorDni debe devolver un Flux vacío si el repositorio falla")
    void obtenerTodasPorDni_cuandoRepositorioFalla_debeDevolverFluxVacio() {
        given(polizaRepository.findByDniCliente(anyString())).willReturn(Flux.error(new RuntimeException("Error de BD")));

        Flux<Poliza> polizasFlux = polizaService.obtenerTodasPorDni("un-dni-cualquiera");

        StepVerifier.create(polizasFlux)
                .expectNextCount(0)
                .verifyComplete();
    }
}
