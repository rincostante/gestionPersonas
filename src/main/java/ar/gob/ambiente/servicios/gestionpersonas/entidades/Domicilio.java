/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author rodriguezn
 */
@Entity
public class Domicilio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo calle no puede quedar nulo")
    @Size(message = "El campo calle debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String calle;
    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo numero no puede quedar nulo")
    @Size(message = "El campo numero debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String numero;
    
    @Column (nullable=false, length=50)
    @Size(message = "El campo piso debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String piso;
    
    @Column (nullable=false, length=50)
    @Size(message = "El campo departamento debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String dpto;
    
    private int idLocalidad;
    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo localidad no puede quedar nulo")
    @Size(message = "El campo localidad debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String localidad;
    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo provincia no puede quedar nulo")
    @Size(message = "El campo provincia debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String provincia;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getDpto() {
        return dpto;
    }

    public void setDpto(String dpto) {
        this.dpto = dpto;
    }

    public int getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(int idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
        if (!(object instanceof Domicilio)) {
            return false;
        }
        Domicilio other = (Domicilio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.personas.entidades.Domicilio[ id=" + id + " ]";
    }

    public Object getNombre() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
