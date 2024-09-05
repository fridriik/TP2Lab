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

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        logger.info("Solicitud recibida para crear empleado");
        EmpleadoDTO empleadoCreado = empleadoService.crearEmpleado(empleadoDTO);
        logger.info("Solicitud finalizada con éxito, empleado creado con Id: {}", empleadoCreado.getId());
        return new ResponseEntity<>(empleadoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerTodosEmpleados() {
        logger.info("Solicitud recibida para obtener todos los empleados");
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodosLosEmpleados();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{empleadoId}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Integer empleadoId) {
        logger.info("Solicitud recibida para obtener un empleado con el Id: {}", empleadoId);
        EmpleadoDTO empleado = empleadoService.obtenerEmpleadoPorId(empleadoId);
        return new ResponseEntity<>(empleado, HttpStatus.OK);
    }

    @PutMapping("/{empleadoId}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable("empleadoId") Integer empleadoId, @RequestBody EmpleadoDTO empleadoDTO) {
        logger.info("Solicitud recibida para actualizar al empleado con el Id: {}", empleadoId);
        EmpleadoDTO empleadoActualizado = empleadoService.actualizarEmpleado(empleadoId, empleadoDTO);
        logger.info("Solicitud finalizada con éxito, empleado actualizado con Id: {}", empleadoId);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @DeleteMapping("/{empleadoId}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer empleadoId) {
        logger.info("Solicitud recibida para eliminar empleado con Id: {}", empleadoId);
        empleadoService.eliminarEmpleado(empleadoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}