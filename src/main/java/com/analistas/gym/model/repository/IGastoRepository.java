package com.analistas.gym.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.analistas.gym.model.domain.Gasto;

public interface IGastoRepository extends JpaRepository<Gasto, Long> {

    @Query("""
                SELECT MONTH(g.fecha), SUM(g.monto)
                FROM Gasto g
                WHERE YEAR(g.fecha) = :anio
                GROUP BY MONTH(g.fecha)
                ORDER BY MONTH(g.fecha)
            """)
    List<Object[]> obtenerTotalesPorMes(@Param("anio") int anio);
}
