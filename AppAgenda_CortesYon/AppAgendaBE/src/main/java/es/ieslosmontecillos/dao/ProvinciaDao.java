package es.ieslosmontecillos.dao;

import es.ieslosmontecillos.entity.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinciaDao extends JpaRepository<Provincia, Long>
{

}
