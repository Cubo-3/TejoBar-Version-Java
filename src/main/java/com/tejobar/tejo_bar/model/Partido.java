package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "partido")
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPartido;

    private LocalDate fecha;
    private String hora;
    private String capitan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cancha cancha;

    @Enumerated(EnumType.STRING)
    private EstadoPartido estado;

    public enum EstadoPartido {
        Pendiente, Confirmada, Cancelada
    }
}
