package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.ConflictException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO empleadoDTO) {
        logger.info("Iniciando creación de empleado: {}", empleadoDTO);
        validarEmpleado(empleadoDTO);
        Empleado empleado = empleadoDTO.toEntity();
        Empleado empleadoCreado = empleadoRepository.save(empleado);
        logger.info("Empleado creado exitosamente: {}", empleadoCreado);
        return EmpleadoDTO.fromEntity(empleadoCreado);
    }

    private void validarEmpleado(EmpleadoDTO empleadoDTO) {
        logger.debug("Validando empleado: {}", empleadoDTO);
        validarDocumentoUnico(empleadoDTO.getNroDocumento());
        validarEdad(empleadoDTO.getFechaNacimiento());
        validarEmailUnico(empleadoDTO.getEmail());
        validarFechaIngreso(empleadoDTO.getFechaIngreso());
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now().minusYears(18))) {
            logger.warn("Intento de crear empleado menor de 18 años");
            throw new BadRequestException("La edad del empleado no puede ser menor a 18 años.");
        }
    }

    private void validarDocumentoUnico(Integer nroDocumento) {
        if (nroDocumento != null && empleadoRepository.existsByNroDocumento(nroDocumento)) {
            logger.warn("Intento de crear empleado con documento duplicado: {}", nroDocumento);
            throw new ConflictException("Ya existe un empleado con el documento ingresado.");
        }
    }

    private void validarEmailUnico(String email) {
        if (email != null && empleadoRepository.existsByEmail(email)) {
            logger.warn("Intento de crear empleado con email duplicado: {}", email);
            throw new ConflictException("Ya existe un empleado con el email ingresado.");
        }
    }

    private void validarFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso != null && fechaIngreso.isAfter(LocalDate.now())) {
            logger.warn("Intento de crear empleado con fecha posterior al día de la fecha: {}", fechaIngreso);
            throw new BadRequestException("La fecha de ingreso no puede ser posterior al día de la fecha.");
        }
    }
}