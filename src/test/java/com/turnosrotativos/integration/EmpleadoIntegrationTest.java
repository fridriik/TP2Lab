package com.turnosrotativos.integration;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class EmpleadoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Test
    public void whenCrearEmpleadoValidoThenReturnEmpleadoCreado() throws Exception {
        EmpleadoDTO empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setNombre("German");
        empleadoDTO.setApellido("Zotella");
        empleadoDTO.setEmail("gzotella@gmail.com");
        empleadoDTO.setNroDocumento(30415654);
        empleadoDTO.setFechaNacimiento(LocalDate.now().minusYears(18));
        empleadoDTO.setFechaIngreso(LocalDate.now());

        mockMvc.perform(post("/empleado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("German"))
                .andExpect(jsonPath("$.apellido").value("Zotella"))
                .andExpect(jsonPath("$.email").value("gzotella@gmail.com"))
                .andExpect(jsonPath("$.nroDocumento").value(30415654))
                .andExpect(jsonPath("$.fechaIngreso").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.fechaNacimiento").value(LocalDate.now().minusYears(18).toString()));

        assertTrue(empleadoRepository.existsByEmail("gzotella@gmail.com"));
    }
}