package com.analistas.gym.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.gym.model.domain.MovimientoCaja;
import com.analistas.gym.model.domain.TipoMovimiento;
import com.analistas.gym.model.repository.MovimientoCajaRepository;

@Service
public class MovimientoCajaService {

    @Autowired
    private MovimientoCajaRepository repository;

    // MÉTODO AGREGADO PARA QUE EL CONTROLADOR LO PUEDA USAR
    public List<MovimientoCaja> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin) {
        return repository.findByFechaHoraBetween(inicio, fin);
    }

    public List<MovimientoCaja> obtenerMovimientosDelDia(LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(23, 59, 59);

        return repository.findByFechaHoraBetween(desde, hasta);
    }

    public List<MovimientoCaja> obtenerPorRangoYTipo(
            LocalDate desde,
            LocalDate hasta,
            TipoMovimiento tipo) {

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);

        if (tipo == null) {
            return repository.findByFechaHoraBetween(inicio, fin);
        }

        return repository.findByFechaHoraBetweenAndTipoMovimiento(inicio, fin, tipo);
    }

    public List<MovimientoCaja> obtenerPorFiltros(
            LocalDate desde,
            LocalDate hasta,
            TipoMovimiento tipo,
            String formaPago) {

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);

        // SIN FILTROS
        if (tipo == null && formaPago == null) {
            return repository.findByFechaHoraBetween(inicio, fin);
        }

        // SOLO TIPO
        if (tipo != null && formaPago == null) {
            return repository.findByFechaHoraBetweenAndTipoMovimiento(inicio, fin, tipo);
        }

        // SOLO FORMA DE PAGO
        if (tipo == null) {
            return repository.findByFechaHoraBetweenAndFormaPago(inicio, fin, formaPago);
        }

        // AMBOS FILTROS
        return repository.findByFechaHoraBetweenAndTipoMovimientoAndFormaPago(
                inicio, fin, tipo, formaPago);
    }

    public void guardar(MovimientoCaja movimiento) {
        movimiento.setFechaHora(LocalDateTime.now());
        repository.save(movimiento);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Double calcularTotal(List<MovimientoCaja> movimientos) {
        return movimientos.stream()
                .mapToDouble(MovimientoCaja::getMonto)
                .sum();
    }

    public List<Integer> obtenerTotalesPorMesYAnio(int anio) {

        List<Object[]> resultados = repository.obtenerTotalesPorMes(anio);

        // Inicializamos 12 meses en 0
        List<Integer> totales = new ArrayList<>(Collections.nCopies(12, 0));

        for (Object[] fila : resultados) {
            int mes = ((Number) fila[0]).intValue(); // 1 a 12
            int total = ((Number) fila[1]).intValue();
            totales.set(mes - 1, total);
        }

        return totales;
    }

    // Método para obtener total de inscripciones por mes
    public List<Integer> obtenerInscripcionesPorMesYAnio(int anio) {
        List<Object[]> resultados = repository.obtenerConteoPorMesYTipo(anio, TipoMovimiento.INSCRIPCION);

        List<Integer> conteos = new ArrayList<>(Collections.nCopies(12, 0));

        for (Object[] fila : resultados) {
            int mes = ((Number) fila[0]).intValue(); // 1..12
            int cantidad = ((Number) fila[1]).intValue();
            conteos.set(mes - 1, cantidad);
        }

        return conteos;
    }

}
