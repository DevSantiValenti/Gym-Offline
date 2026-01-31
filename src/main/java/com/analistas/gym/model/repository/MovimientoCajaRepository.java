package com.analistas.gym.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.analistas.gym.model.domain.MovimientoCaja;
import com.analistas.gym.model.domain.TipoMovimiento;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {

        List<MovimientoCaja> findByFechaHoraBetween(
                        LocalDateTime desde,
                        LocalDateTime hasta);

        List<MovimientoCaja> findByFechaHoraBetweenAndTipoMovimiento(
                        LocalDateTime desde,
                        LocalDateTime hasta,
                        TipoMovimiento tipoMovimiento);

        List<MovimientoCaja> findByFechaHoraBetweenAndFormaPago(
                        LocalDateTime desde,
                        LocalDateTime hasta,
                        String formaPago);

        List<MovimientoCaja> findByFechaHoraBetweenAndTipoMovimientoAndFormaPago(
                        LocalDateTime desde,
                        LocalDateTime hasta,
                        TipoMovimiento tipoMovimiento,
                        String formaPago);

        @Query("""
                            SELECT MONTH(m.fechaHora), SUM(m.monto)
                            FROM MovimientoCaja m
                            WHERE YEAR(m.fechaHora) = :anio
                            GROUP BY MONTH(m.fechaHora)
                            ORDER BY MONTH(m.fechaHora)
                        """)
        List<Object[]> obtenerTotalesPorMes(@Param("anio") int anio);

        //Consulta para obtener cantidad de inscripciones por mes
        @Query("""
                            SELECT MONTH(m.fechaHora), COUNT(m)
                            FROM MovimientoCaja m
                            WHERE YEAR(m.fechaHora) = :anio AND m.tipoMovimiento = :tipo
                            GROUP BY MONTH(m.fechaHora)
                            ORDER BY MONTH(m.fechaHora)
                        """)
        List<Object[]> obtenerConteoPorMesYTipo(@Param("anio") int anio, @Param("tipo") TipoMovimiento tipo);

}
