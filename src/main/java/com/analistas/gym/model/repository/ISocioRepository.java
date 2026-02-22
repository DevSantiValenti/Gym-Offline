package com.analistas.gym.model.repository;

import org.springframework.data.repository.CrudRepository;

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
}
