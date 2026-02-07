package com.analistas.gym.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analistas.gym.model.domain.Gasto;
import com.analistas.gym.model.domain.TipoGasto;
import com.analistas.gym.model.service.IGastoService;

@Controller
@RequestMapping("/gastos")
@Secured({"ROLE_ADMIN"})
public class GastoController {

    @Autowired
    private IGastoService gastoService;

    @GetMapping
    public String verGastos(Model model) {

        model.addAttribute("gastos", gastoService.listarTodos());
        model.addAttribute("gasto", new Gasto());
        model.addAttribute("tipos", TipoGasto.values());

        return "gastos/gastos";
    }

    @PostMapping("/guardar")
    public String guardarGasto(@ModelAttribute Gasto gasto) {
        gastoService.guardar(gasto);
        return "redirect:/gastos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        gastoService.eliminar(id);
        return "redirect:/gastos";
    }
}

