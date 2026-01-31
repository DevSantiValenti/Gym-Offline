package com.analistas.gym.model.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.repository.ISocioRepository;

@Service
public class SocioServiceImpl implements ISocioService {

    @Autowired
    ISocioRepository socioRepository;

    @Override
    public List<Socio> listarSocios() {
        return (List<Socio>) socioRepository.findAll();
    }

    @Override
    public void guardar(Socio socio) {
        socioRepository.save(socio);
    }

    // @Override
    // public Socio buscarPorDNI(String dni) {
    // return socioRepository.findByDni(dni);
    // }

    @Transactional
    @Override
    public Optional<Socio> actualizarVecesIngresado(String dni) {
        Optional<Socio> socioOpt = socioRepository.findByDni(dni);
        if (socioOpt.isPresent()) {

            Socio socio = socioOpt.get();

            // Si la fecha de vencimiento ya pasó, marcar cuota como no pagada
            LocalDate fechaVto = socio.getFechaVencimiento();
            if (fechaVto != null && (fechaVto.isEqual(LocalDate.now()) ||
                    fechaVto.isBefore(LocalDate.now()))) {
                socio.setCuotaPaga(false);
            }

            // 2 Resetear frecuencia si corresponde (LUNES)
            resetearVecesIngresadoSiCorresponde(socio);

            // Actualizar datos en BD
            socio.setVecesIngresado(socio.getVecesIngresado() == null ? 1 : socio.getVecesIngresado() + 1);
            socio.setUltIngreso(LocalDateTime.now());
            socioRepository.save(socio);

            return Optional.of(socio);
        }
        return Optional.empty();
    }

    @Override
    public List<Socio> buscarTodos() {
        return (List<Socio>) socioRepository.findAll();
    }

    @Transactional
    @Override
    public List<Socio> listarSociosActualizados() {

        List<Socio> socios = (List<Socio>) socioRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Socio socio : socios) {
            LocalDate fechaVto = socio.getFechaVencimiento();

            if (fechaVto != null && !fechaVto.isAfter(hoy)) {
                socio.setCuotaPaga(false);
            }
        }

        socioRepository.saveAll(socios);
        return socios;
    }

    @Override
    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id).orElse(null);
    }

    @Override
    public Socio buscarPorDNI(String dni) {
        return socioRepository.findByDni(dni).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        socioRepository.deleteById(id);
    }

    private void resetearVecesIngresadoSiCorresponde(Socio socio) {

        if (socio.getUltIngreso() == null) {
            socio.setVecesIngresado(0);
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate ultimoIngreso = socio.getUltIngreso().toLocalDate();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int semanaActual = hoy.get(weekFields.weekOfWeekBasedYear());
        int semanaUltimoIngreso = ultimoIngreso.get(weekFields.weekOfWeekBasedYear());

        int anioActual = hoy.getYear();
        int anioUltimoIngreso = ultimoIngreso.getYear();

        // Si cambió la semana o el año → reset
        if (semanaActual != semanaUltimoIngreso || anioActual != anioUltimoIngreso) {
            socio.setVecesIngresado(0);
        }

    }
}

// Guardar el valor del último ingreso ANTES de modificar
// LocalDateTime ultimoIngresoAnterior = socio.getUltIngreso() != null ?
// socio.getUltIngreso()
// // Copia para el frontend
// : LocalDateTime.now();
// Socio copia = new Socio();
// copia.setId(socio.getId());
// copia.setNombreCompleto(socio.getNombreCompleto());
// copia.setDni(socio.getDni());
// copia.setActividad(socio.getActividad());
// copia.setSaldoPendiente(socio.getSaldoPendiente());
// copia.setCuotaPaga(socio.getCuotaPaga());
// copia.setFechaVencimiento(socio.getFechaVencimiento());
// copia.setVecesIngresado(socio.getVecesIngresado());
// // Mandamos al front la ult vez ingresada (no actual)
// copia.setUltIngreso(ultimoIngresoAnterior);
// return Optional.of(copia);