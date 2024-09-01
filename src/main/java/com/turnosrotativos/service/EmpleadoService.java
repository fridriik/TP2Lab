package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.exception.NotFoundException;
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
import java.util.List;
import java.util.stream.Collectors;

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
        validarEmpleado(empleadoDTO, null);
        Empleado empleado = empleadoDTO.toEntity();
        Empleado empleadoCreado = empleadoRepository.save(empleado);
        logger.info("Empleado creado exitosamente: {}", empleadoCreado);
        return EmpleadoDTO.fromEntity(empleadoCreado);
    }

    public List<EmpleadoDTO> obtenerTodosLosEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        logger.info("Obteniendo empleados: {}", empleados);
        return empleados.stream()
                .map(EmpleadoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public EmpleadoDTO obtenerEmpleadoPorId(Integer id) {
        Empleado empleado = empleadoRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NotFoundException("No se encontró el empleado con Id: " + id));
        logger.info("Obteniendo empleado: {}", empleado);
        return EmpleadoDTO.fromEntity(empleado);
    }

    public EmpleadoDTO actualizarEmpleado(Long empleadoId, EmpleadoDTO empleadoDTO) {
        logger.info("Iniciando actualización de empleado: {}", empleadoDTO);
        Empleado empleadoExistente = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new NotFoundException("No se encontró el empleado con Id: " + empleadoId));

        validarEmpleado(empleadoDTO, empleadoExistente.getId());

        empleadoExistente.setNombre(empleadoDTO.getNombre());
        empleadoExistente.setApellido(empleadoDTO.getApellido());
        empleadoExistente.setEmail(empleadoDTO.getEmail());
        empleadoExistente.setNroDocumento(empleadoDTO.getNroDocumento());
        empleadoExistente.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleadoExistente.setFechaIngreso(empleadoDTO.getFechaIngreso());

        empleadoRepository.save(empleadoExistente);
        logger.info("Actualización de empleado exitosa: {}", empleadoExistente);
        return EmpleadoDTO.fromEntity(empleadoExistente);
    }

    private void validarEmpleado(EmpleadoDTO empleadoDTO, Integer empleadoDTOId) {
        logger.debug("Validando empleado: {}", empleadoDTO);
        validarDocumentoUnico(empleadoDTO, empleadoDTOId);
        validarEdad(empleadoDTO.getFechaNacimiento());
        validarEmailUnico(empleadoDTO, empleadoDTOId);
        validarFechaIngreso(empleadoDTO.getFechaIngreso());
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now().minusYears(18))) {
            logger.warn("Intento de crear empleado menor de 18 años");
            throw new BadRequestException("La edad del empleado no puede ser menor a 18 años.");
        }
    }

    private void validarDocumentoUnico(EmpleadoDTO empleadoDTO, Integer empleadoDTOId) {
        boolean documentoExiste = empleadoRepository.existsByNroDocumento(empleadoDTO.getNroDocumento());
        if (documentoExiste) {
            if (empleadoDTOId == null ||
                    !empleadoRepository.findById(Long.valueOf(empleadoDTOId))
                            .orElseThrow(() -> new NotFoundException("No se encontró el empleado con Id: " + empleadoDTOId))
                            .getNroDocumento()
                            .equals(empleadoDTO.getNroDocumento())) {
                logger.warn("Intento de crear un empleado con el documento existente: {}", empleadoDTO.getNroDocumento());
                throw new ConflictException("Ya existe un empleado con el documento ingresado.");
            }
        }
    }

    private void validarEmailUnico(EmpleadoDTO empleadoDTO, Integer empleadoDTOId) {
        boolean emailExiste = empleadoRepository.existsByEmail(empleadoDTO.getEmail());
        if (emailExiste) {
            if (empleadoDTOId == null ||
                    !empleadoRepository.findById(Long.valueOf(empleadoDTOId))
                            .orElseThrow(() -> new NotFoundException("No se encontró el empleado con Id: " + empleadoDTOId))
                            .getEmail()
                            .equals(empleadoDTO.getEmail())) {
                logger.warn("Intento de crear un empleado con el email existente: {}", empleadoDTO.getEmail());
                throw new ConflictException("Ya existe un empleado con el email ingresado.");
            }
        }
    }

    private void validarFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso != null && fechaIngreso.isAfter(LocalDate.now())) {
            logger.warn("Intento de crear empleado con fecha posterior al día de la fecha: {}", fechaIngreso);
            throw new BadRequestException("La fecha de ingreso no puede ser posterior al día de la fecha.");
        }
    }
}