package com.analistas.gym.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.gym.model.domain.Actividad;

public interface IActividadRepository extends CrudRepository<Actividad, Long> {

}
