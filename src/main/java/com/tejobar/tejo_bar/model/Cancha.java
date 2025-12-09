package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cancha")
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCancha;

    @Enumerated(EnumType.STRING)
    private EstadoCancha estado;

    public enum EstadoCancha {
        ACTIVAS, MANTENIMIENTO, PENDIENTE, NO_ACTIVA
    }

    private String disponibilidad;
}
