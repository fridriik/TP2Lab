package com.turnosrotativos.repository;

import com.turnosrotativos.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findAll();
    boolean existsByNroDocumento(Integer nroDocumento);
    boolean existsByNroDocumentoAndIdNot(Integer nroDocumento, Integer empleadoId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Integer empleadoId);
}

