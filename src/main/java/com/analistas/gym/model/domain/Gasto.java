package com.analistas.gym.model.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concepto; // Ej: Luz, Equipamiento, Arreglo

    private String descripcion;

    private Integer monto;

    @Enumerated(EnumType.STRING)
    private TipoGasto tipoGasto;

    private LocalDate fecha;

    private String formaPago; // EFECTIVO / TRANSFERENCIA
}
