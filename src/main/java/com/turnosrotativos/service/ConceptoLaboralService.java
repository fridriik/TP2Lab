package com.turnosrotativos.service;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConceptoLaboralService {

    private static final Logger logger = LoggerFactory.getLogger(ConceptoLaboralService.class);
    private final ConceptoLaboralRepository conceptoLaboralRepository;

    public ConceptoLaboralService(ConceptoLaboralRepository conceptoLaboralRepository) {
        this.conceptoLaboralRepository = conceptoLaboralRepository;
    }

    public List<ConceptoLaboralDTO> obtenerConceptosLaborales(Integer id, String nombre) {
        logger.info("Iniciando búsqueda de conceptos laborales con id: {} y nombre: {}", id, nombre);
        if (id != null && nombre != null) {
            return obtenerConceptoLaboralPorIdYNombre(id, nombre);
        }
        if (id != null) {
            logger.info("Buscando conceptos laborales por id: {}", id);
            return obtenerConceptoLaboralPorId(id);
        }
        if (nombre != null) {
            logger.info("Buscando conceptos laborales por nombre: {}", nombre);
            return obtenerConceptoLaboralPorNombre(nombre);
        }
        logger.info("Buscando todos los conceptos laborales");
        return obtenerTodosLosConceptos();
    }

    public List<ConceptoLaboralDTO> obtenerTodosLosConceptos() {
        logger.info("Obteniendo todos los conceptos laborales");
        List<ConceptoLaboral> conceptos = conceptoLaboralRepository.findAll();
        logger.info("Cantidad de conceptos encontrados: {}", conceptos.size());
        return conceptos.stream()
                .map(ConceptoLaboralDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorId(Integer id) {
        logger.info("Buscando concepto laboral por id: {}", id);
        return conceptoLaboralRepository.findById(id)
                .map(concepto -> {
                    logger.info("Concepto laboral encontrado con id: {}", id);
                    return Collections.singletonList(ConceptoLaboralDTO.fromEntity(concepto));
                })
                .orElseGet(() -> {
                    logger.warn("No se encontró concepto laboral con id: {}", id);
                    return Collections.emptyList();
                });
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorNombre(String nombre) {
        logger.info("Buscando concepto laboral por nombre que contenga: {}", nombre);
        List<ConceptoLaboral> conceptos = conceptoLaboralRepository.findByNombreContaining(nombre);
        logger.info("Resultados de la búsqueda para el nombre {}: {}", nombre, conceptos.size());
        return conceptos.stream()
                .map(ConceptoLaboralDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConceptoLaboralDTO> obtenerConceptoLaboralPorIdYNombre(Integer id, String nombre) {
        logger.info("Buscando concepto laboral por id: {} y nombre que contenga: {}", id, nombre);
        return conceptoLaboralRepository.findById(id)
                .stream()
                .filter(concepto -> concepto.getNombre().contains(nombre))
                .map(concepto -> {
                    logger.info("Concepto laboral encontrado con id: {} y nombre que contiene: {}", id, nombre);
                    return ConceptoLaboralDTO.fromEntity(concepto);
                })
                .collect(Collectors.toList());
    }
}