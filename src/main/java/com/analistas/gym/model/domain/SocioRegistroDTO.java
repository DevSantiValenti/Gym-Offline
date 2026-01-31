package com.analistas.gym.model.domain;

import java.time.LocalDate;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class SocioRegistroDTO {

    // --- Paso 1: datos personales ---
    private String nombreCompleto;
    private String dni;
    private LocalDate fechaNacimiento;
    private LocalDate fechaVencimiento;
    private LocalDate fechaAlta;
    private String telefono;
    private String direccion;
    @ManyToOne
    @JoinColumn(name = "actividad_id") // Foreign key hacia ACTIVIDAD
    private Actividad actividad;
    private String profesion;
    // --- Paso 2: datos de membresía ---
    private Integer monto;
    private String formaPago;

}