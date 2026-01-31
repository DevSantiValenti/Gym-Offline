package com.analistas.gym.model.service;

import java.util.List;
import java.util.Optional;

import com.analistas.gym.model.domain.Socio;

public interface ISocioService  {

    public Socio buscarPorId(Long id);

    public List<Socio> listarSocios();

    public List<Socio> buscarTodos();

    public void guardar(Socio socio);

    public void eliminar(Long id);

    public Socio buscarPorDNI(String dni);

    public Optional<Socio> actualizarVecesIngresado(String dni);

    public List<Socio> listarSociosActualizados();

}
