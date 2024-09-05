package com.turnosrotativos.controller;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.service.ConceptoLaboralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/concepto-laboral")
public class ConceptoLaboralController {

    private static final Logger logger = LoggerFactory.getLogger(ConceptoLaboralController.class);

    @Autowired
    private ConceptoLaboralService conceptoLaboralService;

    @GetMapping
    public ResponseEntity<List<ConceptoLaboralDTO>> obtenerConceptosLaborales(
            @RequestParam(required = false)Integer id,
            @RequestParam(required = false)String nombre) {
        logger.info("Solicitud recibida para obtener los conceptos laborales");
        List<ConceptoLaboralDTO> conceptos = conceptoLaboralService.obtenerConceptosLaborales(id, nombre);
        return ResponseEntity.ok(conceptos);
    }
}