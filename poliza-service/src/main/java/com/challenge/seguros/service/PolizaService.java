package com.challenge.seguros.service;

import com.challenge.seguros.model.Poliza;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PolizaService {

    Mono<Poliza> obtenerPolizaActivaPorNumero(String numeroPoliza);

    Flux<Poliza> obtenerTodasPorDni(String dniCliente);
}