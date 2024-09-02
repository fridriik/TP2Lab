package com.turnosrotativos.controller;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.service.ConceptoLaboralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConceptoLaboralController {

    private final ConceptoLaboralService conceptoLaboralService;

    public ConceptoLaboralController(ConceptoLaboralService conceptoLaboralService) {
        this.conceptoLaboralService = conceptoLaboralService;
    }

    @GetMapping("/concepto-laboral")
    public ResponseEntity<List<ConceptoLaboralDTO>> obtenerConceptosLaborales(
            @RequestParam(required = false)Integer id,
            @RequestParam(required = false)String nombre) {
        List<ConceptoLaboralDTO> conceptos = conceptoLaboralService.obtenerConceptosLaborales(id, nombre);
        return ResponseEntity.ok(conceptos);
    }
}
