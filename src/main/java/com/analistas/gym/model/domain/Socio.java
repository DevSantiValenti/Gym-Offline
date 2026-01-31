package com.analistas.gym.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "socios")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío.")
    @Size(min = 3, max = 25, message = "El nombre debe tener entre 3 y 25 caracteres.")
    private String nombreCompleto;

    @NotEmpty(message = "El DNI no puede estar vacío.")
    @Size(min = 5, max = 8)
    private String dni;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    private String telefono;

    private String direccion;

    private String profesion;

    private LocalDate fechaAlta;

    private LocalDateTime ultIngreso;

    private LocalDate fechaVencimiento;

    private Integer vecesIngresado;
    
    private Integer saldoPendiente;

    private Boolean cuotaPaga;

    @ManyToOne
    @JoinColumn(name = "actividad_id") // Foreign key hacia ACTIVIDAD
    private Actividad actividad;
}
