package com.turnosrotativos.integration;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private EmpleadoDTO empleadoDTO1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        empleadoDTO1 = new EmpleadoDTO();
        empleadoDTO1.setNombre("German");
        empleadoDTO1.setApellido("Zotella");
        empleadoDTO1.setEmail("gzotella@gmail.com");
        empleadoDTO1.setNroDocumento(30415654);
        empleadoDTO1.setFechaNacimiento(LocalDate.now().minusYears(18));
        empleadoDTO1.setFechaIngreso(LocalDate.now());
    }

    @Test
    public void whenCrearEmpleadoValidoThenReturnEmpleadoCreado() throws Exception {
        empleadoRepository.deleteAll();
        mockMvc.perform(post("/empleado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("German"))
                .andExpect(jsonPath("$.apellido").value("Zotella"))
                .andExpect(jsonPath("$.email").value("gzotella@gmail.com"))
                .andExpect(jsonPath("$.nroDocumento").value(30415654))
                .andExpect(jsonPath("$.fechaIngreso").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.fechaNacimiento").value(LocalDate.now().minusYears(18).toString()));

        assertTrue(empleadoRepository.existsByEmail("gzotella@gmail.com"));
    }

    @Test
    public void whenObtenerEmpleadosThenReturnList() throws Exception {
        empleadoRepository.save(empleadoDTO1.toEntity());
        mockMvc.perform(get("/empleado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void whenNoEmpleadosThenReturnEmptyList() throws Exception {
        empleadoRepository.deleteAll();
        mockMvc.perform(get("/empleado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}