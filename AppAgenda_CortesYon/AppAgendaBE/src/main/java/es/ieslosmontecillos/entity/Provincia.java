package es.ieslosmontecillos.entity;

import java.util.HashSet;
import java.util.Objects;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="PROVINCIA")
public class Provincia implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;
    @Basic
    @Column(name = "CODIGO", nullable = true, length = 2)
    private String codigo;
    @Basic
    @Column(name = "NOMBRE", nullable = false, length = 20)
    private String nombre;

    @OneToMany(mappedBy = "provincia")
    private Set<Persona> personas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provincia provincia = (Provincia) o;
        return Objects.equals(id, provincia.id) && Objects.equals(codigo, provincia.codigo) &&
                Objects.equals(nombre, provincia.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, nombre);
    }
}
