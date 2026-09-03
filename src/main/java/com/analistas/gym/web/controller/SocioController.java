package com.analistas.gym.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.analistas.gym.model.service.WhatsAppService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/socios")
@SessionAttributes("socioRegistro")
public class SocioController {

    private static final ZoneId ZONA_BUENOS_AIRES = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final String TOKEN_PAGO_ATTR = "tokenPagoSocio";
    private static final String TOKEN_PAGO_CREADO_ATTR = "tokenPagoSocioCreado";
    private static final long MINUTOS_TOKEN_PAGO = 20;

    // @Autowired
    // ReciboPdfService reciboPdfService;

    @Autowired
    WhatsAppService whatsappService;

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
    public String mostrarFormularioPaso2(Model model, HttpSession session) {
        // El objeto "socioRegistro" ya está en sesión gracias a @SessionAttributes
        // Si por alguna razón no está, lo agregamos (aunque no debería pasar)

        if (!model.containsAttribute("socioRegistro")) {
            return "redirect:/socios/nuevo";
        }

        List<Actividad> actividades = actividadService.listarActividades();

        model.addAttribute("actividades", actividades);

        model.addAttribute("fechaInicio", LocalDateTime.now(ZONA_BUENOS_AIRES));
        model.addAttribute("fechaVencimiento", LocalDate.now(ZONA_BUENOS_AIRES).plusMonths(1));
        prepararTokenPago(model, session);
        return "socios/socios-form-2.html"; // ← nombre de tu segunda plantilla
    }

    // Paso 2
    @PostMapping("/guardar")
    public String finalizarFormulario(
            @Valid @ModelAttribute("socioRegistro") SocioRegistroDTO dto,
            BindingResult result,
            SessionStatus sessionStatus,
            RedirectAttributes redirectAttributes,
            Model model,
            @RequestParam("tokenPago") String tokenPago,
            HttpSession session) {

        if (!consumirTokenPagoValido(tokenPago, session)) {
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("error",
                    "El formulario de pago estaba vencido o ya fue usado. Volvé a abrirlo antes de registrar la cuota.");
            return "redirect:/home";
        }

        // -------------------------------------------------------------
        // 1) Obtener actividad y monto REAL
        // -------------------------------------------------------------
        Actividad actividad = actividadService.buscarPorId(dto.getActividad().getId());
        Integer cuota = actividad.getMonto();

        // 1) Ver si el socio YA EXISTE (por DNI)
        // Socio socioExistente = null;

        // if (dto.getDni() != null && !dto.getDni().isBlank()) {
        // socioExistente = socioService.buscarPorDNI(dto.getDni());
        // }
        Socio socioActivo = null;
        Socio socioEliminado = null;

        if (dto.getDni() != null && !dto.getDni().isBlank()) {
            socioActivo = socioService.buscarPorDNI(dto.getDni()); // ahora solo trae activos

            // NUEVO: buscar también eliminados
            socioEliminado = socioService.buscarEliminadoPorDNI(dto.getDni());
        }

        // 2) SI EXISTE → SOLO ACTUALIZA CUOTA
        // 1. Si existe ACTIVO → actualizar
        if (socioActivo != null) {

            // Calcular nuevo saldo pendiente acumulando el saldo anterior
            Integer pago = dto.getMonto() != null ? dto.getMonto() : 0;
            Integer saldoAnterior = socioActivo.getSaldoPendiente() != null ? socioActivo.getSaldoPendiente() : 0;
            int nuevoSaldo = saldoAnterior + (cuota - pago);

            socioActivo.setActividad(actividad);
            socioActivo.setSaldoPendiente(nuevoSaldo);
            // socioExistente.setFechaVencimiento(LocalDate.now().plusMonths(1));
            // socioExistente.setFechaAlta(dto.getFechaAlta());
            socioActivo.setFechaVencimiento(dto.getFechaVencimiento());
            socioActivo.setCuotaPaga(true);

            socioService.guardar(socioActivo);

            // 👉 REGISTRO EN CAJA
            MovimientoCaja movimiento = new MovimientoCaja();
            movimiento.setActividad(actividad.getNombre());
            movimiento.setSocioNombreCompleto(socioActivo.getNombreCompleto());
            movimiento.setSocioId(socioActivo.getId());
            movimiento.setDetalle("Pago de cuota");
            movimiento.setFormaPago(dto.getFormaPago()); // EFECTIVO / TRANSFERENCIA
            movimiento.setMonto(dto.getMonto());
            movimiento.setTipoMovimiento(TipoMovimiento.CUOTA);

            cajaService.guardar(movimiento);

            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("mensaje", "Cuota abonada con éxito.");

            // Justo antes del return final, generamos la URL
            // byte[] pdf = reciboPdfService.generarReciboPdf(socioExistente,
            // dto.getMonto());

            // try {
            // whatsappService.enviarReciboPdf(
            // socioExistente,
            // pdf,
            // "recibo-captain-gym.pdf");
            // } catch (Exception e) {
            // System.out.println("⚠ Error enviando WhatsApp: " + e.getMessage());
            // }

            return "redirect:/home";
        }

        // 2. Si existe ELIMINADO → bloquear
        if (socioEliminado != null) {
            redirectAttributes.addFlashAttribute("error",
                    "El socio existe pero está eliminado. Debes restaurarlo.");
            return "redirect:/socios/listadoAdmin";
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
        socio.setTelefono("549" + dto.getTelefono());
        socio.setProfesion(dto.getProfesion());
        socio.setDireccion(dto.getDireccion());

        socio.setActividad(actividad);
        // socio.setFechaAlta(LocalDate.now());
        // socio.setFechaVencimiento(LocalDate.now().plusMonths(1));
        socio.setFechaAlta(dto.getFechaAlta());
        socio.setFechaVencimiento(dto.getFechaVencimiento());
        socio.setSaldoPendiente(cuota - dto.getMonto());
        socio.setCuotaPaga(true);

        // Justo antes del return final, generamos la URL
        // byte[] pdf = reciboPdfService.generarReciboPdf(socio, dto.getMonto());

        // whatsappService.enviarReciboPdf(
        // socio,
        // pdf,
        // "recibo-captain-gym.pdf");

        // socioService.guardar(socio);

        socioService.guardar(socio);

        // try {
        // whatsappService.enviarReciboPdf(
        // socio,
        // pdf,
        // "recibo-captain-gym.pdf");
        // } catch (Exception e) {
        // System.out.println("⚠ Error enviando WhatsApp: " + e.getMessage());
        // }

        // 👉 REGISTRO EN CAJA (INSCRIPCIÓN)
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setActividad(actividad.getNombre());
        movimiento.setSocioNombreCompleto(socio.getNombreCompleto());
        movimiento.setSocioId(socio.getId());
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
    public String editarEstadoCuota(@PathVariable Long id, Model model, HttpSession session) {

        Socio socio = socioService.buscarPorId(id);

        SocioRegistroDTO dto = new SocioRegistroDTO();
        dto.setDni(socio.getDni());

        // Mostrar la lista de actividades
        List<Actividad> actividades = actividadService.listarActividades();
        model.addAttribute("actividades", actividades);

        model.addAttribute("socioRegistro", dto);
        model.addAttribute("titulo", "Abonar Cuota");
        model.addAttribute("socio", socio);
        model.addAttribute("fechaInicio", LocalDateTime.now(ZONA_BUENOS_AIRES));
        model.addAttribute("fechaVencimiento", LocalDate.now(ZONA_BUENOS_AIRES).plusMonths(1));
        prepararTokenPago(model, session);

        return "socios/socios-form-2.html";
    }

    @GetMapping("/editarCuota/{id}")
    public String editarCuota(@PathVariable Long id, Model model) {

        Socio socio = socioService.buscarPorId(id);

        if (socio == null) {
            return "redirect:/socios/listadoAdmin";
        }

        SocioRegistroDTO dto = new SocioRegistroDTO();
        dto.setDni(socio.getDni());
        dto.setFechaVencimiento(socio.getFechaVencimiento());

        model.addAttribute("socioRegistro", dto);
        model.addAttribute("socio", socio);
        model.addAttribute("modo", "editarCuota");
        model.addAttribute("titulo", "Editar Cuota");
        model.addAttribute("fechaInicio", calcularInicioCuota(socio.getFechaVencimiento()));
        model.addAttribute("fechaVencimiento", socio.getFechaVencimiento());

        return "socios/socios-form-2.html";
    }

    @PostMapping("/editarCuota/{id}")
    public String guardarEdicionCuota(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            RedirectAttributes redirectAttributes) {

        Socio socio = socioService.buscarPorId(id);

        if (socio == null) {
            redirectAttributes.addFlashAttribute("error", "El socio no existe.");
            return "redirect:/socios/listadoAdmin";
        }

        socio.setFechaVencimiento(fechaVencimiento);
        socioService.guardar(socio);

        redirectAttributes.addFlashAttribute("mensaje", "Fechas de cuota actualizadas correctamente.");
        return "redirect:/socios/listadoAdmin";
    }

    private LocalDate calcularInicioCuota(LocalDate fechaVencimiento) {
        if (fechaVencimiento == null) {
            return LocalDate.now(ZONA_BUENOS_AIRES).minusMonths(1);
        }

        return fechaVencimiento.minusMonths(1);
    }

    private void prepararTokenPago(Model model, HttpSession session) {
        String token = UUID.randomUUID().toString();
        session.setAttribute(TOKEN_PAGO_ATTR, token);
        session.setAttribute(TOKEN_PAGO_CREADO_ATTR, LocalDateTime.now(ZONA_BUENOS_AIRES));
        model.addAttribute("tokenPago", token);
    }

    private boolean consumirTokenPagoValido(String tokenPago, HttpSession session) {
        Object tokenGuardado = session.getAttribute(TOKEN_PAGO_ATTR);
        Object creado = session.getAttribute(TOKEN_PAGO_CREADO_ATTR);

        session.removeAttribute(TOKEN_PAGO_ATTR);
        session.removeAttribute(TOKEN_PAGO_CREADO_ATTR);

        if (!(tokenGuardado instanceof String tokenEsperado)
                || !(creado instanceof LocalDateTime fechaCreacion)
                || tokenPago == null
                || !tokenEsperado.equals(tokenPago)) {
            return false;
        }

        return fechaCreacion.plusMinutes(MINUTOS_TOKEN_PAGO).isAfter(LocalDateTime.now(ZONA_BUENOS_AIRES));
    }

    // Eliminar Socio
    @GetMapping("/eliminar/{id}")
    @Secured({ "ROLE_ADMIN" })
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

    @GetMapping("/eliminados")
    @Secured({ "ROLE_ADMIN" })
    public String sociosEliminados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        List<Socio> socios;

        if (desde != null && hasta != null) {
            socios = socioService.listarEliminadosPorFecha(desde, hasta);
        } else {
            socios = socioService.listarEliminados();
        }

        model.addAttribute("titulo", "Socios Eliminados");
        model.addAttribute("socios", socios);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);

        return "socios/socios-eliminados";
    }

    @GetMapping("/restaurar/{id}")
    @Secured({ "ROLE_ADMIN" })
    public String restaurarSocio(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        socioService.restaurarSocio(id);

        redirectAttributes.addFlashAttribute("mensaje",
                "Socio restaurado correctamente.");

        return "redirect:/socios/eliminados";
    }

    // Editar Información del Socio...
    @GetMapping("/editar/{id}")
    public String editarSocio(@PathVariable Long id, Model model) {

        Socio socio = socioService.buscarPorId(id);

        model.addAttribute("socio", socio);
        model.addAttribute("modo", "editar");
        return "socios/socios-form";
    }

    @GetMapping("/eliminarFisico/{id}")
    @Secured({ "ROLE_ADMIN" })
    public String eliminarFisico(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            socioService.eliminarFisico(id);
            redirectAttributes.addFlashAttribute("mensaje", "Socio eliminado definitivamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/socios/eliminados";
    }

    @GetMapping("/api/validar-dni")
    @ResponseBody
    public ResponseEntity<?> validarDni(@RequestParam String dni) {

        Socio activo = socioService.buscarPorDNI(dni);
        Socio eliminado = socioService.buscarEliminadoPorDNI(dni);

        if (activo != null) {
            return ResponseEntity.ok("EXISTE_ACTIVO");
        }

        if (eliminado != null) {
            return ResponseEntity.ok("EXISTE_ELIMINADO");
        }

        return ResponseEntity.ok("NO_EXISTE");
    }
}
