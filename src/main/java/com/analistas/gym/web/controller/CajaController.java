package com.analistas.gym.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.gym.model.domain.MovimientoCaja;
import com.analistas.gym.model.domain.TipoMovimiento;
import com.analistas.gym.model.service.MovimientoCajaService;

@Controller
@RequestMapping("/caja")
public class CajaController {

    @Autowired
    private MovimientoCajaService movimientoCajaService;

    @GetMapping
    public String verCaja(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String formaPago,
            Model model) {

        TipoMovimiento tipoMovimiento = null;

        if (tipo != null && !tipo.isBlank()) {
            tipoMovimiento = TipoMovimiento.valueOf(tipo);
        }

        if (desde == null) {
            desde = LocalDate.now();
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }

        List<MovimientoCaja> movimientos = movimientoCajaService.obtenerPorFiltros(
                desde,
                hasta,
                tipoMovimiento,
                formaPago != null && !formaPago.isBlank() ? formaPago : null);

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("total", movimientoCajaService.calcularTotal(movimientos));

        return "caja/caja";
    }

    @GetMapping("/pago-diario")
    public String mostrarPagoDiario() {
        return "caja/pago-diario";
    }

    @GetMapping("/eliminar/{id}")
    @Secured({ "ROLE_ADMIN" })
    public String eliminarMovimiento(@PathVariable Long id) {
        movimientoCajaService.eliminar(id);
        return "redirect:/caja";
    }

    @PostMapping("/pago-diario")
    public String registrarPagoDiario(
            @RequestParam String nombreCompleto,
            @RequestParam Integer monto,
            @RequestParam String formaPago,
            RedirectAttributes redirectAttributes) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setActividad("Pase diario");
        movimiento.setSocioNombreCompleto(nombreCompleto);
        movimiento.setDetalle("Pago diario");
        movimiento.setFormaPago(formaPago);
        movimiento.setMonto(monto);
        movimiento.setTipoMovimiento(TipoMovimiento.DIARIO);

        movimientoCajaService.guardar(movimiento);

        redirectAttributes.addFlashAttribute("mensaje", "Pago diario registrado.");

        return "redirect:/caja";
    }
}
