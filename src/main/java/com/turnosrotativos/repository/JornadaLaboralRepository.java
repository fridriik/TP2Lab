package com.turnosrotativos.repository;

import com.turnosrotativos.model.JornadaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JornadaLaboralRepository extends JpaRepository<JornadaLaboral, Integer> {
    List<JornadaLaboral> findByEmpleadoIdAndFechaBetween(Integer empleadoId, LocalDate startDate, LocalDate endDate);
    boolean existsByEmpleadoIdAndFechaAndConceptoLaboralId(Integer empleadoId, LocalDate fecha, Integer conceptoId);
    Integer countByFechaAndConceptoLaboralId(LocalDate fecha, Integer idConcepto);
    List<JornadaLaboral> findByFechaBetween(LocalDate fechaDesde, LocalDate fechaHasta);
    List<JornadaLaboral> findAll();
    List<JornadaLaboral> findByEmpleadoNroDocumento(Integer nroDocumento);
    List<JornadaLaboral> findByFechaLessThanEqual(LocalDate fechaHasta);
    List<JornadaLaboral> findByFechaGreaterThanEqual(LocalDate fechaDesde);
    List<JornadaLaboral> findByEmpleadoNroDocumentoAndFechaLessThanEqual(Integer nroDocumento, LocalDate fechaHasta);
    List<JornadaLaboral> findByEmpleadoNroDocumentoAndFechaGreaterThanEqual(Integer nroDocumento, LocalDate fechaDesde);
    List<JornadaLaboral> findByEmpleadoNroDocumentoAndFechaBetween(Integer nroDocumento, LocalDate fechaDesde, LocalDate fechaHasta);
}

