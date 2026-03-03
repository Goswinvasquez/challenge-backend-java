package com.challenge.seguros.service;

import com.challenge.seguros.dto.SiniestroRequest;
import reactor.core.publisher.Mono;

public interface SiniestroService {
    Mono<String> procesarSiniestro(SiniestroRequest request);
}
