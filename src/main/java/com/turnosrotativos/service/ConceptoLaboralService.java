package com.turnosrotativos.service;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConceptoLaboralService {

    private final ConceptoLaboralRepository conceptoLaboralRepository;

    public ConceptoLaboralService(ConceptoLaboralRepository conceptoLaboralRepository) {
        this.conceptoLaboralRepository = conceptoLaboralRepository;
    }

    public List<ConceptoLaboralDTO> obtenerConceptosLaborales(Integer id, String nombre) {
        if (id != null && nombre != null) {
            return obtenerConceptoLaboralPorIdYNombre(id, nombre);
        } else if (id != null) {
            return obtenerConceptoLaboralPorId(id);
        } else if (nombre != null) {
            return obtenerConceptoLaboralPorNombre(nombre);
        } else {
            return obtenerTodosLosConceptos();
        }
    }

    public List<ConceptoLaboralDTO> obtenerTodosLosConceptos() {
        List<ConceptoLaboral> conceptos = conceptoLaboralRepository.findAll();
        return conceptos.stream()
                .map(ConceptoLaboralDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorId(Integer id) {
        return conceptoLaboralRepository.findById(id)
                .map(concepto -> List.of(ConceptoLaboralDTO.fromEntity(concepto)))
                .orElse(Collections.emptyList());
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorNombre(String nombre) {
        List<ConceptoLaboral> conceptos = conceptoLaboralRepository.findByNombreContaining(nombre);
        System.out.println("Resultados de la búsqueda: " + conceptos);
        return conceptos.stream()
                .map(ConceptoLaboralDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorIdYNombre(Integer id, String nombre) {
        List<ConceptoLaboral> conceptos = conceptoLaboralRepository.findByIdAndNombreContaining(id, nombre);
        return conceptos.stream()
                .map(ConceptoLaboralDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
