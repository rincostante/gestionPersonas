/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
*
* @author rodriguezn
*/
@Stateless
public class EstablecimientoFacade extends AbstractFacade<Establecimiento> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstablecimientoFacade() {
        super(Establecimiento.class);
    }   
    
    /**
     * Método que retorna un Establecimiento del tipo solicitado ubicado en un domicilio coincidente
     * @param calle
     * @param numero
     * @param tipo
     * @param idLocalidad
     * @return 
     */
    public List<Establecimiento> getExistente(String calle, String numero, Long idLocalidad, TipoEstablecimiento tipo){
        em = getEntityManager();       
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.tipo = :tipo "
                + "AND est.domicilio.calle = :calle "
                + "AND est.domicilio.numero = :numero "
                + "AND est.domicilio.idLocalidad = :idLocalidad";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo)
                .setParameter("calle", calle)
                .setParameter("numero", numero)
                .setParameter("idLocalidad", idLocalidad);
        return q.getResultList();
    }   
    
    /**
     * Método para consultar por la existencia de un domicilio legal existente
     * @param cuit
     * @param tipoPer = "false" para Física y "true" para Jurídica
     * @return 
     */
    public boolean noExisteDomLegal(Long cuit, boolean tipoPer){
        String tipo = "Domicilio legal";
        em = getEntityManager();
        String queryString; 
        Query q;
        if(tipoPer){
            queryString = "SELECT est FROM Establecimiento est "
                    + "WHERE est.tipo.nombre = :tipo "
                    + "AND est.perJuridica.cuit = :cuit";
            q = em.createQuery(queryString)
                    .setParameter("tipo", tipo)
                    .setParameter("cuit", cuit);   
        }else{
            queryString = "SELECT est FROM Establecimiento est "
                    + "WHERE est.tipo.nombre = :tipo "
                    + "AND est.perFisica.cuitCuil = :cuit";
            q = em.createQuery(queryString)
                    .setParameter("tipo", tipo)
                    .setParameter("cuit", cuit);   
        }
        return q.getResultList().isEmpty();
    }  

}
