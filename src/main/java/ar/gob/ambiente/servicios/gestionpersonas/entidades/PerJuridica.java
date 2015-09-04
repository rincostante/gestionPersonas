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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
    
    private String razonSocial;
    private String cuit;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="tipoPersonaJuridica_id")
    private TipoPersonaJuridica tipoPersonaJuridica;
    
    private String correoElectronico;
    private String telefono;
    
    @ManyToMany
    @JoinTable(
          name = "perJuridicasXperFisicas", 
          joinColumns = @JoinColumn(name = "perJuridica_id"),
          inverseJoinColumns = @JoinColumn(name = "perFisica_fk")
    ) 
    private List<PerFisica> representantes;
    
    @OneToMany(mappedBy="perJuridica")
    private List<Establecimiento> establecimientos;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="expediente_id")
    private Expediente expediente;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="estado_id")
    private Estado estado;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="especialidad_id")
    private Especialidad especialidad;
   
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="adminentidad_id")
    private AdminEntidad admin; 

    public PerJuridica() {
        representantes = new ArrayList();
        establecimientos = new ArrayList();
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

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public TipoPersonaJuridica getTipoPersonaJuridica() {
        return tipoPersonaJuridica;
    }

    public void setTipoPersonaJuridica(TipoPersonaJuridica tipoPersonaJuridica) {
        this.tipoPersonaJuridica = tipoPersonaJuridica;
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

    public List<PerFisica> getRepresentantes() {
        return representantes;
    }

    public void setRepresentantes(List<PerFisica> representantes) {
        this.representantes = representantes;
    }

    public List<Establecimiento> getEstablecimientos() {
        return establecimientos;
    }

    public void setEstablecimientos(List<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
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

    public Domicilio getDomicilio() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object getNombre() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getActividad() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Establecimiento getEstablecimiento() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
