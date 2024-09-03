package com.turnosrotativos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turnosrotativos.model.JornadaLaboral;

import java.time.LocalDate;

public class JornadaResponseDTO {

    private Integer id;
    private Integer nroDocumento;
    private String nombreCompleto;
    private LocalDate fecha;
    private String concepto;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer horasTrabajadas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(Integer nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(Integer horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public static JornadaResponseDTO fromEntity(JornadaLaboral jornadaLaboral) {
        JornadaResponseDTO dto = new JornadaResponseDTO();
        dto.setId(jornadaLaboral.getId());
        dto.setNroDocumento(jornadaLaboral.getEmpleado().getNroDocumento());
        dto.setNombreCompleto(jornadaLaboral.getEmpleado().getNombre() + " " + jornadaLaboral.getEmpleado().getApellido());
        dto.setFecha(jornadaLaboral.getFecha());
        dto.setConcepto(jornadaLaboral.getConceptoLaboral().getNombre());
        if (!"Día Libre".equals(jornadaLaboral.getConceptoLaboral().getNombre())) {
            dto.setHorasTrabajadas(jornadaLaboral.getHorasTrabajadas());
        }
        return dto;
    }
}
