package com.challenge.seguros.service.impl;

import com.challenge.seguros.dto.SiniestroRequest;
import com.challenge.seguros.exception.PolizaInvalidaException;
import com.challenge.seguros.service.SiniestroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiniestroServiceImpl implements SiniestroService {

    private static final String POLIZA_API_URI = "/api/v1/polizas/{numeroPoliza}";
    private static final String MSG_SINIESTRO_APROBADO = "Siniestro APROBADO. La póliza %s es válida y cubre el monto de %s";
    private static final String MSG_SINIESTRO_RECHAZADO = "Siniestro RECHAZADO: Póliza no encontrada o inactiva.";
    private static final String LOG_INICIO_PROCESAMIENTO = "Iniciando procesamiento de siniestro para la póliza: {}";
    private static final String LOG_POLIZA_NO_ENCONTRADA = "El poliza-service indicó que la póliza {} no existe o está inactiva.";


    private final WebClient polizaWebClient;

    @Override
    public Mono<String> procesarSiniestro(SiniestroRequest request) {
        log.info(LOG_INICIO_PROCESAMIENTO, request.numeroPoliza());

        return polizaWebClient.get()
                .uri(POLIZA_API_URI, request.numeroPoliza())
                .retrieve()
                .bodyToMono(Object.class)
                .map(respuesta -> String.format(MSG_SINIESTRO_APROBADO, request.numeroPoliza(), request.montoReclamado()))
                .onErrorResume(WebClientResponseException.class, error -> {
                    if (error.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.warn(LOG_POLIZA_NO_ENCONTRADA, request.numeroPoliza());
                        return Mono.error(new PolizaInvalidaException(MSG_SINIESTRO_RECHAZADO));
                    }
                    return Mono.error(error);
                });
    }
}
