package com.turnosrotativos.service;

import com.turnosrotativos.dto.ConceptoLaboralDTO;
import com.turnosrotativos.repository.ConceptoLaboralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void obtenerTodosLosConceptos() {
        when(conceptoLaboralRepository.findAll()).thenReturn(
                Arrays.asList(conceptoDTO1.toEntity(), conceptoDTO2.toEntity(), conceptoDTO3.toEntity()));

        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerTodosLosConceptos();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Turno Normal", resultado.get(0).getNombre());
        assertEquals(2, resultado.get(1).getHsMinimo());
        assertEquals(false, resultado.get(2).getLaborable());

        verify(conceptoLaboralRepository, times(1)).findAll();
    }

    @Test
    void obtenerConceptoPorId() {
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.of(conceptoDTO1.toEntity()));

        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerConceptoLaboralPorId(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getId());

        verify(conceptoLaboralRepository, times(1)).findById(1);
    }

    @Test
    void obtenerConceptoLaboralPorIdNoEncontrado() {
        when(conceptoLaboralRepository.findById(1)).thenReturn(Optional.empty());

        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerConceptoLaboralPorId(1);

        assertTrue(resultado.isEmpty());
        verify(conceptoLaboralRepository, times(1)).findById(1);
    }

    @Test
    void obtenerConceptoPorNombre() {
        when(conceptoLaboralRepository.findByNombreContaining("Turno Normal")).thenReturn(Arrays.asList(conceptoDTO1.toEntity(), conceptoDTO2.toEntity()));

        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerConceptoLaboralPorNombre("Turno Normal");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Turno Normal", resultado.get(0).getNombre());

        verify(conceptoLaboralRepository, times(1)).findByNombreContaining("Turno Normal");
    }

    @Test
    void testObtenerConceptoLaboralPorNombreNoEncontrado() {
        when(conceptoLaboralRepository.findByNombreContaining("Turno Inexistente")).thenReturn(Collections.emptyList());

        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerConceptoLaboralPorNombre("Turno Inexistente");

        assertTrue(resultado.isEmpty());

        verify(conceptoLaboralRepository, times(1)).findByNombreContaining("Turno Inexistente");
    }


    @Test
    void testObtenerConceptoLaboralPorIdYNombre() {
        when(conceptoLaboralRepository.findByIdAndNombreContaining(1, "Turno Normal")).thenReturn(List.of(conceptoDTO1.toEntity()));

        List<ConceptoLaboralDTO> result = conceptoLaboralService.obtenerConceptoLaboralPorIdYNombre(1, "Turno Normal");

        assertEquals(1, result.size());
        assertEquals("Turno Normal", result.get(0).getNombre());

        verify(conceptoLaboralRepository, times(1)).findByIdAndNombreContaining(1, "Turno Normal");
    }

    @Test
    void testObtenerConceptosLaboralesSinParametros() {
        when(conceptoLaboralRepository.findAll()).thenReturn(
                Arrays.asList(conceptoDTO1.toEntity(), conceptoDTO2.toEntity(), conceptoDTO3.toEntity()));
        List<ConceptoLaboralDTO> resultado = conceptoLaboralService.obtenerConceptosLaborales(null, null);
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(conceptoLaboralRepository, times(1)).findAll();
    }
}
