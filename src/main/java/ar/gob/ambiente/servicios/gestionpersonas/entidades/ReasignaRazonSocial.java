

package ar.gob.ambiente.servicios.gestionpersonas.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 * Esta entidad gestiona los cambios de razón social que se realicen a los Establecimientos.
 * Tanto sean Personas Jurídicas o Personas Físicas.
 * @author rincostante
 */
@Entity
public class ReasignaRazonSocial implements Serializable {
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
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="establecimiento_id")    
    private Establecimiento establecimiento;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fecha;
    
    private boolean activa;

    public PerJuridica getPerJuridica() {
        return perJuridica;
    }

    public void setPerJuridica(PerJuridica perJuridica) {
        this.perJuridica = perJuridica;
    }

    public PerFisica getPerFisica() {
        return perFisica;
    }

    public void setPerFisica(PerFisica perFisica) {
        this.perFisica = perFisica;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof ReasignaRazonSocial)) {
            return false;
        }
        ReasignaRazonSocial other = (ReasignaRazonSocial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.gestionpersonas.entidades.ReasignaRazonSocial[ id=" + id + " ]";
    }
}
