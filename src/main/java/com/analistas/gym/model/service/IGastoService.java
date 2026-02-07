package com.analistas.gym.model.service;

import java.util.List;

import com.analistas.gym.model.domain.Gasto;

public interface IGastoService {

    public void guardar(Gasto gasto);

    public List<Gasto> listarTodos();

    public void eliminar(Long id);

    public List<Integer> obtenerTotalesPorMes(int anio);

}
