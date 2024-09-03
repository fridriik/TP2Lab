package com.turnosrotativos.controller;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
import com.turnosrotativos.service.JornadaLaboralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/jornada")
public class JornadaLaboralController {

    @Autowired
    private JornadaLaboralService jornadaLaboralService;

    @PostMapping
    public ResponseEntity<JornadaResponseDTO> crearJornada(@RequestBody JornadaRequestDTO requestDTO) {
        JornadaResponseDTO responseDTO = jornadaLaboralService.crearJornada(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}