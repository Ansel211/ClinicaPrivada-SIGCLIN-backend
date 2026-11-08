package com.example.demo.dto;

public class NCitasXMedicoDTO {
    private String nombreMedico;
    private Long cantidadCitas;

    public NCitasXMedicoDTO(String nombreMedico, Long cantidadCitas) {
        this.nombreMedico = nombreMedico;
        this.cantidadCitas = cantidadCitas;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public Long getCantidadCitas() {
        return cantidadCitas;
    }

    public void setCantidadCitas(Long cantidadCitas) {
        this.cantidadCitas = cantidadCitas;
    }
}