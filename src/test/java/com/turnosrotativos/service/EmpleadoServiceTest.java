package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.NotFoundException;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.repository.JornadaLaboralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private JornadaLaboralRepository jornadaLaboralRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoDTO empleadoDTO1, empleadoDTO2;
    private Empleado empleado1, empleado2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empleadoDTO1 = new EmpleadoDTO();
        empleadoDTO1.setId(1);
        empleadoDTO1.setNombre("German");
        empleadoDTO1.setApellido("Zotella");
        empleadoDTO1.setEmail("gzotella@gmail.com");
        empleadoDTO1.setNroDocumento(30415654);
        empleadoDTO1.setFechaNacimiento(LocalDate.now().minusYears(18));
        empleadoDTO1.setFechaIngreso(LocalDate.now());

        empleadoDTO2 = new EmpleadoDTO();
        empleadoDTO2.setId(2);
        empleadoDTO2.setNombre("Maria");
        empleadoDTO2.setApellido("Perez");
        empleadoDTO2.setEmail("mperez@gmail.com");
        empleadoDTO2.setNroDocumento(30865478);
        empleadoDTO2.setFechaNacimiento(LocalDate.now().minusYears(30));
        empleadoDTO2.setFechaIngreso(LocalDate.now().minusDays(5));

        empleado1 = empleadoDTO1.toEntity();
        empleado2 = empleadoDTO2.toEntity();
    }

    @Test
    void crearEmpleadoValido() {
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado1);

        EmpleadoDTO result = empleadoService.crearEmpleado(empleadoDTO1);

        assertNotNull(result);
        assertEquals(empleadoDTO1.getId(), result.getId());
        assertEquals(empleadoDTO1.getNombre(), result.getNombre());
        assertEquals(empleadoDTO1.getEmail(), result.getEmail());

        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void obtenerTodosLosEmpleados() {
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleado1, empleado2));

        List<EmpleadoDTO> result = empleadoService.obtenerTodosLosEmpleados();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getId().equals(empleado1.getId())));
        assertTrue(result.stream().anyMatch(e -> e.getId().equals(empleado2.getId())));
    }

    @Test
    void obtenerEmpleadoPorIdExistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado1));

        EmpleadoDTO result = empleadoService.obtenerEmpleadoPorId(1);

        assertNotNull(result);
        assertEquals(empleado1.getId(), result.getId());
        assertEquals(empleado1.getNombre(), result.getNombre());

        verify(empleadoRepository).findById(anyLong());
    }

    @Test
    void obtenerEmpleadoPorIdInexistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            empleadoService.obtenerEmpleadoPorId(1);
        });

        assertEquals("No se encontró el empleado con Id: 1", exception.getMessage());
    }

    @Test
    void actualizarEmpleadoExistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado1));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado1);

        EmpleadoDTO result = empleadoService.actualizarEmpleado(1, empleadoDTO1);

        assertNotNull(result);
        assertEquals(empleadoDTO1.getId(), result.getId());
        assertEquals(empleadoDTO1.getNombre(), result.getNombre());

        verify(empleadoRepository).findById(anyLong());
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void actualizarEmpleadoInexistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            empleadoService.actualizarEmpleado(1, empleadoDTO1);
        });

        assertEquals("No se encontró el empleado con Id: 1", exception.getMessage());
    }

    @Test
    void eliminarEmpleadoExistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado1));
        when(jornadaLaboralRepository.countByEmpleadoId(anyInt())).thenReturn(0);

        empleadoService.eliminarEmpleado(1);

        verify(empleadoRepository).delete(any(Empleado.class));
    }

    @Test
    void eliminarEmpleadoConJornadasAsociadas() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado1));
        when(jornadaLaboralRepository.countByEmpleadoId(anyInt())).thenReturn(1);

        Exception exception = assertThrows(BadRequestException.class, () -> {
            empleadoService.eliminarEmpleado(1);
        });

        assertEquals("No es posible eliminar un empleado con jornadas asociadas.", exception.getMessage());
    }

    @Test
    void eliminarEmpleadoInexistente() {
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            empleadoService.eliminarEmpleado(1);
        });

        assertEquals("No se encontró el empleado con Id: 1", exception.getMessage());
    }
}