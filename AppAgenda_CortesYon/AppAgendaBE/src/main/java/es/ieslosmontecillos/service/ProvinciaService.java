package es.ieslosmontecillos.service;

import es.ieslosmontecillos.entity.Provincia;
import java.util.List;
public interface ProvinciaService
{
    List<Provincia> findAll();
    Provincia save(Provincia provincia);
    Provincia findById(Long id);
    void delete(Provincia provincia);
}