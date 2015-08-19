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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author rodriguezn
 */
@Entity
public class PerFisica implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="expediente_id")
    private Expediente expediente;
    
    @Column (nullable=false, length=50, unique=false)
    @NotNull (message = "El campo no puede ser nulo")
    @Size (message = "El campo debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String instrumentoSolicitante;
    
    @Column (nullable=false, length=50, unique=false)
    @NotNull (message = "El campo apellido no puede ser nulo")
    @Size (message = "El campo debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String apellido;
    
    @Column (nullable=false, length=50, unique=false)
    @NotNull (message = "El campo nombre no puede ser nulo")
    @Size (message = "El campo debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String nombre;
    
    private Long dni;
    
    private Long cuitCuil;
    
    @Column (nullable=false, length=50, unique=false)
    @NotNull (message = "El campo correo electronico no puede ser nulo")
    @Size (message = "El campo debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String correoElectronico;
    
    @Column (nullable=false, length=50, unique=false)
    @NotNull (message = "El campo celular no puede ser nulo")
    @Size (message = "El campo debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    private String cel;
    
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="domicilio_id")
    private Domicilio domicilio;
    
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

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public List<PerJuridica> getPerJuridica() {
        return perJuridica;
    }

    public void setPerJuridica(List<PerJuridica> perJuridica) {
        this.perJuridica = perJuridica;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
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

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
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
