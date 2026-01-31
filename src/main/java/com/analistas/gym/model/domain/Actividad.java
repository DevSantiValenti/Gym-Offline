package com.analistas.gym.model.domain;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString; // Importar la anotación ToString de Lombok

@Data
@Entity
@ToString(exclude = "socios") // 🔥 SOLUCIÓN: Excluir la colección LAZY del toString()
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer monto;

    @OneToMany(mappedBy = "actividad")
    @JsonIgnore
    private List<Socio> socios;
}