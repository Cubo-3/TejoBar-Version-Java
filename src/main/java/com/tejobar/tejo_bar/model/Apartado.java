package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "apartados")
public class Apartado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idApartado;

    @ManyToOne
    @JoinColumn(name = "idPersona", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    private Integer cantidad;

    @Column(name = "fechaApartado", insertable = false, updatable = false)
    private LocalDateTime fechaApartado;

    @Enumerated(EnumType.STRING)
    private EstadoApartado estado;

    public enum EstadoApartado {
        pendiente, comprado
    }
}
