package com.turnosrotativos.repository;

import com.turnosrotativos.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    boolean existsByNroDocumento(int nroDocumento);
    boolean existsByEmail(String email);
    List<Empleado> findAll();
}