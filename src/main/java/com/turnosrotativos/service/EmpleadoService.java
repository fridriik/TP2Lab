package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.exception.NotFoundException;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.ConflictException;
import com.turnosrotativos.repository.JornadaLaboralRepository;
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
        validarEmpleado(empleadoDTO, null);
        Empleado empleado = empleadoDTO.toEntity();
        Empleado empleadoCreado = empleadoRepository.save(empleado);
        logger.info("Empleado creado exitosamente con ID: {}", empleadoCreado.getId());
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
        logger.info("Buscando empleado con ID: {}", id);
        Empleado empleado = empleadoRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> {
                    logger.warn("No se encontró el empleado con ID: {}", id);
                    return new NotFoundException("No se encontró el empleado con Id: " + id);
                });
        logger.info("Obteniendo empleado: {}", empleado);
        return EmpleadoDTO.fromEntity(empleado);
    }

    public EmpleadoDTO actualizarEmpleado(Integer empleadoId, EmpleadoDTO empleadoDTO) {
        logger.info("Iniciando actualización de empleado con ID: {}", empleadoId);
        Empleado empleadoExistente = empleadoRepository.findById(Long.valueOf(empleadoId))
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar empleado inexistente con ID: {}", empleadoId);
                    return new NotFoundException("No se encontró el empleado con Id: " + empleadoId);
                });

        validarEmpleado(empleadoDTO, empleadoExistente.getId());
        empleadoExistente.setNombre(empleadoDTO.getNombre());
        empleadoExistente.setApellido(empleadoDTO.getApellido());
        empleadoExistente.setEmail(empleadoDTO.getEmail());
        empleadoExistente.setNroDocumento(empleadoDTO.getNroDocumento());
        empleadoExistente.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleadoExistente.setFechaIngreso(empleadoDTO.getFechaIngreso());

        empleadoRepository.save(empleadoExistente);
        logger.info("Empleado actualizado exitosamente con ID: {}", empleadoId);
        return EmpleadoDTO.fromEntity(empleadoExistente);
    }

    @Transactional
    public void eliminarEmpleado(Integer empleadoId) {
        logger.info("Iniciando eliminación de empleado con Id: {}", empleadoId);
        if (existenJornadasAsociadas(empleadoId)) {
            logger.warn("Intento de eliminar empleado con jornadas asociadas. Id: {}", empleadoId);
            throw new BadRequestException("No es posible eliminar un empleado con jornadas asociadas.");
        }
        if (!empleadoRepository.existsById(Long.valueOf(empleadoId))) {
            logger.warn("Intento de eliminar empleado inexistente. Id: {}", empleadoId);
            throw new NotFoundException("No se encontró el empleado con Id: {" + empleadoId + "}");
        }
        empleadoRepository.deleteById(Long.valueOf(empleadoId));
        logger.info("Empleado eliminado exitosamente. Id: {}", empleadoId);
    }

    private boolean existenJornadasAsociadas(Integer empleadoId) {
        return jornadaLaboralRepository.countByEmpleadoId(empleadoId) > 0;
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