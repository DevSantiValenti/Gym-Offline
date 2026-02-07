package com.analistas.gym.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.gym.model.domain.Gasto;
import com.analistas.gym.model.repository.IGastoRepository;

@Service
public class GastoServiceImpl implements IGastoService {

    @Autowired
    private IGastoRepository gastoRepository;

    @Override
    public void guardar(Gasto gasto) {
        gastoRepository.save(gasto);
    }

    @Override
    public List<Gasto> listarTodos() {
        return gastoRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        gastoRepository.deleteById(id);
    }

    @Override
    public List<Integer> obtenerTotalesPorMes(int anio) {

        List<Integer> totales = new ArrayList<>(Collections.nCopies(12, 0));

        for (Object[] fila : gastoRepository.obtenerTotalesPorMes(anio)) {
            int mes = ((Integer) fila[0]) - 1;
            int total = ((Long) fila[1]).intValue();
            totales.set(mes, total);
        }

        return totales;
    }

}
