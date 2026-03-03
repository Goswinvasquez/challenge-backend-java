package com.challenge.seguros.repository;

import com.challenge.seguros.model.EstadoPoliza;
import com.challenge.seguros.model.Poliza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class PolizaRepositoryTest {

    private static final String DNI_CLIENTE_EXISTENTE = "DNI-CLIENTE-1";
    private static final String NUMERO_POLIZA_ACTIVA = "POL-2001";
    private static final String NUMERO_POLIZA_VENCIDA = "POL-2002";
    private static final String SQL_CLEANUP = "DELETE FROM POLIZAS";

    @Autowired
    private PolizaRepository polizaRepository;

    @Autowired
    private DatabaseClient databaseClient;

    private Poliza polizaActiva;
    private Poliza polizaVencida;

    @BeforeEach
    void setUp() {
        polizaActiva = new Poliza(null, NUMERO_POLIZA_ACTIVA, DNI_CLIENTE_EXISTENTE, EstadoPoliza.ACTIVA, new BigDecimal("10000.00"));
        polizaVencida = new Poliza(null, NUMERO_POLIZA_VENCIDA, DNI_CLIENTE_EXISTENTE, EstadoPoliza.VENCIDA, new BigDecimal("20000.00"));

        databaseClient.sql(SQL_CLEANUP).then()
                .then(polizaRepository.saveAll(List.of(polizaActiva, polizaVencida)).then())
                .block();
    }

    @Test
    @DisplayName("findByNumeroPoliza debe encontrar la póliza correcta")
    void findByNumeroPoliza_cuandoExiste_debeDevolverPoliza() {
        Mono<Poliza> polizaMono = polizaRepository.findByNumeroPoliza(NUMERO_POLIZA_ACTIVA);

        StepVerifier.create(polizaMono)
                .assertNext(poliza -> {
                    assertThat(poliza.numeroPoliza()).isEqualTo(NUMERO_POLIZA_ACTIVA);
                    assertThat(poliza.estado()).isEqualTo(EstadoPoliza.ACTIVA);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("findByNumeroPoliza debe devolver Mono.empty si no encuentra la póliza")
    void findByNumeroPoliza_cuandoNoExiste_debeDevolverVacio() {
        Mono<Poliza> polizaMono = polizaRepository.findByNumeroPoliza("NUMERO-INEXISTENTE");

        StepVerifier.create(polizaMono)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByDniCliente debe encontrar todas las pólizas de un cliente")
    void findByDniCliente_cuandoExisten_debeDevolverTodasLasPolizas() {
        Flux<Poliza> polizasFlux = polizaRepository.findByDniCliente(DNI_CLIENTE_EXISTENTE);

        StepVerifier.create(polizasFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByDniCliente debe devolver Flux.empty si el cliente no tiene pólizas")
    void findByDniCliente_cuandoNoExisten_debeDevolverVacio() {
        Flux<Poliza> polizasFlux = polizaRepository.findByDniCliente("DNI-INEXISTENTE");

        StepVerifier.create(polizasFlux)
                .verifyComplete();
    }
}
