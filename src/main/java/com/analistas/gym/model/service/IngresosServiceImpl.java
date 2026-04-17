package com.analistas.gym.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.gym.model.domain.Ingreso;
import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.repository.IingresoRepository;

@Service
public class IngresosServiceImpl implements IingresoService {

    @Autowired
    IingresoRepository ingresoRepository;

    @Override
    public void registrarIngreso(Socio socio) {

        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));

        Ingreso ingreso = new Ingreso();
        ingreso.setSocio(socio);
        ingreso.setFechaHora(ahora);

        ingresoRepository.save(ingreso);
    }

    @Override
    public List<Ingreso> obtenerIngresosPorFecha(LocalDate fecha) {
        return ingresoRepository.findByFecha(fecha);
    }

    @Override
    public void eliminarIngreso(Long id) {
        ingresoRepository.deleteById(id);
    }
}
