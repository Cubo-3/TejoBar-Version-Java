package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
    List<Cancha> findByEstado(Cancha.EstadoCancha estado);
}
