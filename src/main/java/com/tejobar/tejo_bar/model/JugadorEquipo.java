package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "jugador_equipo")
@IdClass(JugadorEquipoId.class)
public class JugadorEquipo implements Serializable {
    
    @Id
    @Column(name = "idJugador")
    private Integer idJugador;
    
    @Id
    @Column(name = "idEquipo")
    private Integer idEquipo;
    
    @ManyToOne
    @JoinColumn(name = "idJugador", insertable = false, updatable = false)
    private Jugador jugador;
    
    @ManyToOne
    @JoinColumn(name = "idEquipo", insertable = false, updatable = false)
    private Equipo equipo;
    
    @Column(name = "esCapitan")
    private Boolean esCapitan = false;
}

