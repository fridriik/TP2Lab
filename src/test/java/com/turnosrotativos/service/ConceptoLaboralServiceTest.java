package com.turnosrotativos.service;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConceptoLaboralServiceTest {

    @Mock
    private ConceptoLaboralRepository conceptoLaboralRepository;

    @InjectMocks
    private ConceptoLaboralService conceptoLaboralService;

    private ConceptoLaboralDTO conceptoDTO1, conceptoDTO2, conceptoDTO3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        conceptoDTO1 = new ConceptoLaboralDTO(1, "Turno Normal", 6, 8, true);
        conceptoDTO2 = new ConceptoLaboralDTO(2, "Turno Extra", 2, 6, true);
        conceptoDTO3 = new ConceptoLaboralDTO(3, "Día Libre", null, null, false);
    }

    @Test
    void testObtenerTodosLosConceptos() {
        when(conceptoLaboralRepository.findAll())
                .thenReturn(List.of(conceptoDTO1.toEntity(), conceptoDTO2.toEntity(), conceptoDTO3.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerTodosLosConceptos();

        assertEquals(3, result.size());
        assertEquals("Turno Normal", result.get(0).getNombre());
        assertEquals("Turno Extra", result.get(1).getNombre());
        assertEquals("Día Libre", result.get(2).getNombre());
        verify(conceptoLaboralRepository, times(1)).findAll();
    }

    @Test
    void testObtenerConceptoPorId() {
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.of(conceptoDTO1.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorId(1);

        assertEquals(1, result.size());
        assertEquals("Turno Normal", result.get(0).getNombre());
        verify(conceptoLaboralRepository, times(1)).findById(1);
    }

    @Test
    void testObtenerConceptoPorIdNotFound() {
        when(conceptoLaboralRepository.findById(anyInt())).thenReturn(Optional.empty());

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorId(4);

        assertTrue(result.isEmpty());
        verify(conceptoLaboralRepository, times(1)).findById(4);
    }

    @Test
    void testObtenerConceptoPorNombre() {
        when(conceptoLaboralRepository.findByNombreContaining("Turno"))
                .thenReturn(List.of(conceptoDTO1.toEntity(), conceptoDTO2.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorNombre("Turno");

        assertEquals(2, result.size());
        assertEquals("Turno Normal", result.get(0).getNombre());
        assertEquals("Turno Extra", result.get(1).getNombre());
        verify(conceptoLaboralRepository, times(1)).findByNombreContaining("Turno");
    }

    @Test
    void testObtenerConceptoPorIdYNombre() {
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.of(conceptoDTO1.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorIdYNombre(1, "Normal");

        assertEquals(1, result.size());
        assertEquals("Turno Normal", result.get(0).getNombre());
        verify(conceptoLaboralRepository, times(1)).findById(1);
    }

    @Test
    void testObtenerConceptoPorIdYNombreNoMatch() {
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.of(conceptoDTO1.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorIdYNombre(1, "Extra");

        assertTrue(result.isEmpty());
        verify(conceptoLaboralRepository, times(1)).findById(1);
    }
}
