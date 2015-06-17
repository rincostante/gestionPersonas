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
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombre no puede quedar nulo")
    @Size(message = "El campo nombre debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String nombre;
    
     /**
     * Campo de texto que indica la clave de acceso del usuario
     */        
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "{entidades.fieldNotNullError}")
    @Size(message = "{endidades.stringSizeError}", min = 1, max = 50)
    private String clave; 
    
    private int idPersona;
    
    @ManyToOne /*(fetch=FetchType.LAZY)*/
    @JoinColumn(name="rol_id")
    private Rol rol;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @NotNull(message = "{entidades.objectNotNullError}") 
    private AdminEntidad adminentidad;
    
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public AdminEntidad getAdminentidad() {
        return adminentidad;
    }

    public void setAdminentidad(AdminEntidad adminentidad) {
        this.adminentidad = adminentidad;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.gestionPersonas.entidades.Usuario[ id=" + id + " ]";
    }
    
}
