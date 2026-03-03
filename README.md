# 🚀 Challenge Técnico Backend Java - Arquitectura Reactiva

Este repositorio contiene la solución al reto técnico de Backend, diseñado bajo una arquitectura de **Microservicios Reactivos** utilizando el stack moderno de Spring.

El dominio elegido para esta prueba es un sistema de **Seguros de Personas**, el cual permite demostrar interacciones asíncronas complejas entre servicios, inmutabilidad y programación funcional.

## 🛠️ Stack Tecnológico
* **Java 17** (Uso intensivo de `Records` para inmutabilidad).
* **Spring Boot 3.x**
* **Spring WebFlux** (Programación reactiva y endpoints no bloqueantes).
* **Spring Data R2DBC** (Persistencia reactiva).
* **H2 Database** (Base de datos en memoria, modo R2DBC).
* **WebClient** (Comunicación HTTP asíncrona entre microservicios).
* **JUnit 5, Mockito & StepVerifier** (Pruebas unitarias reactivas).

---

## 🏗️ Arquitectura de la Solución

El proyecto consta de dos microservicios independientes que se comunican de forma asíncrona:

### 1. `poliza-service` (Puerto: 8081)
Gestiona el dominio principal de las pólizas.
* Cuenta con una base de datos reactiva H2 (R2DBC).
* Expone endpoints para consultar pólizas individuales y listar pólizas por cliente usando Server-Sent Events (`TEXT_EVENT_STREAM`).
* Implementa lógica de dominio utilizando interfaces funcionales de Java (`Predicate`, `Consumer`, `Supplier`).

### 2. `siniestro-service` (Puerto: 8082)
Gestiona la recepción de reclamos (siniestros).
* **No posee base de datos propia.** Actúa como un consumidor orquestador.
* Al recibir un siniestro, utiliza **WebClient** para consultar de forma no bloqueante al `poliza-service` y verificar si la póliza existe y está `ACTIVA`.
* Implementa manejo de errores reactivo (`onErrorResume`) para transformar errores HTTP (Ej. 404 Not Found) en excepciones de negocio personalizadas.

---

## ⚙️ Cómo ejecutar el proyecto

### Prerrequisitos
* Java 17 instalado en la máquina.
* Maven instalado (o usar los wrappers incluidos `./mvnw`).

### Paso a paso
1. Levantar primero el servicio de pólizas (contiene la base de datos):
   ```bash
   cd poliza-service
   ./mvnw spring-boot:run