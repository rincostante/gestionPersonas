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

/**
 *
 * @author rodriguezn
 */
@Entity
public class Expediente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     /**
     * Campo de tipo entero que indica el Número del expediente
     */
    @Column (nullable=false)
    @NotNull(message = "{entidades.fieldNotNullError}")
    private int numero;
    
    /**
     * Campo de tipo entero que indica el Año del expediente
     */
    @Column (nullable=false)
    @NotNull(message = "{entidades.fieldNotNullError}")
    private int anio;
    
    /**
     * Listado de Personas Físicas vinculadas a un Expediente
     */
    @OneToMany(mappedBy="expediente")
    private List<PerFisica> perFisicas;    
 
    /**
     * Listado de Establecimientos vinculados a un Expediente
     */
    @OneToMany(mappedBy="expediente")
    private List<Establecimiento> establecimientos;        
    
    public Expediente(){
        perFisicas = new ArrayList();
        establecimientos = new ArrayList();
    } 

    public List<PerFisica> getPerFisicas() {
        return perFisicas;
    }

    public void setPerFisicas(List<PerFisica> perFisicas) {
        this.perFisicas = perFisicas;
    }

    public List<Establecimiento> getEstablecimientos() {
        return establecimientos;
    }

    public void setEstablecimientos(List<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
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
        if (!(object instanceof Expediente)) {
            return false;
        }
        Expediente other = (Expediente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.gestionPersonas.entidades.Expediente[ id=" + id + " ]";
    }

    public Object getNombre() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
