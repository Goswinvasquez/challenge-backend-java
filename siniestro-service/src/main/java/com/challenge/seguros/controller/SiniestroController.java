package com.challenge.seguros.controller;

import com.challenge.seguros.dto.SiniestroRequest;
import com.challenge.seguros.service.SiniestroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/siniestros")
@RequiredArgsConstructor
public class SiniestroController {

    private final SiniestroService siniestroService;

    @PostMapping
    public Mono<String> reportarSiniestro(@RequestBody SiniestroRequest request) {
        return siniestroService.procesarSiniestro(request);
    }
}
