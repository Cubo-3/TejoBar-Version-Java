package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Partido;
import com.tejobar.tejo_bar.model.Partido.EstadoPartido;
import com.tejobar.tejo_bar.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<Partido> findByEstado(EstadoPartido estado) {
        return partidoRepository.findByEstado(estado);
    }

    public List<Partido> findByFecha(LocalDate fecha) {
        return partidoRepository.findByFecha(fecha);
    }

    public List<Partido> findByCancha(Integer idCancha) {
        return partidoRepository.findByCancha(idCancha);
    }

    public void confirmarPartido(Integer id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        partido.setEstado(EstadoPartido.Confirmada);
        partidoRepository.save(partido);
    }

    public void cancelarPartido(Integer id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        partido.setEstado(EstadoPartido.Cancelada);
        partidoRepository.save(partido);
    }
}


