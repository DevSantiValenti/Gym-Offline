package com.analistas.gym.model.service;

import java.util.List;


import com.analistas.gym.model.domain.Actividad;

public interface IActividadService {
    
    public List<Actividad> listarActividades();

    public void guardar(Actividad actividad);

    public Actividad buscarPorId(Long id);

    public void eliminar(Long id);
}
