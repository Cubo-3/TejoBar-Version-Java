package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Apartado;
import com.tejobar.tejo_bar.model.Apartado.EstadoApartado;
import com.tejobar.tejo_bar.repository.ApartadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartadoService {

    @Autowired
    private ApartadoRepository apartadoRepository;

    public List<Apartado> findAll() {
        return apartadoRepository.findAll();
    }

    public Optional<Apartado> findById(Integer id) {
        return apartadoRepository.findById(id);
    }

    public Apartado save(Apartado apartado) {
        return apartadoRepository.save(apartado);
    }

    public void deleteById(Integer id) {
        apartadoRepository.deleteById(id);
    }

    public List<Apartado> findByPersona(Integer idPersona) {
        return apartadoRepository.findByPersona_IdPersona(idPersona);
    }

    public List<Apartado> findByProducto(Integer idProducto) {
        return apartadoRepository.findByProducto_IdProducto(idProducto);
    }

    public List<Apartado> findPendientes() {
        return apartadoRepository.findByEstado(EstadoApartado.pendiente);
    }

    public List<Apartado> findComprados() {
        return apartadoRepository.findByEstado(EstadoApartado.comprado);
    }

    public void confirmarApartado(Integer id) {
        Apartado apartado = apartadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartado no encontrado"));
        apartado.setEstado(EstadoApartado.comprado);
        apartadoRepository.save(apartado);
    }
}


