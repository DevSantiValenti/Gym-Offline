package com.analistas.gym.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.analistas.gym.model.domain.MovimientoCaja;
import com.analistas.gym.model.domain.TipoMovimiento;
import com.analistas.gym.model.repository.MovimientoCajaRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class MovimientoCajaService {

    private static final ZoneId ZONA_BUENOS_AIRES = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final int MINUTOS_ANTIDUPLICADO = 5;

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

    public synchronized void guardar(MovimientoCaja movimiento) {
        LocalDateTime ahora = LocalDateTime.now(ZONA_BUENOS_AIRES);

        if (esMovimientoDuplicadoReciente(movimiento, ahora)) {
            return;
        }

        movimiento.setFechaHora(ahora);
        completarDatosDeAuditoria(movimiento);
        repository.save(movimiento);
    }

    private void completarDatosDeAuditoria(MovimientoCaja movimiento) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            movimiento.setUsuarioCreador(authentication.getName());
        }

        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        movimiento.setIpCreacion(obtenerIpCliente(request));
        movimiento.setUserAgentCreacion(recortar(request.getHeader("User-Agent"), 512));
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String recortar(String valor, int maximo) {
        if (valor == null || valor.length() <= maximo) {
            return valor;
        }

        return valor.substring(0, maximo);
    }

    private boolean esMovimientoDuplicadoReciente(MovimientoCaja movimiento, LocalDateTime ahora) {
        if (movimiento.getSocioId() == null
                || movimiento.getTipoMovimiento() == null
                || movimiento.getDetalle() == null
                || movimiento.getMonto() == null) {
            return false;
        }

        LocalDateTime desde = ahora.minusMinutes(MINUTOS_ANTIDUPLICADO);

        return repository
                .findFirstBySocioIdAndTipoMovimientoAndDetalleAndMontoAndFechaHoraBetweenOrderByFechaHoraDesc(
                        movimiento.getSocioId(),
                        movimiento.getTipoMovimiento(),
                        movimiento.getDetalle(),
                        movimiento.getMonto(),
                        desde,
                        ahora)
                .isPresent();
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Long calcularTotal(List<MovimientoCaja> movimientos) {
        return movimientos.stream()
                .mapToLong(MovimientoCaja::getMonto)
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

    public List<Integer> obtenerCuotasPorMesYAnio(int anio) {
        List<Integer> conteos = new ArrayList<>(Collections.nCopies(12, 0));

        LocalDateTime inicio = LocalDate.of(anio, 1, 1).atStartOfDay();
        LocalDateTime fin = LocalDate.of(anio, 12, 31).atTime(23, 59, 59);

        List<MovimientoCaja> movimientos = repository.findByFechaHoraBetween(inicio, fin);

        for (MovimientoCaja movimiento : movimientos) {
            if (movimiento.getFechaHora() == null) {
                continue;
            }

            boolean esPagoDeCuota = movimiento.getTipoMovimiento() == TipoMovimiento.CUOTA
                    || "Pago de cuota".equalsIgnoreCase(movimiento.getDetalle());

            if (!esPagoDeCuota) {
                continue;
            }

            int mes = movimiento.getFechaHora().getMonthValue() - 1;
            conteos.set(mes, conteos.get(mes) + 1);
        }

        return conteos;
    }

}
