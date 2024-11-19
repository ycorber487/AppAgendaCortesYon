package es.ieslosmontecillos.service;

import es.ieslosmontecillos.entity.Persona;


import java.util.List;

public interface PersonaService
{
    List<Persona> findAll();
    Persona save(Persona persona);
    Persona findById(Long id);
    void delete(Persona persona);
}