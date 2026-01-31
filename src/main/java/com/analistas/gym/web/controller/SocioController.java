package com.analistas.gym.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.gym.model.domain.Actividad;
import com.analistas.gym.model.domain.MovimientoCaja;
import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.domain.SocioRegistroDTO;
import com.analistas.gym.model.domain.TipoMovimiento;
import com.analistas.gym.model.service.IActividadService;
import com.analistas.gym.model.service.ISocioService;
import com.analistas.gym.model.service.MovimientoCajaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/socios")
@SessionAttributes("socioRegistro")
public class SocioController {

    @Autowired
    ISocioService socioService;

    @Autowired
    private MovimientoCajaService cajaService;

    @Autowired
    IActividadService actividadService;

    @GetMapping("/listadoAdmin")
    public String listadoSocios(Model model) {
        model.addAttribute("titulo", "Listado de Socios");
        model.addAttribute("socios", socioService.listarSociosActualizados());
        return "socios/socios-list-admin.html";
    }

    // Este método se ejecuta automáticamente antes de cualquier handler
    @ModelAttribute("socioRegistro")
    public SocioRegistroDTO inicializarSocioRegistro() {
        return new SocioRegistroDTO();
    }

    // Paso 1
    @GetMapping("/nuevo")
    public String nuevoSocio(Model model) {

        model.addAttribute("socio", new Socio());
        model.addAttribute("modo", "nuevo");

        return "socios/socios-form.html";
    }

    // Despues del paso anterior, al dar click en siguiente:

    @PostMapping("/nuevo")
    public String procesarSocio1(@Valid @ModelAttribute("socioRegistro") SocioRegistroDTO dto, BindingResult result,
            RedirectAttributes redirectAttributes, @ModelAttribute("socio") Socio socio) {

        if (result.hasErrors()) {
            // Si hay errores, vuelve al paso 1
            return "socios/socios-form.html";
        }

        
        if (socio.getId() != null) {

            Socio socioDB = socioService.buscarPorId(socio.getId());

            socioDB.setNombreCompleto(socio.getNombreCompleto());
            socioDB.setDni(socio.getDni());
            socioDB.setFechaNacimiento(socio.getFechaNacimiento());
            socioDB.setTelefono(socio.getTelefono());
            socioDB.setDireccion(socio.getDireccion());
            socioDB.setProfesion(socio.getProfesion());

            socioService.guardar(socioDB);
            return "redirect:/socios/listadoAdmin";
        } else {
            return "redirect:/socios/nuevo/final";
        }

        // System.out.println("DTO recibido: " + dto); // ← Verás si los campos están
        // vacíos
    }

    // Paso 2: mostrar el formulario de membresía
    @GetMapping("/nuevo/final")
    public String mostrarFormularioPaso2(Model model) {
        // El objeto "socioRegistro" ya está en sesión gracias a @SessionAttributes
        // Si por alguna razón no está, lo agregamos (aunque no debería pasar)

        if (!model.containsAttribute("socioRegistro")) {
            return "redirect:/socios/nuevo";
        }

        List<Actividad> actividades = actividadService.listarActividades();

        model.addAttribute("actividades", actividades);

        model.addAttribute("fechaInicio", LocalDateTime.now());
        model.addAttribute("fechaVencimiento", (LocalDate.now()).plusMonths(1));
        return "socios/socios-form-2.html"; // ← nombre de tu segunda plantilla
    }

    // Paso 2
    @PostMapping("/guardar")
    public String finalizarFormulario(
            @Valid @ModelAttribute("socioRegistro") SocioRegistroDTO dto,
            BindingResult result,
            SessionStatus sessionStatus,
            RedirectAttributes redirectAttributes,
            Model model) {

        // -------------------------------------------------------------
        // 1) Obtener actividad y monto REAL
        // -------------------------------------------------------------
        Actividad actividad = actividadService.buscarPorId(dto.getActividad().getId());
        Integer cuota = actividad.getMonto();

        // 1) Ver si el socio YA EXISTE (por DNI)
        Socio socioExistente = null;

        if (dto.getDni() != null && !dto.getDni().isBlank()) {
            socioExistente = socioService.buscarPorDNI(dto.getDni());
        }

        // 2) SI EXISTE → SOLO ACTUALIZA CUOTA
        if (socioExistente != null) {

            // Calcular nuevo saldo pendiente acumulando el saldo anterior
            Integer pago = dto.getMonto() != null ? dto.getMonto() : 0;
            Integer saldoAnterior = socioExistente.getSaldoPendiente() != null ? socioExistente.getSaldoPendiente() : 0;
            int nuevoSaldo = saldoAnterior + (cuota - pago);

            socioExistente.setActividad(actividad);
            socioExistente.setSaldoPendiente(nuevoSaldo);
            // socioExistente.setFechaVencimiento(LocalDate.now().plusMonths(1));
            // socioExistente.setFechaAlta(dto.getFechaAlta());
            socioExistente.setFechaVencimiento(dto.getFechaVencimiento());
            socioExistente.setCuotaPaga(true);

            socioService.guardar(socioExistente);

            // 👉 REGISTRO EN CAJA
            MovimientoCaja movimiento = new MovimientoCaja();
            movimiento.setActividad(actividad.getNombre());
            movimiento.setSocioNombreCompleto(socioExistente.getNombreCompleto());
            movimiento.setDetalle("Pago de cuota");
            movimiento.setFormaPago(dto.getFormaPago()); // EFECTIVO / TRANSFERENCIA
            movimiento.setMonto(dto.getMonto());
            movimiento.setTipoMovimiento(TipoMovimiento.CUOTA);

            cajaService.guardar(movimiento);

            redirectAttributes.addFlashAttribute("mensaje", "Cuota abonada con éxito.");
            return "redirect:/home";
        }

        // -------------------------------------------------------------
        // 4) SOCIO NUEVO
        // -------------------------------------------------------------
        if (result.hasErrors()) {
            return "socios/socios-form.html";
        }

        Socio socio = new Socio();

        socio.setNombreCompleto(dto.getNombreCompleto());
        socio.setDni(dto.getDni());
        socio.setFechaNacimiento(dto.getFechaNacimiento());
        socio.setTelefono(dto.getTelefono());
        socio.setProfesion(dto.getProfesion());
        socio.setDireccion(dto.getDireccion());

        socio.setActividad(actividad);
        // socio.setFechaAlta(LocalDate.now());
        // socio.setFechaVencimiento(LocalDate.now().plusMonths(1));
        socio.setFechaAlta(dto.getFechaAlta());
        socio.setFechaVencimiento(dto.getFechaVencimiento());
        socio.setSaldoPendiente(cuota - dto.getMonto());
        socio.setCuotaPaga(true);

        socioService.guardar(socio);

        // 👉 REGISTRO EN CAJA (INSCRIPCIÓN)
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setActividad(actividad.getNombre());
        movimiento.setSocioNombreCompleto(socio.getNombreCompleto());
        movimiento.setDetalle("Pago de inscripción");
        movimiento.setFormaPago(dto.getFormaPago());
        movimiento.setMonto(dto.getMonto());
        movimiento.setTipoMovimiento(TipoMovimiento.INSCRIPCION);

        cajaService.guardar(movimiento);

        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("mensaje", "Socio registrado con éxito!");

        return "redirect:/home";
    }

    // Abonar cuota cuando esté vencida:
    @GetMapping("/abonarCuota/{id}")
    public String editarEstadoCuota(@PathVariable Long id, Model model) {

        Socio socio = socioService.buscarPorId(id);

        SocioRegistroDTO dto = new SocioRegistroDTO();
        dto.setDni(socio.getDni());

        // Mostrar la lista de actividades
        List<Actividad> actividades = actividadService.listarActividades();
        model.addAttribute("actividades", actividades);

        model.addAttribute("socioRegistro", dto);
        model.addAttribute("titulo", "Abonar Cuota");
        model.addAttribute("socio", socio);
        model.addAttribute("fechaInicio", LocalDateTime.now());
        model.addAttribute("fechaVencimiento", (LocalDate.now()).plusMonths(1));

        return "socios/socios-form-2.html";
    }

    // Eliminar Socio
    @GetMapping("/eliminar/{id}")
    @Secured({"ROLE_ADMIN"})
    public String eliminarSocio(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        Socio socio = socioService.buscarPorId(id);

        if (socio == null) {
            redirectAttributes.addFlashAttribute("error", "El socio no existe.");
            return "redirect:/socios/listadoAdmin";
        }

        socioService.eliminar(id);

        redirectAttributes.addFlashAttribute("mensaje", "Socio eliminado correctamente.");

        return "redirect:/socios/listadoAdmin";
    }

    // Editar Información del Socio...
    @GetMapping("/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {

        Socio socio = socioService.buscarPorId(id);

        model.addAttribute("socio", socio);
        model.addAttribute("modo", "editar");
        return "socios/socios-form";
    }

}
