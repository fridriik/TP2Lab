package com.turnosrotativos.service;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.model.JornadaLaboral;
import com.turnosrotativos.repository.JornadaLaboralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ValidadorService {

    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    public void validarJornada(JornadaRequestDTO jornadaRequestDTO, Empleado empleado, ConceptoLaboral concepto, List<JornadaLaboral> jornadasSemanales, List<JornadaLaboral> jornadasMensuales) {
        validarHorasDiariasYSemanales(jornadasSemanales, jornadaRequestDTO.getHorasTrabajadas(), jornadaRequestDTO.getFecha());
        validarHorasMensuales(jornadasMensuales, jornadaRequestDTO.getHorasTrabajadas());
        validarDiasLibres(jornadasSemanales, jornadasMensuales, concepto, jornadaRequestDTO.getFecha());
        validarTurnosExtra(jornadasSemanales, concepto);
        validarTurnosNormales(jornadasSemanales, concepto);
        validarEmpleadosPorConceptoPorDia(jornadaRequestDTO.getFecha(), jornadaRequestDTO.getIdConcepto());
        validarConceptoDuplicadoPorDia(empleado.getId(), jornadaRequestDTO.getFecha(), jornadaRequestDTO.getIdConcepto());
    }

    public void validarHorasTrabajadas(ConceptoLaboral concepto, Integer horasTrabajadas) {
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

    public void validarHorasDiariasYSemanales(List<JornadaLaboral> jornadasSemanales, Integer horasTrabajadas, LocalDate fecha) {
        Integer horasSemanales = calcularHorasSemanales(jornadasSemanales);
        if (horasSemanales + (horasTrabajadas != null ? horasTrabajadas : 0) > 52) {
            throw new BadRequestException("El empleado ingresado supera las 52 horas semanales.");
        }

        Integer horasDiarias = calcularHorasDiarias(jornadasSemanales, fecha);
        if (horasDiarias + (horasTrabajadas != null ? horasTrabajadas : 0) > 14) {
            throw new BadRequestException("Un empleado no puede cargar más de 14 horas trabajadas en un día.");
        }
    }

    public void validarHorasMensuales(List<JornadaLaboral> jornadasMensuales, Integer horasTrabajadas) {
        Integer horasMensuales = calcularHorasMensuales(jornadasMensuales);
        if (horasMensuales + (horasTrabajadas != null ? horasTrabajadas : 0) > 190) {
            throw new BadRequestException("El empleado ingresado supera las 190 horas mensuales.");
        }
    }

    public void validarDiasLibres(List<JornadaLaboral> jornadasSemanales, List<JornadaLaboral> jornadasMensuales, ConceptoLaboral concepto, LocalDate fecha) {
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

    public void validarTurnosExtra(List<JornadaLaboral> jornadasSemanales, ConceptoLaboral concepto) {
        Integer turnosExtra = contarTurnosPorConcepto(jornadasSemanales, "Turno Extra");
        if (concepto.getNombre().equals("Turno Extra") && turnosExtra >= 3) {
            throw new BadRequestException("El empleado ingresado ya cuenta con 3 turnos extra esta semana.");
        }
    }

    public void validarTurnosNormales(List<JornadaLaboral> jornadasSemanales, ConceptoLaboral concepto) {
        Integer turnosNormales = contarTurnosPorConcepto(jornadasSemanales, "Turno Normal");
        if (concepto.getNombre().equals("Turno Normal") && turnosNormales >= 5) {
            throw new BadRequestException("El empleado ingresado ya cuenta con 5 turnos normales esta semana.");
        }
    }

    public void validarEmpleadosPorConceptoPorDia(LocalDate fecha, Integer idConcepto) {
        Integer empleadosPorConcepto = jornadaLaboralRepository.countByFechaAndConceptoLaboralId(fecha, idConcepto);
        if (empleadosPorConcepto >= 2) {
            throw new BadRequestException("Ya existen 2 empleados registrados para este concepto en la fecha ingresada.");
        }
    }

    public void validarConceptoDuplicadoPorDia(Integer empleadoId, LocalDate fecha, Integer conceptoId) {
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
