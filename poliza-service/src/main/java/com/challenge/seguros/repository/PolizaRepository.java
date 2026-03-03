package com.challenge.seguros.repository;

import com.challenge.seguros.model.Poliza;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface PolizaRepository extends ReactiveCrudRepository<Poliza, Long> {
    Mono<Poliza> findByNumeroPoliza(String numeroPoliza);
    Flux<Poliza> findByDniCliente(String dniCliente);
}