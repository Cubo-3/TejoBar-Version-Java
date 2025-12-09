package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Apartado;
import com.tejobar.tejo_bar.model.Apartado.EstadoApartado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartadoRepository extends JpaRepository<Apartado, Integer> {
    List<Apartado> findByPersona_IdPersona(Integer idPersona);

    List<Apartado> findByProducto_IdProducto(Integer idProducto);

    List<Apartado> findByEstado(EstadoApartado estado);

    List<Apartado> findByPersona_IdPersonaAndEstado(Integer idPersona, EstadoApartado estado);
}
