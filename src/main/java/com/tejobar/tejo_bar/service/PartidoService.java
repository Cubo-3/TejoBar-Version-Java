package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Partido;
import com.tejobar.tejo_bar.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

    public List<Partido> findAll() {
        return partidoRepository.findAll();
    }

    public Optional<Partido> findById(Integer id) {
        return partidoRepository.findById(id);
    }

    public Partido save(Partido partido) {
        return partidoRepository.save(partido);
    }

    public void deleteById(Integer id) {
        partidoRepository.deleteById(id);
    }

    public List<Partido> findByFecha(LocalDateTime fecha) {
        return partidoRepository.findByFecha(fecha);
    }

    public List<Partido> findByEquipo(Integer idEquipo) {
        List<Partido> partidos1 = partidoRepository.findByEquipo1_IdEquipo(idEquipo);
        List<Partido> partidos2 = partidoRepository.findByEquipo2_IdEquipo(idEquipo);
        partidos1.addAll(partidos2);
        return partidos1;
    }

    public List<Partido> findByCancha(Integer idCancha) {
        return partidoRepository.findByCancha_IdCancha(idCancha);
    }
}


