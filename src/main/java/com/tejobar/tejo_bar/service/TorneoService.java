package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Torneo;
import com.tejobar.tejo_bar.model.Torneo.EstadoTorneo;
import com.tejobar.tejo_bar.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    public Optional<Torneo> findById(Integer id) {
        return torneoRepository.findById(id);
    }

    public Torneo save(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    public void deleteById(Integer id) {
        torneoRepository.deleteById(id);
    }

    public List<Torneo> findByEstado(EstadoTorneo estado) {
        return torneoRepository.findByEstado(estado);
    }

    public List<Torneo> findByFecha(LocalDate fecha) {
        return torneoRepository.findByFecha(fecha);
    }

    public List<Torneo> findByCancha(Integer idCancha) {
        return torneoRepository.findByCancha_IdCancha(idCancha);
    }

    public void confirmarTorneo(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        torneo.setEstado(EstadoTorneo.Confirmada);
        torneoRepository.save(torneo);
    }

    public void cancelarTorneo(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        torneo.setEstado(EstadoTorneo.Cancelada);
        torneoRepository.save(torneo);
    }
}


