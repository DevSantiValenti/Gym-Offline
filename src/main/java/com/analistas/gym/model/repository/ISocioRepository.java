package com.analistas.gym.model.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    long countByEliminadoFalse();

    @Query("""
            SELECT s FROM Socio s
            LEFT JOIN s.actividad a
            WHERE s.eliminado = false
            AND (:busqueda IS NULL OR :busqueda = ''
                OR LOWER(s.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(s.dni) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(COALESCE(s.telefono, '')) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(COALESCE(a.nombre, '')) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            """)
    Page<Socio> buscarActivosParaListado(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("""
            SELECT s FROM Socio s
            WHERE s.eliminado = false
            AND s.fechaVencimiento <= :hoy
            AND (s.cuotaPaga IS NULL OR s.cuotaPaga = true)
            """)
    List<Socio> buscarActivosConCuotaVencidaParaActualizar(@Param("hoy") LocalDate hoy);

    @Query("SELECT s FROM Socio s WHERE DATE(s.ultIngreso) = :fecha AND s.eliminado = false ORDER BY s.ultIngreso DESC")
    List<Socio> findByFechaIngreso(@Param("fecha") LocalDate fecha);

    public Optional<Socio> findByDniAndEliminadoFalse(String dni);

    public Optional<Socio> findByDniAndEliminadoTrue(String dni);
}
