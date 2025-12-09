package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * Partido representa un enfrentamiento entre dos equipos.
 * Mapea a la tabla 'torneo' que contiene equipo1 y equipo2.
 */
@Data
@Entity
@Table(name = "torneo")
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPartido")
    private Integer idPartido;

    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo1")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Equipo equipo1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo2")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Equipo equipo2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cancha cancha;
}
