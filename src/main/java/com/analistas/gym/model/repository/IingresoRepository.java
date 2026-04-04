package com.analistas.gym.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.analistas.gym.model.domain.Ingreso;

public interface IingresoRepository extends JpaRepository<Ingreso, Long> {

    @Query("SELECT i FROM Ingreso i WHERE DATE(i.fechaHora) = :fecha ORDER BY i.fechaHora DESC")
    List<Ingreso> findByFecha(@Param("fecha") LocalDate fecha);

    @Modifying
    @Query("DELETE FROM Ingreso i WHERE i.socio.id = :id")
    void deleteBySocioId(@Param("id") Long id);

}
