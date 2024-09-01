package com.turnosrotativos.dto;

import com.turnosrotativos.model.Empleado;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmpleadoDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'nombre'.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'apellido'.")
    private String apellido;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email ingresado no es correcto.")
    private String email;

    @NotNull(message = "El número de documento es obligatorio.")
    @DecimalMin(value = "1000000", message = "El número de documento debe tener al menos 7 dígitos.")
    @DecimalMax(value = "99999999", message = "El número de documento no puede tener más de 8 dígitos.")
    private Integer nroDocumento;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de ingreso es obligatoria.")
    private LocalDate fechaIngreso;

    private LocalDateTime fechaCreacion;

    public EmpleadoDTO(Integer id, String nombre, String apellido,
                       String email, Integer nroDocumento,
                       LocalDate fechaNacimiento, LocalDate fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.nroDocumento = nroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
    }

    public EmpleadoDTO(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotBlank(message = "El nombre es obligatorio.") @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'nombre'.") String getNombre() {
        return nombre;
    }

    public void setNombre(@NotBlank(message = "El nombre es obligatorio.") @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'nombre'.") String nombre) {
        this.nombre = nombre;
    }

    public @NotBlank(message = "El apellido es obligatorio.") @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'apellido'.") String getApellido() {
        return apellido;
    }

    public void setApellido(@NotBlank(message = "El apellido es obligatorio.") @Pattern(regexp = "^[a-zA-Z]+$", message = "Solo se permiten letras en el campo 'apellido'.") String apellido) {
        this.apellido = apellido;
    }

    public @NotBlank(message = "El email es obligatorio.") @Email(message = "El email ingresado no es correcto.") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "El email es obligatorio.") @Email(message = "El email ingresado no es correcto.") String email) {
        this.email = email;
    }

    public @NotNull(message = "El número de documento es obligatorio.") @DecimalMin(value = "1000000", message = "El número de documento debe tener al menos 7 dígitos.") @DecimalMax(value = "99999999", message = "El número de documento no puede tener más de 8 dígitos.") Integer getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(@NotNull(message = "El número de documento es obligatorio.") @DecimalMin(value = "1000000", message = "El número de documento debe tener al menos 7 dígitos.") @DecimalMax(value = "99999999", message = "El número de documento no puede tener más de 8 dígitos.") Integer nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public @NotNull(message = "La fecha de nacimiento es obligatoria.") LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(@NotNull(message = "La fecha de nacimiento es obligatoria.") LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(@NotNull(message = "La fecha de ingreso es obligatoria.") @Past(message = "La fecha de ingreso no puede ser posterior al día de la fecha.") LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Empleado toEntity() {
        Empleado empleado = new Empleado();
        empleado.setId(this.id);
        empleado.setNroDocumento(this.nroDocumento);
        empleado.setNombre(this.nombre);
        empleado.setApellido(this.apellido);
        empleado.setEmail(this.email);
        empleado.setFechaNacimiento(this.fechaNacimiento);
        empleado.setFechaIngreso(this.fechaIngreso);
        return empleado;
    }

    public static EmpleadoDTO fromEntity(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getEmail(),
                empleado.getNroDocumento(),
                empleado.getFechaNacimiento(),
                empleado.getFechaIngreso()
        );
        dto.setFechaCreacion(empleado.getFechaCreacion());
        return dto;
    }
}