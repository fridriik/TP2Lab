package com.turnosrotativos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turnosrotativos.model.ConceptoLaboral;
import com.turnosrotativos.model.Empleado;
import com.turnosrotativos.model.JornadaLaboral;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class JornadaRequestDTO {

    @NotNull(message = "idEmpleado es obligatorio")
    private Integer idEmpleado;

    @NotNull(message = "idConcepto es obligatorio")
    private Integer idConcepto;

    @NotNull(message = "fecha es obligatoria")
    private LocalDate fecha;

    private Integer horasTrabajadas;

    public @NotNull(message = "idEmpleado es obligatorio") Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(@NotNull(message = "idEmpleado es obligatorio") Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public @NotNull(message = "idConcepto es obligatorio") Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(@NotNull(message = "idConcepto es obligatorio") Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public @NotNull(message = "fecha es obligatoria") LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(@NotNull(message = "fecha es obligatoria") LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public JornadaLaboral toEntity(Empleado empleado, ConceptoLaboral conceptoLaboral) {
        JornadaLaboral jornadaLaboral = new JornadaLaboral();
        jornadaLaboral.setEmpleado(empleado);
        jornadaLaboral.setConceptoLaboral(conceptoLaboral);
        jornadaLaboral.setFecha(this.fecha);
        jornadaLaboral.sethorasTrabajadas(this.horasTrabajadas);
        return jornadaLaboral;
    }
}
