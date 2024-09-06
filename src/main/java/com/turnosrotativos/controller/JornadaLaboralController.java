package com.turnosrotativos.controller;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
import com.turnosrotativos.service.JornadaLaboralService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/jornada")
public class JornadaLaboralController {

    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);

    @Autowired
    private JornadaLaboralService jornadaLaboralService;

    @PostMapping
    public ResponseEntity<JornadaResponseDTO> crearJornada(@Valid @RequestBody JornadaRequestDTO requestDTO) {
        logger.info("Solicitud recibida para crear jornada laboral");
        JornadaResponseDTO responseDTO = jornadaLaboralService.crearJornada(requestDTO);
        logger.info("Solicitud finalizada con éxito, jornada laboral creada con Id: {}", responseDTO.getId());
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JornadaResponseDTO>> obtenerJornadas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) @Min(value = 1000000, message = "El número de documento debe tener al menos 7 dígitos.")
            @Max(value = 99999999, message = "El número de documento no puede tener más de 8 dígitos.") Integer nroDocumento) {
        logger.info("Solicitud recibida para obtener las jornadas laborales");
        List<JornadaResponseDTO> jornadas = jornadaLaboralService.obtenerJornadas(fechaDesde, fechaHasta, nroDocumento);
        return new ResponseEntity<>(jornadas, HttpStatus.OK);
    }
}