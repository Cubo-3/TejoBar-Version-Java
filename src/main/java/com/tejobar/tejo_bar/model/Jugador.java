package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "jugador")
@PrimaryKeyJoinColumn(name = "idPersona")
public class Jugador extends Persona {
    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "rut")
    private String rut;

    // Relación con equipos a través de la tabla intermedia jugador_equipo
    // NO usar @ManyToOne directo ya que la relación es muchos-a-muchos
    // La relación real está en JugadorEquipo
}
