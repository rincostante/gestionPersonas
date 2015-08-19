/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author rodriguezn
 */
@Entity
public class Especialidad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombre no puede quedar nulo")
    @Size(message = "El campo nombre debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String nombre;
    
    @OneToMany(mappedBy="especialidad")
    private List<PerJuridica> perJuridicas;
    
    @OneToMany(mappedBy="especialidad")
    private List<PerFisica> perFisicas;

    public Especialidad() {
        perJuridicas = new ArrayList();
        perFisicas = new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<PerJuridica> getPerJuridicas() {
        return perJuridicas;
    }

    public void setPerJuridicas(List<PerJuridica> perJuridicas) {
        this.perJuridicas = perJuridicas;
    }

    public List<PerFisica> getPerFisicas() {
        return perFisicas;
    }

    public void setPerFisicas(List<PerFisica> perFisicas) {
        this.perFisicas = perFisicas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Especialidad)) {
            return false;
        }
        Especialidad other = (Especialidad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.personas.entidades.Especialidad[ id=" + id + " ]";
    }
    
}
