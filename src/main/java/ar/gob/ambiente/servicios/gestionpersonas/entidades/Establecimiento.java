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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author rodriguezn
 */
@Entity
public class Establecimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne
    private PerJuridica perJuridica;
    
    @ManyToOne /*(fetch=FetchType.LAZY)*/
    @JoinColumn(name="tipoEstablecimiento_id")
    private TipoEstablecimiento tipo;
  
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="actividad_id")
    private Actividad actividad;
    
    @OneToOne
    private Domicilio domicilio;
    
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo correo electronico no puede quedar nulo")
    @Size(message = "El campo correo electronico debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String correoElectronico;
    
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo telefono no puede quedar nulo")
    @Size(message = "El campo telefono debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String telefono;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="estado_id")
    private Estado estado;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="adminentidad_id")
    private AdminEntidad admin;    

    public Establecimiento() {
    }  
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerJuridica getPerJuridica() {
        return perJuridica;
    }

    public void setPerJuridica(PerJuridica perJuridica) {
        this.perJuridica = perJuridica;
    }

    public TipoEstablecimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEstablecimiento tipo) {
        this.tipo = tipo;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public AdminEntidad getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntidad admin) {
        this.admin = admin;
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
        if (!(object instanceof Establecimiento)) {
            return false;
        }
        Establecimiento other = (Establecimiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.personas.entidades.Establecimiento[ id=" + id + " ]";
    }
    
}
