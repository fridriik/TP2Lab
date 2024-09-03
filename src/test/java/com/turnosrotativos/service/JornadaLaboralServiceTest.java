package com.turnosrotativos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.dto.JornadaRequestDTO;
import com.turnosrotativos.dto.JornadaResponseDTO;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.NotFoundException;
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
import java.util.Optional;

public class JornadaLaboralServiceTest {

    @InjectMocks
    private JornadaLaboralService jornadaLaboralService;

    @Mock
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ConceptoLaboralRepository conceptoLaboralRepository;

    private JornadaRequestDTO jornadaRequestDTO;
    private EmpleadoDTO empleadoDTO;
    private ConceptoLaboralDTO conceptoDTO1, conceptoDTO2, conceptoDTO3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Integer empleadoId = 1;
        empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setId(empleadoId);
        empleadoDTO.setNombre("German");
        empleadoDTO.setApellido("Zotella");
        empleadoDTO.setNroDocumento(30415654);

        conceptoDTO1 = new ConceptoLaboralDTO(1, "Turno Normal", 6, 8, true);
        conceptoDTO2 = new ConceptoLaboralDTO(2, "Turno Extra", 2, 6, true);
        conceptoDTO3 = new ConceptoLaboralDTO(3, "Día Libre", null, null, false);

        jornadaRequestDTO = new JornadaRequestDTO();
        jornadaRequestDTO.setIdEmpleado(empleadoDTO.getId());
        jornadaRequestDTO.setFecha(LocalDate.now());
        jornadaRequestDTO.setIdConcepto(conceptoDTO1.getId());
        jornadaRequestDTO.setHorasTrabajadas(8);
    }

    @Test
    public void testCrearJornadaExito() {
        when(empleadoRepository.findById(Long.valueOf(empleadoDTO.getId()))).thenReturn(Optional.of(empleadoDTO.toEntity()));
        when(conceptoLaboralRepository.findById(conceptoDTO1.getId())).thenReturn(Optional.of(conceptoDTO1.toEntity()));
        JornadaLaboral jornada = jornadaRequestDTO.toEntity(empleadoDTO.toEntity(), conceptoDTO1.toEntity());
        when(jornadaLaboralRepository.save(any(JornadaLaboral.class))).thenReturn(jornada);

        JornadaResponseDTO result = jornadaLaboralService.crearJornada(jornadaRequestDTO);

        assertEquals(jornadaRequestDTO.getHorasTrabajadas(), result.getHorasTrabajadas());
        assertEquals(jornadaRequestDTO.getFecha(), result.getFecha());
        assertEquals(jornadaRequestDTO.getIdEmpleado(), empleadoDTO.getId());
        assertEquals(jornadaRequestDTO.getIdConcepto(), conceptoDTO1.getId());
    }

    @Test
    public void testCrearJornadaEmpleadoNoEncontrado() {
        when(empleadoRepository.findById(Long.valueOf(empleadoDTO.getId()))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                jornadaLaboralService.crearJornada(jornadaRequestDTO));

        assertEquals("No existe el empleado ingresado.", exception.getMessage());
    }

    @Test
    public void testCrearJornadaConceptoNoEncontrado() {
        when(empleadoRepository.findById(Long.valueOf(empleadoDTO.getId()))).thenReturn(Optional.of(empleadoDTO.toEntity()));
        when(conceptoLaboralRepository.findById(jornadaRequestDTO.getIdConcepto())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                jornadaLaboralService.crearJornada(jornadaRequestDTO));

        assertEquals("No existe el concepto ingresado.", exception.getMessage());
    }

    @Test
    public void testCrearJornadaHorasTrabajadasObligatorias() {
        jornadaRequestDTO.setHorasTrabajadas(null);
        when(empleadoRepository.findById(Long.valueOf(empleadoDTO.getId()))).thenReturn(Optional.of(empleadoDTO.toEntity()));
        when(conceptoLaboralRepository.findById(conceptoDTO1.getId())).thenReturn(Optional.of(conceptoDTO1.toEntity()));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                jornadaLaboralService.crearJornada(jornadaRequestDTO));

        assertEquals("'hsTrabajadas' es obligatorio para el concepto ingresado.", exception.getMessage());
    }

    @Test
    public void testCrearJornadaDiaLibreConHorasTrabajadas() {
        jornadaRequestDTO.setIdConcepto(conceptoDTO3.getId());
        jornadaRequestDTO.setHorasTrabajadas(8);
        when(empleadoRepository.findById(Long.valueOf(empleadoDTO.getId()))).thenReturn(Optional.of(empleadoDTO.toEntity()));
        when(conceptoLaboralRepository.findById(conceptoDTO3.getId())).thenReturn(Optional.of(conceptoDTO3.toEntity()));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                jornadaLaboralService.crearJornada(jornadaRequestDTO));

        assertEquals("El concepto ingresado no requiere el ingreso de 'hsTrabajadas'", exception.getMessage());
    }

}