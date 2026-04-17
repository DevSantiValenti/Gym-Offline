package com.analistas.gym.model.service;

import java.time.LocalDate;
import java.util.List;

import com.analistas.gym.model.domain.Ingreso;
import com.analistas.gym.model.domain.Socio;

public interface IingresoService {

    void registrarIngreso(Socio socio);

    List<Ingreso> obtenerIngresosPorFecha(LocalDate fecha);
    
    void eliminarIngreso(Long id);
}
