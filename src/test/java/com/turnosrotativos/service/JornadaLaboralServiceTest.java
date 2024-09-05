package com.turnosrotativos.service;

import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
import com.turnosrotativos.exception.NotFoundException;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.model.JornadaLaboral;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.repository.JornadaLaboralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaLaboralServiceTest {

    @InjectMocks
    private JornadaLaboralService jornadaLaboralService;

    @Mock
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ConceptoLaboralRepository conceptoLaboralRepository;

    @Mock
    private ValidadorService validadorService;

    private JornadaRequestDTO jornadaRequestDTO;
    private Empleado empleado;
    private ConceptoLaboral conceptoLaboral;
    private JornadaLaboral jornadaLaboral;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empleado = new Empleado();
        empleado.setId(1);
        empleado.setNroDocumento(30415654);

        conceptoLaboral = new ConceptoLaboral();
        conceptoLaboral.setId(1);
        conceptoLaboral.setNombre("Turno Normal");
        conceptoLaboral.setHsMinimo(6);
        conceptoLaboral.setHsMaximo(8);

        jornadaRequestDTO = new JornadaRequestDTO();
        jornadaRequestDTO.setIdEmpleado(1);
        jornadaRequestDTO.setIdConcepto(1);
        jornadaRequestDTO.setFecha(LocalDate.now());
        jornadaRequestDTO.setHorasTrabajadas(8);

        jornadaLaboral = new JornadaLaboral();
        jornadaLaboral.setEmpleado(empleado);
        jornadaLaboral.setConceptoLaboral(conceptoLaboral);
        jornadaLaboral.setFecha(jornadaRequestDTO.getFecha());
        jornadaLaboral.setHorasTrabajadas(jornadaRequestDTO.getHorasTrabajadas());
    }

    @Test
    void testCrearJornadaExitosa() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.of(conceptoLaboral));
        when(jornadaLaboralRepository.save(any(JornadaLaboral.class))).thenReturn(jornadaLaboral);
        JornadaResponseDTO response = jornadaLaboralService.crearJornada(jornadaRequestDTO);
        assertNotNull(response);
        verify(validadorService).validarHorasTrabajadas(conceptoLaboral, 8);
        verify(jornadaLaboralRepository).save(any(JornadaLaboral.class));
    }

    @Test
    void testCrearJornadaEmpleadoNoEncontrado() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            jornadaLaboralService.crearJornada(jornadaRequestDTO));
        assertEquals("No existe el empleado ingresado.", exception.getMessage());
    }

    @Test
    void testCrearJornadaConceptoNoEncontrado() {
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            jornadaLaboralService.crearJornada(jornadaRequestDTO));
        assertEquals("No existe el concepto ingresado.", exception.getMessage());
    }

    @Test
    void testObtenerJornadasSinDocumento() {
        when(jornadaLaboralRepository.findByFechaBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(jornadaLaboral));
        List<JornadaResponseDTO> response = jornadaLaboralService.obtenerJornadas(LocalDate.now().minusDays(1), LocalDate.now(), null);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(jornadaLaboralRepository).findByFechaBetween(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testObtenerJornadasConDocumento() {
        when(empleadoRepository.existsByNroDocumento(30415654)).thenReturn(true);
        when(jornadaLaboralRepository.findByEmpleadoNroDocumentoAndFechaBetween(eq(30415654), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(jornadaLaboral));
        List<JornadaResponseDTO> response = jornadaLaboralService.obtenerJornadas(LocalDate.now().minusDays(1), LocalDate.now(), 30415654);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        verify(jornadaLaboralRepository).findByEmpleadoNroDocumentoAndFechaBetween(eq(30415654), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testObtenerJornadasEmpleadoNoEncontrado() {
        when(empleadoRepository.existsByNroDocumento(30415654)).thenReturn(false);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            jornadaLaboralService.obtenerJornadas(LocalDate.now().minusDays(1), LocalDate.now(), 30415654));
        assertEquals("No existe un empleado con el número de documento ingresado.", exception.getMessage());
    }
}