package com.turnosrotativos.service;

import com.turnosrotativos.dto.EmpleadoDTO;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.repository.EmpleadoRepository;
import com.turnosrotativos.exception.BadRequestException;
import com.turnosrotativos.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoDTO empleadoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setNombre("German");
        empleadoDTO.setApellido("Zotella");
        empleadoDTO.setEmail("gzotella@gmail.com");
        empleadoDTO.setNroDocumento(30415654);
        empleadoDTO.setFechaNacimiento(LocalDate.now().minusYears(18));
        empleadoDTO.setFechaIngreso(LocalDate.now());
    }

    @Test
    void crearEmpleadoValido() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(false);
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoDTO.toEntity());

        EmpleadoDTO result = empleadoService.crearEmpleado(empleadoDTO);

        assertNotNull(result);
        assertEquals(empleadoDTO.getNombre(), result.getNombre());
        assertEquals(empleadoDTO.getApellido(), result.getApellido());
        assertEquals(empleadoDTO.getEmail(), result.getEmail());
        assertEquals(empleadoDTO.getNroDocumento(), result.getNroDocumento());
        assertEquals(empleadoDTO.getFechaIngreso(), result.getFechaIngreso());
        assertEquals(empleadoDTO.getFechaNacimiento(), result.getFechaNacimiento());

        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoDocumentoDuplicado() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(true);
        assertThrows(ConflictException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoEmailDuplicado() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(false);
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoMenor18() {
        empleadoDTO.setFechaNacimiento(LocalDate.now().minusYears(17));
        assertThrows(BadRequestException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoFechaIngresoFutura() {
        empleadoDTO.setFechaIngreso(LocalDate.now().plusDays(1));
        assertThrows(BadRequestException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}