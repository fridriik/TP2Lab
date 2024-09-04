package com.turnosrotativos.controller;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    private final EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        logger.info("Recibida solicitud para crear empleado: {}", empleadoDTO);
        EmpleadoDTO empleadoCreado = empleadoService.crearEmpleado(empleadoDTO);
        logger.info("Empleado creado exitosamente: {}", empleadoCreado);
        return new ResponseEntity<>(empleadoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerTodosEmpleados() {
        logger.info("Recibida solicitud para obtener todos los empleados");
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodosLosEmpleados();
        logger.info("Empleados obtenidos: {}", empleados);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{empleadoId}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Integer empleadoId) {
        logger.info("Recibida solicitud para obtener un empleado con el Id: {}", empleadoId);
        EmpleadoDTO empleado = empleadoService.obtenerEmpleadoPorId(empleadoId);
        logger.info("Empleado obtenido: {}", empleado);
        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }

    @PutMapping("/{empleadoId}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable("empleadoId") Integer empleadoId, @RequestBody EmpleadoDTO empleadoDTO) {
        logger.info("Recibida solicitud para actualizar al empleado: {}", empleadoDTO);
        EmpleadoDTO empleadoActualizado = empleadoService.actualizarEmpleado(empleadoId, empleadoDTO);
        logger.info("Empleado actualizado: {}", empleadoActualizado);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @DeleteMapping("/{empleadoId}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer empleadoId) {
        empleadoService.eliminarEmpleado(empleadoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}