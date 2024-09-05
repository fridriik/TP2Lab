package com.turnosrotativos.dto;

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

    public JornadaLaboral toEntity(Empleado empleado, ConceptoLaboral conceptoLaboral) {
        JornadaLaboral jornadaLaboral = new JornadaLaboral();
        jornadaLaboral.setEmpleado(empleado);
        jornadaLaboral.setConceptoLaboral(conceptoLaboral);
        jornadaLaboral.setFecha(this.fecha);
        jornadaLaboral.setHorasTrabajadas(this.horasTrabajadas);
        return jornadaLaboral;
    }

    private Integer horasTrabajadas;

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {this.idEmpleado = idEmpleado;}

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(Integer idConcepto) {this.idConcepto = idConcepto;}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
}
