package com.tejobar.tejo_bar.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
public class JugadorEquipoId implements Serializable {
    private Integer idJugador;
    private Integer idEquipo;

    public JugadorEquipoId() {}

    public JugadorEquipoId(Integer idJugador, Integer idEquipo) {
        this.idJugador = idJugador;
        this.idEquipo = idEquipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JugadorEquipoId that = (JugadorEquipoId) o;
        return Objects.equals(idJugador, that.idJugador) &&
               Objects.equals(idEquipo, that.idEquipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idJugador, idEquipo);
    }
}


