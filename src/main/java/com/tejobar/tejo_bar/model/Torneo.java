package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "torneo")
public class Torneo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPartido") // Mantener nombre de columna de BD pero usar nombre correcto en código
    private Integer idTorneo;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "equipo1")
    private Equipo equipo1;

    @ManyToOne
    @JoinColumn(name = "equipo2")
    private Equipo equipo2;

    @ManyToOne
    @JoinColumn(name = "cancha")
    private Cancha cancha;
}
