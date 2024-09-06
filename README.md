# TP2Lab - API de gestión de empleados y jornadas laborales

## Descripción
Esta API, desarrollada en Spring, permite la gestión de empleados y sus jornadas laborales.  
Ofrece funcionalidades para crear, buscar, actualizar y eliminar empleados, 
así como asignarles diferentes tipos de jornadas laborales,  
buscar estas jornadas y obtener los conceptos de cada una.

## Funcionalidades principales
- CRUD de empleados
- Asignación de jornadas laborales a empleados
- Tres tipos de conceptos laborales: Turno Normal, Turno Extra y Día Libre
- Validaciones personalizadas para cada operación

## Arquitectura y tecnologías
- Spring
- Arquitectura por capas
- JPA para persistencia de datos
- DTOs para transferencia de datos
- Manejo de excepciones personalizadas y globales
- Testing con JUnit y Mockito

## Decisiones de diseño

1. **Manejo de validaciones**:
    - Validaciones básicas en DTOs:
        - Detección temprana de errores
        - Mejora de legibilidad
        - Reducción de carga en el servidor
    - Validaciones complejas en `ValidadorService`:
        - Lógica que requiere acceso a la base de datos
        - Reglas de negocio específicas
        - Validaciones reutilizables en diferentes contextos

2. **Excepciones**:
    - Uso de `GlobalExceptionHandler` para manejo centralizado de errores
    - Proporciona respuestas de error consistentes
    - Reduce código repetitivo en controladores

3. **Modularización**:
    - DTOs como capa de abstracción entre cliente y modelos internos
    - Separación de DTOs para solicitudes y respuestas en `JornadaLaboral`
    - Servicios especializados como `ValidadorService`

4. **Testeo**:
    - Pruebas unitarias con Mockito
    - Cobertura de escenarios principales y casos de excepción

## Cómo usar la API

1. **Empleado**:
    - POST `/empleado`: Crear un nuevo empleado
    - GET `/empleado/{id}`: Obtener detalles de un empleado
    - PUT `/empleado/{id}`: Actualizar información de un empleado
    - DELETE `empleado/{id}`: Eliminar un empleado

2. **Concepto Laboral**:
    - GET `/concepto-laboral`: Obtener los conceptos laborales

3. **Jornadas Laborales**:
    - POST `/jornada`: Asignar una jornada laboral a un empleado
    - GET `/jornada`: Obtener las jornadas

4. **Manejo de errores**:
    - Los errores se manejan de forma consistente, devolviendo mensajes de estado HTTP en base a cada status

## Configuración y ejecución

### Usando IntelliJ
1. Clonar el repositorio.
2. Abrir el proyecto en IntelliJ.
3. Localizar `TurnosRotativosApplication` y correr.

### Usando Maven Wrapper (recomendado para cualquier sistema)
1. Clonar el repositorio.
2. Navegar hasta el directorio del proyecto en la terminal.
3. Ejecutar el siguiente comando:
   ```
   ./mvnw spring-boot:run
   ```
   En Windows, si el comando anterior no funciona:
   ```
   mvnw.cmd spring-boot:run
   ```

### Usando Maven (si está instalado en tu sistema)
1. Navegar hasta el directorio del proyecto en la terminal.
2. Ejecutar el siguiente comando:
   ```
   mvn spring-boot:run
   ```

### Usando el JAR ejecutable
1. Generar el JAR con el siguiente comando:
   ```
   ./mvnw clean package
   ```
2. Ejecutar el JAR:
   ```
   java -jar target/turnos-rotativos-0.0.1-SNAPSHOT.jar
   ```

## Pruebas

### Usando IntelliJ IDEA
1. En el proyecto, localizar la carpeta `src/test/java`.
2. Hacer clic derecho sobre la carpeta y seleccionar Run 'Tests in 'java''.

### Usando Maven Wrapper
1. Navegar hasta el directorio del proyecto en la terminal.
2. Ejecutar el siguiente comando:
   ```
   ./mvnw test
   ```