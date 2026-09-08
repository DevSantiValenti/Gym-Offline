package com.analistas.gym.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Socio> listarSociosActivosParaTabla(String busqueda, Pageable pageable);

    long contarSociosActivos();

    void actualizarCuotasVencidas();

    void restaurarSocio(Long id);

    List<Socio> listarEliminados();

    List<Socio> listarEliminadosPorFecha(LocalDate desde, LocalDate hasta);

    public List<Socio> obtenerIngresosPorFecha(LocalDate fecha);

    public Socio buscarEliminadoPorDNI(String dni);

    public void eliminarFisico(Long id);
}
