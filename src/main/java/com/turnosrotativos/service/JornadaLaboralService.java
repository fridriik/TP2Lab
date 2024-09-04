package com.turnosrotativos.service;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.NotFoundException;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.model.JornadaLaboral;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.repository.JornadaLaboralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JornadaLaboralService {

    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ConceptoLaboralRepository conceptoLaboralRepository;

    @Transactional
    public JornadaResponseDTO crearJornada(JornadaRequestDTO jornadaRequestDTO) {
        Empleado empleado = empleadoRepository.findById(Long.valueOf(jornadaRequestDTO.getIdEmpleado()))
                .orElseThrow(() -> new NotFoundException("No existe el empleado ingresado."));

        ConceptoLaboral concepto = conceptoLaboralRepository.findById(jornadaRequestDTO.getIdConcepto())
                .orElseThrow(() -> new NotFoundException("No existe el concepto ingresado."));

        validarHorasTrabajadas(concepto, jornadaRequestDTO.getHorasTrabajadas());

        List<JornadaLaboral> jornadasSemanales = obtenerJornadasSemanales(empleado.getId(), jornadaRequestDTO.getFecha());
        List<JornadaLaboral> jornadasMensuales = obtenerJornadasMensuales(empleado.getId(), jornadaRequestDTO.getFecha());

        validarHorasDiariasYSemanales(jornadasSemanales, jornadaRequestDTO.getHorasTrabajadas(), jornadaRequestDTO.getFecha());
        validarHorasMensuales(jornadasMensuales, jornadaRequestDTO.getHorasTrabajadas());
        validarDiasLibres(jornadasSemanales, jornadasMensuales, concepto, jornadaRequestDTO.getFecha());
        validarTurnosExtra(jornadasSemanales, concepto);
        validarTurnosNormales(jornadasSemanales, concepto);
        validarEmpleadosPorConceptoPorDia(jornadaRequestDTO.getFecha(), jornadaRequestDTO.getIdConcepto());
        validarConceptoDuplicadoPorDia(empleado.getId(), jornadaRequestDTO.getFecha(), jornadaRequestDTO.getIdConcepto());

        JornadaLaboral jornadaLaboral = jornadaRequestDTO.toEntity(empleado, concepto);
        jornadaLaboral = jornadaLaboralRepository.save(jornadaLaboral);

        return JornadaResponseDTO.fromEntity(jornadaLaboral);
    }

    public List<JornadaResponseDTO> obtenerJornadas(LocalDate fechaDesde, LocalDate fechaHasta, Integer nroDocumento) {
        validarRangoFechas(fechaDesde, fechaHasta);
        List<JornadaLaboral> jornadas;
        if (nroDocumento != null) {
            if (!empleadoRepository.existsByNroDocumento(nroDocumento)) {
                throw new NotFoundException("No existe un empleado con el número de documento ingresado.");
            }
            if (fechaDesde != null && fechaHasta != null) {
                jornadas = jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaBetween(nroDocumento, fechaDesde, fechaHasta);
            } else if (fechaDesde != null) {
                jornadas = jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaGreaterThanEqual(nroDocumento, fechaDesde);
            } else if (fechaHasta != null) {
                jornadas = jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaLessThanEqual(nroDocumento, fechaHasta);
            } else {
                jornadas = jornadaLaboralRepository.findByEmpleadoNroDocumento(nroDocumento);
            }
        } else if (fechaDesde != null || fechaHasta != null) {
            if (fechaDesde != null && fechaHasta != null) {
                jornadas = jornadaLaboralRepository.findByFechaBetween(fechaDesde, fechaHasta);
            } else if (fechaDesde != null) {
                jornadas = jornadaLaboralRepository.findByFechaGreaterThanEqual(fechaDesde);
            } else {
                jornadas = jornadaLaboralRepository.findByFechaLessThanEqual(fechaHasta);
            }
        } else {
            jornadas = jornadaLaboralRepository.findAll();
        }

        return jornadas.stream()
                .map(JornadaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validarHorasTrabajadas(ConceptoLaboral concepto, Integer horasTrabajadas) {
        //ID 3 corresponde a "Día Libre"
        if (concepto.getId() != 3 && horasTrabajadas == null) {
            throw new BadRequestException("'hsTrabajadas' es obligatorio para el concepto ingresado.");
        }
        if (concepto.getId() == 3 && horasTrabajadas != null) {
            throw new BadRequestException("El concepto ingresado no requiere el ingreso de 'hsTrabajadas'");
        }
        if (concepto.getHsMinimo() != null && concepto.getHsMaximo() != null &&
                (horasTrabajadas < concepto.getHsMinimo() || horasTrabajadas > concepto.getHsMaximo())) {
            throw new BadRequestException(String.format("El rango de horas que se puede cargar para este concepto es de %d - %d",
                    concepto.getHsMinimo(), concepto.getHsMaximo()));
        }
    }

    private void validarHorasDiariasYSemanales(List<JornadaLaboral> jornadasSemanales, Integer horasTrabajadas, LocalDate fecha) {
        Integer horasSemanales = calcularHorasSemanales(jornadasSemanales);
        if (horasSemanales + (horasTrabajadas != null ? horasTrabajadas : 0) > 52) {
            throw new BadRequestException("El empleado ingresado supera las 52 horas semanales.");
        }

        Integer horasDiarias = calcularHorasDiarias(jornadasSemanales, fecha);
        if (horasDiarias + (horasTrabajadas != null ? horasTrabajadas : 0) > 14) {
            throw new BadRequestException("Un empleado no puede cargar más de 14 horas trabajadas en un día.");
        }
    }

    private void validarHorasMensuales(List<JornadaLaboral> jornadasMensuales, Integer horasTrabajadas) {
        Integer horasMensuales = calcularHorasMensuales(jornadasMensuales);
        if (horasMensuales + (horasTrabajadas != null ? horasTrabajadas : 0) > 190) {
            throw new BadRequestException("El empleado ingresado supera las 190 horas mensuales.");
        }
    }

    private void validarDiasLibres(List<JornadaLaboral> jornadasSemanales, List<JornadaLaboral> jornadasMensuales, ConceptoLaboral concepto, LocalDate fecha) {
        Integer diasLibresSemana = contarDiasLibres(jornadasSemanales);
        Integer diasLibresMes = contarDiasLibres(jornadasMensuales);

        if (concepto.getNombre().equals("Día Libre")) {
            if (diasLibresSemana >= 2) {
                throw new BadRequestException("El empleado no cuenta con más días libres esta semana.");
            }
            if (diasLibresMes >= 5) {
                throw new BadRequestException("El empleado no cuenta con más días libres este mes.");
            }
        }
    }

    private void validarTurnosExtra(List<JornadaLaboral> jornadasSemanales, ConceptoLaboral concepto) {
        Integer turnosExtra = contarTurnosPorConcepto(jornadasSemanales, "Turno Extra");
        if (concepto.getNombre().equals("Turno Extra") && turnosExtra >= 3) {
            throw new BadRequestException("El empleado ingresado ya cuenta con 3 turnos extra esta semana.");
        }
    }

    private void validarTurnosNormales(List<JornadaLaboral> jornadasSemanales, ConceptoLaboral concepto) {
        Integer turnosNormales = contarTurnosPorConcepto(jornadasSemanales, "Turno Normal");
        if (concepto.getNombre().equals("Turno Normal") && turnosNormales >= 5) {
            throw new BadRequestException("El empleado ingresado ya cuenta con 5 turnos normales esta semana.");
        }
    }

    private void validarEmpleadosPorConceptoPorDia(LocalDate fecha, Integer idConcepto) {
        Integer empleadosPorConcepto = jornadaLaboralRepository.countByFechaAndConceptoLaboralId(fecha, idConcepto);
        if (empleadosPorConcepto >= 2) {
            throw new BadRequestException("Ya existen 2 empleados registrados para este concepto en la fecha ingresada.");
        }
    }

    private void validarConceptoDuplicadoPorDia(Integer empleadoId, LocalDate fecha, Integer conceptoId) {
        boolean existeJornada = jornadaLaboralRepository.existsByEmpleadoIdAndFechaAndConceptoLaboralId(empleadoId, fecha, conceptoId);
        if (existeJornada) {
            throw new BadRequestException("El empleado ya tiene registrado una jornada con este concepto en la fecha ingresada.");
        }
    }

    public void validarRangoFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new BadRequestException("El campo ‘fechaDesde’ no puede ser mayor que ‘fechaHasta’.");
        }
    }

    private List<JornadaLaboral> obtenerJornadasSemanales(Integer empleadoId, LocalDate fecha) {
        LocalDate startOfWeek = fecha.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return jornadaLaboralRepository.findByEmpleadoIdAndFechaBetween(empleadoId, startOfWeek, endOfWeek);
    }

    private List<JornadaLaboral> obtenerJornadasMensuales(Integer empleadoId, LocalDate fecha) {
        LocalDate startOfMonth = fecha.withDayOfMonth(1);
        LocalDate endOfMonth = fecha.withDayOfMonth(fecha.lengthOfMonth());
        return jornadaLaboralRepository.findByEmpleadoIdAndFechaBetween(empleadoId, startOfMonth, endOfMonth);
    }

    private int calcularHorasSemanales(List<JornadaLaboral> jornadas) {
        return jornadas.stream()
                .mapToInt(j -> j.getHorasTrabajadas() != null ? j.getHorasTrabajadas() : 0)
                .sum();
    }

    private int calcularHorasDiarias(List<JornadaLaboral> jornadas, LocalDate fecha) {
        return jornadas.stream()
                .filter(j -> j.getFecha().equals(fecha))
                .mapToInt(j -> j.getHorasTrabajadas() != null ? j.getHorasTrabajadas() : 0)
                .sum();
    }

    private int calcularHorasMensuales(List<JornadaLaboral> jornadas) {
        return jornadas.stream()
                .mapToInt(j -> j.getHorasTrabajadas() != null ? j.getHorasTrabajadas() : 0)
                .sum();
    }

    private int contarDiasLibres(List<JornadaLaboral> jornadas) {
        return (int) jornadas.stream()
                .filter(j -> j.getConceptoLaboral().getNombre().equals("Día Libre"))
                .count();
    }

    private int contarTurnosPorConcepto(List<JornadaLaboral> jornadas, String nombreConcepto) {
        return (int) jornadas.stream()
                .filter(j -> j.getConceptoLaboral().getNombre().equals(nombreConcepto))
                .count();
    }
}