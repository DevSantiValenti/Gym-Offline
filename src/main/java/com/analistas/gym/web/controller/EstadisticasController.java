package com.analistas.gym.web.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analistas.gym.model.service.IGastoService;
import com.analistas.gym.model.service.MovimientoCajaService;

@Controller
@RequestMapping("/estadisticas")
@Secured({ "ROLE_ADMIN" })
public class EstadisticasController {

        @Autowired
        IGastoService gastoService;

        @Autowired
        private MovimientoCajaService cajaService;

        @GetMapping
        public String verEstadisticas(@RequestParam(required = false) Integer anio, Model model) {

                if (anio == null) {
                        anio = LocalDate.now().getYear();
                }

                List<String> meses = List.of(
                                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

                List<Integer> totales = cajaService.obtenerTotalesPorMesYAnio(anio);

                // 🔹 TOTAL ANUAL
                int totalAnual = totales.stream()
                                .mapToInt(Integer::intValue)
                                .sum();

                List<Integer> anios = IntStream
                                .rangeClosed(anio - 4, anio)
                                .boxed()
                                .sorted((a, b) -> b - a)
                                .toList();

                model.addAttribute("titulo", "Estadísticas");
                model.addAttribute("meses", meses);
                model.addAttribute("totales", totales);
                model.addAttribute("totalAnual", totalAnual); // 👈 NUEVO
                model.addAttribute("anioSeleccionado", anio);
                model.addAttribute("anios", anios);

                // Canva de inscripciones por mes:
                List<Integer> totalesIns = cajaService.obtenerTotalesPorMesYAnio(anio);
                List<Integer> cuotasCounts = cajaService.obtenerCuotasPorMesYAnio(anio);
                List<Integer> inscripcionesCounts = cajaService.obtenerInscripcionesPorMesYAnio(anio);

                List<Integer> gastosMensuales = gastoService.obtenerTotalesPorMes(anio);
                model.addAttribute("gastos", gastosMensuales);

                model.addAttribute("meses", meses);
                model.addAttribute("totales", totalesIns);
                model.addAttribute("cuotasCounts", cuotasCounts);
                model.addAttribute("inscripcionesCounts", inscripcionesCounts);
                model.addAttribute("totalAnual", totalAnual);

                return "estadisticas/estadisticas";
        }

}
