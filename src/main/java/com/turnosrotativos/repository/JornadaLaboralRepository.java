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
}

