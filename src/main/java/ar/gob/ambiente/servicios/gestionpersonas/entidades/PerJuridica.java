/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rodriguezn
 */

@Entity
public class PerJuridica implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable=false, length=250, unique=true)
    @NotNull(message = "El campo Razón Social no puede quedar nulo")
    @Size(message = "El campo Razón Social debe tener entre 1 y 250 caracteres", min = 1, max = 250)
    private String razonSocial;
    
    @Column (nullable=false)
    @NotNull(message = "El compo CUIT no debe quedar nulo")
    private long cuit;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tipoPersonaJuridica_id")
    private TipoPersonaJuridica tipoPersonaJuridica;
    
    /**
     * Campo de tipo Array que contiene el conjunto de los establecimientos que contiene esta Persona Jurídica
     */     
    @OneToMany(mappedBy="perJuridica")
    private List<Establecimiento> establecimientos;
    
    
    private Long idAplicacion; 
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="estado_id")
    private Estado estado;
   
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="adminentidad_id")
    private AdminEntidad admin; 

    public PerJuridica() {
        establecimientos = new ArrayList();
    }

    public Long getIdAplicacion() {
        return idAplicacion;
    }

    public void setIdAplicacion(Long idAplicacion) {
        this.idAplicacion = idAplicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public long getCuit() {
        return cuit;
    }

    public void setCuit(long cuit) {
        this.cuit = cuit;
    }

    public TipoPersonaJuridica getTipoPersonaJuridica() {
        return tipoPersonaJuridica;
    }

    public void setTipoPersonaJuridica(TipoPersonaJuridica tipoPersonaJuridica) {
        this.tipoPersonaJuridica = tipoPersonaJuridica;
    }

    @XmlTransient
    public List<Establecimiento> getEstablecimientos() {
        return establecimientos;
    }

    public void setEstablecimientos(List<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
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
        if (!(object instanceof PerJuridica)) {
            return false;
        }
        PerJuridica other = (PerJuridica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.gestionPersonas.entidades.PerJuridica[ id=" + id + " ]";
    }
}

