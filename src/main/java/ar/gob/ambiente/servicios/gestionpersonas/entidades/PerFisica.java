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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author rodriguezn
 */
@Entity
public class PerFisica implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="expediente_id")
    private List<Expediente> expedientes;
    
    private String instrumentoSolicitante;
    private String apellido;
    private String nombre;
    private Long dni;
    private Long cuitCuil;
    private String correoElectronico;
    private String cel;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="domicilio_id")
    private List<Domicilio> domicilios;
    
    @ManyToMany(mappedBy="representantes")
    private List<PerJuridica> perJuridica;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="perfil_id")
    private Perfil perfil;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="actividad_id")
    private Actividad actividad;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="estado_id")
    private Estado estado;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="especialidad_id")
    private Especialidad especialidad;
   
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="adminentidad_id")
    private AdminEntidad admin; 
   
   /**
    * Constructor
    */
    public PerFisica() {
        perJuridica = new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Expediente> getExpedientes() {
        return expedientes;
    }

    public void setExpedientes(List<Expediente> expedientes) {
        this.expedientes = expedientes;
    }

    public String getInstrumentoSolicitante() {
        return instrumentoSolicitante;
    }

    public void setInstrumentoSolicitante(String instrumentoSolicitante) {
        this.instrumentoSolicitante = instrumentoSolicitante;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public Long getCuitCuil() {
        return cuitCuil;
    }

    public void setCuitCuil(Long cuitCuil) {
        this.cuitCuil = cuitCuil;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }

    public List<Domicilio> getDomicilios() {
        return domicilios;
    }

    public void setDomicilios(List<Domicilio> domicilios) {
        this.domicilios = domicilios;
    }

    public List<PerJuridica> getPerJuridicas() {
        return perJuridica;
    }

    public void setPerJuridicas(List<PerJuridica> perJuridica) {
        this.perJuridica = perJuridica;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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
        if (!(object instanceof PerFisica)) {
            return false;
        }
        PerFisica other = (PerFisica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.personas.entidades.PerFisica[ id=" + id + " ]";
    }
    
}
