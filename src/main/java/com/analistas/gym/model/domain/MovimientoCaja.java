package com.analistas.gym.model.domain;

import java.time.LocalDateTime;

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
@Table(name = "movimientos_caja")
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actividad;

    private String socioNombreCompleto;

    private String detalle; // Pago de cuota / Inscripción

    private LocalDateTime fechaHora;

    private String formaPago; // EFECTIVO / TRANSFERENCIA

    private Integer monto;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipoMovimiento; // CUOTA / INSCRIPCION

}
