package com.turnosrotativos.service;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
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

    @Autowired
    private ValidadorService validadorService;

    @Transactional
    public JornadaResponseDTO crearJornada(JornadaRequestDTO jornadaRequestDTO) {
        Empleado empleado = empleadoRepository.findById(Long.valueOf(jornadaRequestDTO.getIdEmpleado()))
                .orElseThrow(() -> new NotFoundException("No existe el empleado ingresado."));

        ConceptoLaboral concepto = conceptoLaboralRepository.findById(jornadaRequestDTO.getIdConcepto())
                .orElseThrow(() -> new NotFoundException("No existe el concepto ingresado."));

        validadorService.validarHorasTrabajadas(concepto, jornadaRequestDTO.getHorasTrabajadas());

        List<JornadaLaboral> jornadasSemanales = obtenerJornadasSemanales(empleado.getId(), jornadaRequestDTO.getFecha());
        List<JornadaLaboral> jornadasMensuales = obtenerJornadasMensuales(empleado.getId(), jornadaRequestDTO.getFecha());

        validadorService.validarJornada(jornadaRequestDTO, empleado, concepto, jornadasSemanales, jornadasMensuales);

        JornadaLaboral jornadaLaboral = jornadaRequestDTO.toEntity(empleado, concepto);
        jornadaLaboral = jornadaLaboralRepository.save(jornadaLaboral);

        return JornadaResponseDTO.fromEntity(jornadaLaboral);
    }

    public List<JornadaResponseDTO> obtenerJornadas(LocalDate fechaDesde, LocalDate fechaHasta, Integer nroDocumento) {
        validadorService.validarRangoFechas(fechaDesde, fechaHasta);
        if (nroDocumento != null && !empleadoRepository.existsByNroDocumento(nroDocumento)) {
            throw new NotFoundException("No existe un empleado con el número de documento ingresado.");
        }
        List<JornadaLaboral> jornadas;
        if (nroDocumento != null) {
            jornadas = obtenerJornadasPorEmpleado(nroDocumento, fechaDesde, fechaHasta);
        } else {
            jornadas = obtenerJornadasPorFecha(fechaDesde, fechaHasta);
        }
        return jornadas.stream()
                .map(JornadaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private List<JornadaLaboral> obtenerJornadasPorEmpleado(Integer nroDocumento, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null) {
            return jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaBetween(nroDocumento, fechaDesde, fechaHasta);
        } else if (fechaDesde != null) {
            return jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaGreaterThanEqual(nroDocumento, fechaDesde);
        } else if (fechaHasta != null) {
            return jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaLessThanEqual(nroDocumento, fechaHasta);
        } else {
            return jornadaLaboralRepository.findByEmpleadoNroDocumento(nroDocumento);
        }
    }

    private List<JornadaLaboral> obtenerJornadasPorFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null) {
            return jornadaLaboralRepository.findByFechaBetween(fechaDesde, fechaHasta);
        } else if (fechaDesde != null) {
            return jornadaLaboralRepository.findByFechaGreaterThanEqual(fechaDesde);
        } else if (fechaHasta != null) {
            return jornadaLaboralRepository.findByFechaLessThanEqual(fechaHasta);
        } else {
            return jornadaLaboralRepository.findAll();
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
}