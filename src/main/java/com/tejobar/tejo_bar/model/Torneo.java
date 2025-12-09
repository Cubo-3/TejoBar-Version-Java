package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

/**
 * Torneo representa una reserva/inscripción para jugar.
 * Mapea a la tabla 'partido' que contiene fecha, hora, capitan y estado.
 */
@Data
@Entity
@Table(name = "partido")
public class Torneo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPartido")
    private Integer idTorneo;

    private LocalDate fecha;
    private String hora;
    private String capitan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cancha cancha;

    @Enumerated(EnumType.STRING)
    private EstadoTorneo estado;

    public enum EstadoTorneo {
        Pendiente, Confirmada, Cancelada
    }
}
