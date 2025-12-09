package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Torneo;
import com.tejobar.tejo_bar.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Torneo> findByEquipo1(Integer idEquipo) {
        return torneoRepository.findByEquipo1_IdEquipo(idEquipo);
    }

    public List<Torneo> findByEquipo2(Integer idEquipo) {
        return torneoRepository.findByEquipo2_IdEquipo(idEquipo);
    }

    public List<Torneo> findByCancha(Integer idCancha) {
        return torneoRepository.findByCancha_IdCancha(idCancha);
    }

    public List<Torneo> findByFecha(LocalDateTime fecha) {
        return torneoRepository.findByFecha(fecha);
    }
}


