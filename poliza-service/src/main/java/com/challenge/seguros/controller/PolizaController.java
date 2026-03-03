package com.challenge.seguros.controller;

import com.challenge.seguros.model.Poliza;
import com.challenge.seguros.service.PolizaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;

    /**
     * Endpoint para buscar una póliza única..
     */
    @GetMapping("/{numeroPoliza}")
    public Mono<Poliza> obtenerPoliza(@PathVariable String numeroPoliza) {
        return polizaService.obtenerPolizaActivaPorNumero(numeroPoliza);
    }

    /**
     * Endpoint para listar pólizas de un cliente.
     */
    @GetMapping(value = "/cliente/{dniCliente}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Poliza> obtenerPolizasDeCliente(@PathVariable String dniCliente) {
        return polizaService.obtenerTodasPorDni(dniCliente);
    }
}