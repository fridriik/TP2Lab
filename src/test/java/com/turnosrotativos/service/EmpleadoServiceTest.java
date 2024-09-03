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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoDTO empleadoDTO1, empleadoDTO2;

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

        empleadoDTO2 = new EmpleadoDTO();
        empleadoDTO2.setNombre("Maria");
        empleadoDTO2.setApellido("Perez");
        empleadoDTO2.setEmail("mperez@gmail.com");
        empleadoDTO2.setNroDocumento(30865478);
        empleadoDTO2.setFechaNacimiento(LocalDate.now().minusYears(30));
        empleadoDTO2.setFechaIngreso(LocalDate.now().minusDays(5));
    }

    @Test
    void crearEmpleadoValido() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(false);
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoDTO1.toEntity());

        EmpleadoDTO result = empleadoService.crearEmpleado(empleadoDTO1);

        assertNotNull(result);
        assertEquals(empleadoDTO1.getId(), result.getId());
        assertEquals(empleadoDTO1.getNombre(), result.getNombre());
        assertEquals(empleadoDTO1.getApellido(), result.getApellido());
        assertEquals(empleadoDTO1.getEmail(), result.getEmail());
        assertEquals(empleadoDTO1.getNroDocumento(), result.getNroDocumento());
        assertEquals(empleadoDTO1.getFechaIngreso(), result.getFechaIngreso());
        assertEquals(empleadoDTO1.getFechaNacimiento(), result.getFechaNacimiento());
        assertEquals(empleadoDTO1.getFechaCreacion(), result.getFechaCreacion());

        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoDocumentoDuplicado() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(true);
        assertThrows(ConflictException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO1);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoEmailDuplicado() {
        when(empleadoRepository.existsByNroDocumento(anyInt())).thenReturn(false);
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(ConflictException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO1);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoMenor18() {
        empleadoDTO1.setFechaNacimiento(LocalDate.now().minusYears(17));
        assertThrows(BadRequestException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO1);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoFechaIngresoFutura() {
        empleadoDTO1.setFechaIngreso(LocalDate.now().plusDays(1));
        assertThrows(BadRequestException.class, () -> {
            empleadoService.crearEmpleado(empleadoDTO1);
        });
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void obtenerTodosLosEmpleados() {
        Empleado empleado1 = empleadoDTO1.toEntity();
        Empleado empleado2 = empleadoDTO2.toEntity();

        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleado1, empleado2));
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodosLosEmpleados();

        assertNotNull(empleados);
        assertEquals(2, empleados.size());

        EmpleadoDTO resultEmpleadoDTO1 = empleados.get(0);
        assertEquals(empleadoDTO1.getId(), resultEmpleadoDTO1.getId());
        assertEquals(empleadoDTO1.getFechaCreacion(), resultEmpleadoDTO1.getFechaCreacion());
        EmpleadoDTO resultEmpleadoDTO2 = empleados.get(1);
        assertEquals(empleadoDTO2.getId(), resultEmpleadoDTO2.getId());
        assertEquals(empleadoDTO2.getFechaCreacion(), resultEmpleadoDTO2.getFechaCreacion());

        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void obtenerEmpleadoPorId() {
        Integer empleadoId = 1;
        Empleado empleadoSimulado = empleadoDTO1.toEntity();
        empleadoSimulado.setId(empleadoId);

        when(empleadoRepository.findById(Long.valueOf(empleadoId))).thenReturn(Optional.of(empleadoSimulado));

        EmpleadoDTO empleadoObtenido = empleadoService.obtenerEmpleadoPorId(empleadoId);

        assertNotNull(empleadoObtenido);
        assertEquals(empleadoSimulado.getId(), empleadoObtenido.getId());
        assertEquals(empleadoDTO1.getNombre(), empleadoObtenido.getNombre());
        assertEquals(empleadoDTO1.getApellido(), empleadoObtenido.getApellido());
        assertEquals(empleadoDTO1.getEmail(), empleadoObtenido.getEmail());
        assertEquals(empleadoDTO1.getNroDocumento(), empleadoObtenido.getNroDocumento());
        assertEquals(empleadoDTO1.getFechaIngreso(), empleadoObtenido.getFechaIngreso());
        assertEquals(empleadoDTO1.getFechaNacimiento(), empleadoObtenido.getFechaNacimiento());
        assertEquals(empleadoDTO1.getFechaCreacion(), empleadoObtenido.getFechaCreacion());

        verify(empleadoRepository).findById(Long.valueOf(empleadoId));
    }

    @Test
    public void actualizarEmpleadoExistente() {
        Integer empleadoId = 1;
        empleadoDTO1.setFechaCreacion(LocalDateTime.now());
        Empleado empleadoExistente = empleadoDTO1.toEntity();
        empleadoExistente.setId(empleadoId);

        when(empleadoRepository.findById(Long.valueOf(empleadoId))).thenReturn(Optional.of(empleadoExistente));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmpleadoDTO empleadoActualizado = empleadoService.actualizarEmpleado(empleadoId, empleadoDTO2);

        assertNotNull(empleadoActualizado);
        assertEquals(empleadoExistente.getId(), empleadoActualizado.getId());
        assertEquals(empleadoDTO2.getNombre(), empleadoActualizado.getNombre());
        assertEquals(empleadoDTO2.getApellido(), empleadoActualizado.getApellido());
        assertEquals(empleadoDTO2.getEmail(), empleadoActualizado.getEmail());
        assertEquals(empleadoDTO2.getNroDocumento(), empleadoActualizado.getNroDocumento());
        assertEquals(empleadoDTO2.getFechaIngreso(), empleadoActualizado.getFechaIngreso());
        assertEquals(empleadoDTO2.getFechaNacimiento(), empleadoActualizado.getFechaNacimiento());
        assertEquals(empleadoExistente.getFechaCreacion(), empleadoActualizado.getFechaCreacion());

        verify(empleadoRepository).findById(Long.valueOf(empleadoExistente.getId()));
        verify(empleadoRepository).save(any(Empleado.class));
    }
}