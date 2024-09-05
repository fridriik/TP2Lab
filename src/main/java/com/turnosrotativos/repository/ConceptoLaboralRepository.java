package com.turnosrotativos.repository;

import com.turnosrotativos.model.ConceptoLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConceptoLaboralRepository extends JpaRepository<ConceptoLaboral, Integer> {
    List<ConceptoLaboral> findByNombreContaining(String nombre);
}
