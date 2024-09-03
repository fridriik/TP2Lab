package com.turnosrotativos.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class JornadaLaboral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "concepto_laboral_id", nullable = false)
    private ConceptoLaboral conceptoLaboral;

    @Column(nullable = false)
    private LocalDate fecha;

    private Integer horasTrabajadas;

    public JornadaLaboral(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Empleado getEmpleado() {return empleado;}

    public void setEmpleado(Empleado empleado) {this.empleado = empleado;}

    public ConceptoLaboral getConceptoLaboral() {return conceptoLaboral;}

    public void setConceptoLaboral(ConceptoLaboral conceptoLaboral) {this.conceptoLaboral = conceptoLaboral;}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void sethorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }
}
