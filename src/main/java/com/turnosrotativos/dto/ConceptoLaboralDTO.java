package com.turnosrotativos.dto;

import com.turnosrotativos.model.ConceptoLaboral;

public class ConceptoLaboralDTO {
    private Integer id;
    private String nombre;
    private Integer hsMinimo;
    private Integer hsMaximo;
    private Boolean laborable;

    public ConceptoLaboralDTO(Integer id, String nombre, Integer hsMinimo, Integer hsMaximo, Boolean laborable) {
        this.id = id;
        this.nombre = nombre;
        this.hsMinimo = hsMinimo;
        this.hsMaximo = hsMaximo;
        this.laborable = laborable;
    }

    public ConceptoLaboralDTO(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getHsMinimo() {
        return hsMinimo;
    }

    public void setHsMinimo(Integer hsMinimo) {
        this.hsMinimo = hsMinimo;
    }

    public Integer getHsMaximo() {
        return hsMaximo;
    }

    public void setHsMaximo(Integer hsMaximo) {
        this.hsMaximo = hsMaximo;
    }

    public Boolean getLaborable() {
        return laborable;
    }

    public void setLaborable(Boolean laborable) {
        this.laborable = laborable;
    }

    public static ConceptoLaboralDTO fromEntity(ConceptoLaboral concepto) {
        ConceptoLaboralDTO dto = new ConceptoLaboralDTO();
        dto.setId(concepto.getId());
        dto.setLaborable(concepto.getLaborable());
        dto.setNombre(concepto.getNombre());
        if (concepto.getHsMaximo() != null) {
            dto.setHsMaximo(concepto.getHsMaximo());
        }
        if (concepto.getHsMinimo() != null) {
            dto.setHsMinimo(concepto.getHsMinimo());
        }
        return dto;
    }

    public ConceptoLaboral toEntity() {
        ConceptoLaboral conceptoLaboral = new ConceptoLaboral();
        conceptoLaboral.setId(this.id);
        conceptoLaboral.setNombre(this.nombre);
        conceptoLaboral.setHsMinimo(this.hsMinimo);
        conceptoLaboral.setHsMaximo(this.hsMaximo);
        conceptoLaboral.setLaborable(this.laborable);
        return conceptoLaboral;
    }
}
