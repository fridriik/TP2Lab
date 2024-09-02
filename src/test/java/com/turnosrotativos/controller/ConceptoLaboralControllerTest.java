package com.turnosrotativos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConceptoLaboralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        //data.sql se ejecuta automáticamente
    }

    @Test
    void testObtenerTodosLosConceptos() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nombre", is("Turno Normal")))
                .andExpect(jsonPath("$[1].nombre", is("Turno Extra")))
                .andExpect(jsonPath("$[2].nombre", is("Día Libre")));
    }

    @Test
    void testObtenerConceptoPorId() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Turno Normal")));
    }

    @Test
    void testObtenerConceptoPorNombre() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .param("nombre", "Turno Normal")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Turno Normal")));
    }

    @Test
    void testObtenerConceptoPorIdYNombre() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .param("id", "1")
                        .param("nombre", "Turno Normal")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Turno Normal")));
    }

    @Test
    void testObtenerConceptoInexistente() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .param("id", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testObtenerConceptoPorNombreParcial() throws Exception {
        mockMvc.perform(get("/concepto-laboral")
                        .param("nombre", "Turno")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Turno Normal")))
                .andExpect(jsonPath("$[1].nombre", is("Turno Extra")));
    }
}
