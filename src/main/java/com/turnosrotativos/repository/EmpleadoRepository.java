package com.turnosrotativos.repository;

import com.turnosrotativos.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    boolean existsByNroDocumento(int nroDocumento);
    boolean existsByEmail(String email);
}