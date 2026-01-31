package com.analistas.gym.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.gym.model.domain.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long>{

}
