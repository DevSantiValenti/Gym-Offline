package com.analistas.gym.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.analistas.gym.model.domain.Socio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
// import java.util.List;
import java.util.Optional;

public interface ISocioRepository extends CrudRepository<Socio, Long> {

    // List<Socio> findByDni(String dni);

    public List<Socio> findByFechaVencimiento(LocalDate fechaVencimiento);

    Optional<Socio> findByDni(String dni);

    List<Socio> findByEliminadoFalse();

    List<Socio> findByEliminadoTrue();

    List<Socio> findByEliminadoTrueOrderByFechaEliminacionDesc();

    List<Socio> findByEliminadoTrueAndFechaEliminacionBetween(
            LocalDateTime desde,
            LocalDateTime hasta);

    @Query("SELECT s FROM Socio s WHERE DATE(s.ultIngreso) = :fecha AND s.eliminado = false ORDER BY s.ultIngreso DESC")
    List<Socio> findByFechaIngreso(@Param("fecha") LocalDate fecha);
}
