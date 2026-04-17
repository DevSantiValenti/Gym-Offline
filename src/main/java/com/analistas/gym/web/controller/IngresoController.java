package com.analistas.gym.web.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analistas.gym.model.domain.Ingreso;
import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.service.ISocioService;
import com.analistas.gym.model.service.IingresoService;

@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    @Autowired
    private IingresoService ingresoService;

    @GetMapping
    public String verIngresosHoy(Model model) {

        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"));

        List<Ingreso> ingresos = ingresoService.obtenerIngresosPorFecha(hoy);

        model.addAttribute("ingresos", ingresos);
        model.addAttribute("fechaSeleccionada", hoy);
        model.addAttribute("titulo", "Ingresos del día");

        return "ingresos";
    }

    @GetMapping("/filtrar")
    public String filtrarPorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        List<Ingreso> ingresos = ingresoService.obtenerIngresosPorFecha(fecha);

        model.addAttribute("ingresos", ingresos);
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("titulo", "Ingresos filtrados");

        return "ingresos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarIngreso(@PathVariable Long id, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        ingresoService.eliminarIngreso(id);
        if (fecha != null) {
            return "redirect:/ingresos/filtrar?fecha=" + fecha;
        }
        return "redirect:/ingresos";
    }
}
