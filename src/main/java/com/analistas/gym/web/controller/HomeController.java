package com.analistas.gym.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.service.ISocioService;
import com.analistas.gym.model.service.IingresoService;

@Controller
public class HomeController {

    @Autowired
    ISocioService socioService;

    @Autowired
    IingresoService ingresoService;

    @GetMapping({ "/", "/home" })
    public String getMethodName() {
        return "index";
    }

    @GetMapping("/api/socios/dni")
    @ResponseBody
    public ResponseEntity<Socio> buscarSocioPorDni(@RequestParam String dni) {

        Optional<Socio> socioOpt = socioService.actualizarVecesIngresado(dni.trim());
        // socio.set
        if (socioOpt.isPresent()) {

            Socio socio = socioOpt.get();

            // 🔥 REGISTRAR INGRESO
            ingresoService.registrarIngreso(socio);

            return ResponseEntity.ok(socio);
        }
        return socioOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

}
