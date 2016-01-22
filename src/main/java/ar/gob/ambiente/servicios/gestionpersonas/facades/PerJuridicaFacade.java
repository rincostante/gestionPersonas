/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
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
    public class PerJuridicaFacade extends AbstractFacade<PerJuridica> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerJuridicaFacade() {
        super(PerJuridica.class);
    }
    /**
     * Método para validad que no exista una Persona Juridica con ese nombre
     * @param cuit
     * @return 
     */
    public boolean noExiste(String cuit){
        em = getEntityManager();
        String queryString = "SELECT pj.cuit FROM PerJuridica pj "
                + "WHERE pj.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList().isEmpty();
    }        
    
   /**
     * Método que devuelve un LIST con las entidades HABILITADAS
     * @return: True o False
     */
    public List<PerJuridica> getHabilitados(){
        em = getEntityManager();        
        List<PerJuridica> result;
        String queryString = "SELECT pj FROM PerJuridica pj " 
                + "WHERE pj.adminentidad.habilitado = true";                   
        Query q = em.createQuery(queryString);
        result = q.getResultList();
        return result;
    }      
    /**
     * Método que obtiene una Procedimiento existente según los datos recibidos como parámetro
     * @param razonSocial
     * @param cuit
     * @return 
     */
    public PerJuridica getExistente(String razonSocial, String cuit){
        List<PerJuridica> lPerj;
        String queryString = "SELECT perj FROM PerJuridica perj "
                + "WHERE perj.razonSocial = :razonSocial "
                + "AND perj.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("razonSocial", razonSocial)
                .setParameter("cuit", cuit);
        lPerj = q.getResultList();
        if(!lPerj.isEmpty()){
            return lPerj.get(0);
        }else{
            return null;
        }
    } 
}
