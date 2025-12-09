package com.tejobar.tejo_bar.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompra;

    private LocalDate fecha;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "idJugador", referencedColumnName = "idPersona")
    private Jugador jugador;
}
