package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipo")
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipo;

    @Column(name = "nombreEquipo")
    private String nombreEquipo;

    @Column(name = "cupos_disponibles")
    private Integer cuposDisponibles;

    // El capitán se identifica por esCapitan=true en la tabla jugador_equipo
    // NO hay columna id_capitan en la tabla equipo
    // Para obtener el capitán, consultar JugadorEquipo donde esCapitan=true
    // La relación con jugadores es many-to-many a través de JugadorEquipo
    // NO hay relación directa @OneToMany con Jugador
}
