package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.exception.NotFoundException;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.repository.JornadaLaboralRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;
    private final JornadaLaboralRepository jornadaLaboralRepository;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository,
                           JornadaLaboralRepository jornadaLaboralRepository) {
        this.empleadoRepository = empleadoRepository;
        this.jornadaLaboralRepository = jornadaLaboralRepository;
    }

    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO empleadoDTO) {
        logger.info("Iniciando creación de empleado");
        Empleado empleado = empleadoDTO.toEntity();
        Empleado empleadoCreado = empleadoRepository.save(empleado);
        logger.info("Empleado creado exitosamente con Id: {}", empleadoCreado.getId());
        return EmpleadoDTO.fromEntity(empleadoCreado);
    }


    public List<EmpleadoDTO> obtenerTodosLosEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        logger.info("Obteniendo todos los empleados");
        return empleados.stream()
                .map(EmpleadoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public EmpleadoDTO obtenerEmpleadoPorId(Integer id) {
        logger.info("Buscando empleado con Id: {}", id);
        Empleado empleado = empleadoRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> {
                    logger.warn("No se encontró el empleado con Id: {}", id);
                    return new NotFoundException("No se encontró el empleado con Id: " + id);
                });
        logger.info("Obteniendo empleado: {}", empleado);
        return EmpleadoDTO.fromEntity(empleado);
    }

    @Transactional
    public EmpleadoDTO actualizarEmpleado(Integer empleadoId, EmpleadoDTO empleadoDTO) {
        logger.info("Iniciando actualización de empleado con Id: {}", empleadoId);
        Empleado empleadoExistente = empleadoRepository.findById(Long.valueOf(empleadoId))
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar empleado inexistente con Id: {}", empleadoId);
                    return new NotFoundException("No se encontró el empleado con Id: " + empleadoId);
                });
        //Actualizamos al empleado seleccionado
        empleadoExistente.setNombre(empleadoDTO.getNombre());
        empleadoExistente.setApellido(empleadoDTO.getApellido());
        empleadoExistente.setEmail(empleadoDTO.getEmail());
        empleadoExistente.setNroDocumento(empleadoDTO.getNroDocumento());
        empleadoExistente.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleadoExistente.setFechaIngreso(empleadoDTO.getFechaIngreso());
        empleadoRepository.save(empleadoExistente);
        logger.info("Empleado actualizado exitosamente con Id: {}", empleadoId);
        return EmpleadoDTO.fromEntity(empleadoExistente);
    }

    @Transactional
    public void eliminarEmpleado(Integer empleadoId) {
        logger.info("Iniciando eliminación de empleado con Id: {}", empleadoId);
        Empleado empleado = empleadoRepository.findById(Long.valueOf(empleadoId))
                .orElseThrow(() -> {
                    logger.warn("Intento de eliminar empleado inexistente. Id: {}", empleadoId);
                    return new NotFoundException("No se encontró el empleado con Id: " + empleadoId);
                });
        // Verifica si el empleado tiene jornadas laborales asociadas antes de eliminar
        if (jornadaLaboralRepository.countByEmpleadoId(empleadoId) > 0) {
            logger.warn("Intento de eliminar empleado con jornadas asociadas. Id: {}", empleadoId);
            throw new BadRequestException("No es posible eliminar un empleado con jornadas asociadas.");
        }
        empleadoRepository.delete(empleado);
        logger.info("Empleado eliminado exitosamente. Id: {}", empleadoId);
    }
}