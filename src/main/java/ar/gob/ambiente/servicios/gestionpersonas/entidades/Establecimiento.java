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
import javax.xml.bind.annotation.XmlTransient;

/**
*
* @author rodriguezn
*/
@Entity
public class Establecimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="perJuridica_id")
    private PerJuridica perJuridica;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="perFisica_id")
    private PerFisica perFisica;    
    
    /**
     * Campo de tipo Array que contiene el conjunto de los cambios de Razón social que pudiera haber tenido el Establecimiento
     */     
    @OneToMany(mappedBy="establecimiento")
    private List<ReasignaRazonSocial> razonesSocialesAnt;
    
    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="tipoEstablecimiento_id")
    private TipoEstablecimiento tipo;
    
    /**
     * Campo de tipo Array que contiene el conjunto de las Especialidades del Establecimiento
     */
    @ManyToMany
    @JoinTable(
            name = "establecimientosXEspecialidades",
            joinColumns = @JoinColumn(name = "establecimiento_fk"),
            inverseJoinColumns = @JoinColumn(name = "especialidad_fk")
    )
    private List<Especialidad> especialidades;    
    
    /**
     * Campo de tipo Array que contiene el conjunto de las Actividades del Establecimiento
     */
    @ManyToMany
    @JoinTable(
            name = "establecimientosXActividades",
            joinColumns = @JoinColumn(name = "establecimiento_fk"),
            inverseJoinColumns = @JoinColumn(name = "actividad_fk")
    )
    private List<Actividad> actividades;        
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="domicilio_id")
    private Domicilio domicilio;
    
    private String correoElectronico;
    private String telefono;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="estado_id")
    private Estado estado;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="adminentidad_id")
    private AdminEntidad admin;    

    public Establecimiento() {
        razonesSocialesAnt = new ArrayList();
        especialidades = new ArrayList();
        actividades = new ArrayList();
    }  

    public PerFisica getPerFisica() {
        return perFisica;
    }

    public void setPerFisica(PerFisica perFisica) {
        this.perFisica = perFisica;
    }

    @XmlTransient
    public List<ReasignaRazonSocial> getRazonesSocialesAnt() {
        // solo dejo los registros que tengan activa en false
        for(ReasignaRazonSocial rs : razonesSocialesAnt){
            if(rs.isActiva()){
                razonesSocialesAnt.remove(rs);
            }
        }
        return razonesSocialesAnt;
    }

    public void setRazonesSocialesAnt(List<ReasignaRazonSocial> razonesSocialesAnt) {
        this.razonesSocialesAnt = razonesSocialesAnt;
    }

    @XmlTransient
    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    @XmlTransient
    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
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

    @XmlTransient  
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
        return "ar.gob.ambiente.servicios.gestionPersonas.entidades.Establecimiento[ id=" + id + " ]";
    }
}
