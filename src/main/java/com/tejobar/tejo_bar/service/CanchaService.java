package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Cancha;
import com.tejobar.tejo_bar.repository.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaService {

    @Autowired
    private CanchaRepository canchaRepository;

    public List<Cancha> findAll() {
        return canchaRepository.findAll();
    }

    public Optional<Cancha> findById(Integer id) {
        return canchaRepository.findById(id);
    }

    public Cancha save(Cancha cancha) {
        return canchaRepository.save(cancha);
    }

    public void deleteById(Integer id) {
        canchaRepository.deleteById(id);
    }

    public List<Cancha> findDisponibles() {
        return canchaRepository.findByEstado(Cancha.EstadoCancha.ACTIVAS);
    }

    public List<Cancha> findOcupadas() {
        // Asumiendo que ocupadas se refiere a mantenimiento o no activas para
        // simplificar,
        // o si se usa para filtrar por estado especifico.
        // Por ahora retornamos MANTENIMIENTO para mantener consistencia de tipo
        // retorno.
        return canchaRepository.findByEstado(Cancha.EstadoCancha.MANTENIMIENTO);
    }

    public void cambiarEstado(Integer id, String nuevoEstadoStr) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));

        try {
            Cancha.EstadoCancha nuevoEstado = Cancha.EstadoCancha.valueOf(nuevoEstadoStr);
            cancha.setEstado(nuevoEstado);
            canchaRepository.save(cancha);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + nuevoEstadoStr);
        }
    }
}
