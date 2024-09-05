package com.turnosrotativos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turnosrotativos.model.Empleado;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellido() {return apellido;}

    public void setApellido(String apellido) {this.apellido = apellido;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public Integer getNroDocumento() {return nroDocumento;}

    public void setNroDocumento(Integer nroDocumento) {this.nroDocumento = nroDocumento;}

    public LocalDate getFechaNacimiento() {return fechaNacimiento;}

    public void setFechaNacimiento(LocalDate fechaNacimiento) {this.fechaNacimiento = fechaNacimiento;}

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {this.fechaIngreso = fechaIngreso;}

    public LocalDateTime getFechaCreacion() {return fechaCreacion;}

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @JsonIgnore
    @AssertTrue(message = "La fecha de ingreso no puede ser posterior al día de la fecha.")
    public boolean isFechaIngresoValida() {return !fechaIngreso.isAfter(LocalDate.now());}

    @JsonIgnore
    @AssertTrue(message = "La edad del empleado no puede ser menor a 18 años.")
    public boolean isMayorDeEdad() {return Period.between(this.fechaNacimiento, LocalDate.now()).getYears() >= 18;}
}