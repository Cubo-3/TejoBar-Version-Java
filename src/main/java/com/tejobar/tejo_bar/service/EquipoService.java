package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Equipo;
import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.model.JugadorEquipo;
import com.tejobar.tejo_bar.model.JugadorEquipoId;
import com.tejobar.tejo_bar.repository.EquipoRepository;
import com.tejobar.tejo_bar.repository.JugadorRepository;
import com.tejobar.tejo_bar.repository.JugadorEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private JugadorEquipoRepository jugadorEquipoRepository;

    @Transactional(readOnly = true)
    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Jugador getCapitan(Equipo equipo) {
        List<JugadorEquipo> relaciones = jugadorEquipoRepository.findByIdEquipo(equipo.getIdEquipo());
        return relaciones.stream()
                .filter(JugadorEquipo::getEsCapitan)
                .map(JugadorEquipo::getJugador)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Equipo getEquipoByJugador(Jugador jugador) {
        List<JugadorEquipo> relaciones = jugadorEquipoRepository.findByIdJugador(jugador.getIdPersona());
        if (relaciones.isEmpty()) {
            return null;
        }
        return relaciones.get(0).getEquipo();
    }

    @Transactional(readOnly = true)
    public List<Jugador> getJugadores(Equipo equipo) {
        List<JugadorEquipo> relaciones = jugadorEquipoRepository.findByIdEquipo(equipo.getIdEquipo());
        return relaciones.stream()
                .map(JugadorEquipo::getJugador)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public Equipo createEquipo(String nombreEquipo, String correoCapitan) {
        Jugador capitan = jugadorRepository.findByCorreo(correoCapitan);
        if (capitan == null) {
            throw new RuntimeException("Capitán no encontrado con correo: " + correoCapitan);
        }

        // Verificar si el jugador ya pertenece a un equipo
        if (getEquipoByJugador(capitan) != null) {
            throw new RuntimeException("El jugador ya pertenece a un equipo.");
        }

        // Verificar si el jugador ya es capitán de otro equipo
        List<JugadorEquipo> relacionesCapitan = jugadorEquipoRepository.findByEsCapitanTrue();
        boolean yaEsCapitan = relacionesCapitan.stream()
                .anyMatch(rel -> rel.getIdJugador().equals(capitan.getIdPersona()));
        if (yaEsCapitan) {
            throw new RuntimeException("El jugador ya es capitán de otro equipo.");
        }

        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(nombreEquipo);
        equipo.setCuposDisponibles(5); // Default value

        equipo = equipoRepository.save(equipo);

        // Crear relación jugador-equipo como capitán
        JugadorEquipo jugadorEquipo = new JugadorEquipo();
        jugadorEquipo.setIdJugador(capitan.getIdPersona());
        jugadorEquipo.setIdEquipo(equipo.getIdEquipo());
        jugadorEquipo.setJugador(capitan);
        jugadorEquipo.setEquipo(equipo);
        jugadorEquipo.setEsCapitan(true);
        jugadorEquipoRepository.save(jugadorEquipo);

        return equipo;
    }

    @Transactional
    public void joinEquipo(Integer idEquipo, String correoJugador) {
        Equipo equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Jugador jugador = jugadorRepository.findByCorreo(correoJugador);
        if (jugador == null) {
            throw new RuntimeException("Jugador no encontrado");
        }

        // Verificar si el jugador ya pertenece a un equipo
        if (getEquipoByJugador(jugador) != null) {
            throw new RuntimeException("Ya perteneces a un equipo.");
        }

        // Verificar si ya existe la relación
        if (jugadorEquipoRepository.existsByIdJugadorAndIdEquipo(jugador.getIdPersona(), idEquipo)) {
            throw new RuntimeException("Ya perteneces a este equipo.");
        }

        if (equipo.getCuposDisponibles() <= 0) {
            throw new RuntimeException("No hay cupos disponibles en este equipo.");
        }

        equipo.setCuposDisponibles(equipo.getCuposDisponibles() - 1);
        equipoRepository.save(equipo);

        // Crear relación jugador-equipo
        JugadorEquipo jugadorEquipo = new JugadorEquipo();
        jugadorEquipo.setIdJugador(jugador.getIdPersona());
        jugadorEquipo.setIdEquipo(idEquipo);
        jugadorEquipo.setJugador(jugador);
        jugadorEquipo.setEquipo(equipo);
        jugadorEquipo.setEsCapitan(false);
        jugadorEquipoRepository.save(jugadorEquipo);
    }

    @Transactional
    public void leaveEquipo(String correoJugador) {
        Jugador jugador = jugadorRepository.findByCorreo(correoJugador);
        if (jugador == null) {
            throw new RuntimeException("Jugador no encontrado");
        }

        Equipo equipo = getEquipoByJugador(jugador);
        if (equipo == null) {
            throw new RuntimeException("No perteneces a ningún equipo.");
        }

        // Verificar si es capitán
        List<JugadorEquipo> relaciones = jugadorEquipoRepository.findByIdJugador(jugador.getIdPersona());
        boolean esCapitan = relaciones.stream()
                .anyMatch(rel -> rel.getIdEquipo().equals(equipo.getIdEquipo()) && rel.getEsCapitan());
        
        if (esCapitan) {
            throw new RuntimeException(
                    "El capitán no puede abandonar el equipo. Debe eliminar el equipo o transferir la capitanía.");
        }

        // Eliminar relación
        JugadorEquipoId id = new JugadorEquipoId(jugador.getIdPersona(), equipo.getIdEquipo());
        jugadorEquipoRepository.deleteById(id);

        equipo.setCuposDisponibles(equipo.getCuposDisponibles() + 1);
        equipoRepository.save(equipo);
    }

    @Transactional
    public void removeJugadorFromEquipo(Integer equipoId, Integer jugadorId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Verificar que el jugador existe
        if (!jugadorRepository.existsById(jugadorId)) {
            throw new RuntimeException("Jugador no encontrado");
        }

        // Verificar si el jugador pertenece a este equipo
        if (!jugadorEquipoRepository.existsByIdJugadorAndIdEquipo(jugadorId, equipoId)) {
            throw new RuntimeException("El jugador no pertenece a este equipo.");
        }

        // Verificar si es capitán
        JugadorEquipo relacion = jugadorEquipoRepository.findById(new JugadorEquipoId(jugadorId, equipoId))
                .orElse(null);
        if (relacion != null && relacion.getEsCapitan()) {
            throw new RuntimeException("No se puede eliminar al capitán del equipo.");
        }

        // Eliminar relación
        JugadorEquipoId id = new JugadorEquipoId(jugadorId, equipoId);
        jugadorEquipoRepository.deleteById(id);

        equipo.setCuposDisponibles(equipo.getCuposDisponibles() + 1);
        equipoRepository.save(equipo);
    }

    @Transactional
    public void deleteEquipo(Integer equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Eliminar todas las relaciones jugador-equipo
        List<JugadorEquipo> relaciones = jugadorEquipoRepository.findByIdEquipo(equipoId);
        jugadorEquipoRepository.deleteAll(relaciones);

        // Eliminar el equipo
        equipoRepository.delete(equipo);
    }
}
