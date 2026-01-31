package com.analistas.gym.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.analistas.gym.model.domain.Socio;
// import java.util.List;
import java.util.Optional;


public interface ISocioRepository extends CrudRepository<Socio, Long> {

    // List<Socio> findByDni(String dni);

    

    Optional<Socio> findByDni(String dni);
    
}
