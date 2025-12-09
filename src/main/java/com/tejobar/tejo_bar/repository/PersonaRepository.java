package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    Persona findByCorreo(String correo);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE persona SET nombre = COALESCE(:nombre, nombre), numero = COALESCE(:numero, numero), correo = COALESCE(:correo, correo) WHERE idPersona = :id", nativeQuery = true)
    int nativeUpdatePersona(@org.springframework.data.repository.query.Param("id") Integer id,
            @org.springframework.data.repository.query.Param("nombre") String nombre,
            @org.springframework.data.repository.query.Param("numero") String numero,
            @org.springframework.data.repository.query.Param("correo") String correo);

    @org.springframework.data.jpa.repository.Query(value = "SELECT p.idPersona, p.nombre, p.correo, p.contrasena, p.numero, p.rol, COALESCE(j.estado, 0) as estado, COALESCE(j.rut, '') as rut, CASE WHEN j.idPersona IS NOT NULL THEN 1 ELSE 0 END as clazz_ FROM persona p LEFT JOIN jugador j ON p.idPersona = j.idPersona WHERE LOWER(TRIM(p.correo)) = LOWER(TRIM(?1))", nativeQuery = true)
    Persona nativeFindByCorreo(@org.springframework.data.repository.query.Param("correo") String correo);

    @org.springframework.data.jpa.repository.Query(value = "SELECT p.idPersona, p.nombre, p.correo, p.contrasena, p.numero, p.rol, COALESCE(j.estado, 0) as estado, COALESCE(j.rut, '') as rut, CASE WHEN j.idPersona IS NOT NULL THEN 1 ELSE 0 END as clazz_ FROM persona p LEFT JOIN jugador j ON p.idPersona = j.idPersona WHERE p.correo IS NULL LIMIT 1", nativeQuery = true)
    Persona findFirstByCorreoIsNull();

    // Emergency repair for known lost emails (ID 109 and 120)
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE persona SET correo = 'juga@gmail.com' WHERE idPersona = 109 AND correo IS NULL", nativeQuery = true)
    void fixEmail109();

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE persona SET correo = 'kevin@gmail.com' WHERE idPersona = 120 AND correo IS NULL", nativeQuery = true)
    void fixEmail120();
}
