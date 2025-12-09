package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Compra;
import com.tejobar.tejo_bar.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Optional<Compra> findById(Integer id) {
        return compraRepository.findById(id);
    }

    public Compra save(Compra compra) {
        if (compra.getFecha() == null) {
            compra.setFecha(LocalDate.now());
        }
        return compraRepository.save(compra);
    }

    public void deleteById(Integer id) {
        compraRepository.deleteById(id);
    }

    public List<Compra> findByJugador(Integer idJugador) {
        return compraRepository.findByJugador_IdPersona(idJugador);
    }

    public List<Compra> findByFecha(LocalDate fecha) {
        return compraRepository.findByFecha(fecha);
    }
}


