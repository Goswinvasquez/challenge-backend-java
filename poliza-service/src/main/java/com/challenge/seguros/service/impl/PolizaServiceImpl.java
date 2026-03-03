package com.challenge.seguros.service.impl;

import com.challenge.seguros.exception.PolizaNotFoundException;
import com.challenge.seguros.model.Poliza;
import com.challenge.seguros.repository.PolizaRepository;
import com.challenge.seguros.service.PolizaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolizaServiceImpl implements PolizaService {

    private static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada o inactiva: ";
    private static final String LOG_OPERACION_EXITOSA = "Operación exitosa para póliza: {}";
    private static final String LOG_ERROR_BUSQUEDA = "Error al buscar pólizas del cliente {}: {}";

    private final PolizaRepository polizaRepository;

    private final Predicate<Poliza> isPolizaActiva = Poliza::isActiva;

    private final Consumer<Poliza> logPolizaEncontrada =
            poliza -> log.info(LOG_OPERACION_EXITOSA, poliza.numeroPoliza());

    /**
     * Busca una póliza por su número y valida que esté activa.
     */
    @Override
    public Mono<Poliza> obtenerPolizaActivaPorNumero(String numeroPoliza) {

        Supplier<Mono<Poliza>> errorSupplier =
                () -> Mono.error(new PolizaNotFoundException(MSG_POLIZA_NO_ENCONTRADA + numeroPoliza));

        return polizaRepository.findByNumeroPoliza(numeroPoliza)
                .filter(isPolizaActiva)
                .switchIfEmpty(errorSupplier.get())
                .doOnNext(logPolizaEncontrada);
    }

    /**
     * Busca todas las pólizas de un cliente usando Streams reactivos.
     */
    @Override
    public Flux<Poliza> obtenerTodasPorDni(String dniCliente) {
        return polizaRepository.findByDniCliente(dniCliente)
                .map(poliza -> new Poliza(
                        poliza.id(),
                        poliza.numeroPoliza(),
                        poliza.dniCliente().toUpperCase(),
                        poliza.estado(),
                        poliza.montoCobertura())
                )
                .onErrorResume(throwable -> {
                    log.error(LOG_ERROR_BUSQUEDA, dniCliente, throwable.getMessage());
                    return Flux.empty();
                });
    }
}
