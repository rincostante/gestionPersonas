/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
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
public class PerFisicaFacade extends AbstractFacade<PerFisica> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerFisicaFacade() {
        super(PerFisica.class);
    }
        /**
     * Método para validad que no exista una Persona Fisica con ese perfil
     * @param cuitCuil
     * @return 
     */
    public boolean noExiste(long cuitCuil){
        em = getEntityManager();
        String queryString = "SELECT pf FROM PerFisica pf "
                + "WHERE pf.cuitCuil = :cuitCuil";
        Query q = em.createQuery(queryString)
                .setParameter("cuitCuil", cuitCuil);
        return q.getResultList().isEmpty();
    }    
    
    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();     
        String queryString;
        Query q;
        boolean result = true;
        
        // verifico que no tenga establecimientos asignados
        queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perFisica.id = :id";        
        q = em.createQuery(queryString)
                .setParameter("id", id);
        if(!q.getResultList().isEmpty()){
            result = false;
        }
        
        // verifico que no tenga establecimientos asignados
        queryString = "SELECT reasig FROM ReasignaRazonSocial reasig "
                + "WHERE reasig.perFisica.id = :id "
                + "AND reasig.activa = true";        
        q = em.createQuery(queryString)
                .setParameter("id", id);
        if(!q.getResultList().isEmpty()){
            result = false;
        }
        
        return result;
    } 
    
   /**
     * Método que devuelve un LIST con las entidades HABILITADAS
     * @return: True o False
     */
    public List<PerFisica> getHabilitados(){
        em = getEntityManager();        
        List<PerFisica> result;
        String queryString = "SELECT pf FROM PerFisica pf " 
                + "WHERE pf.adminentidad.habilitado = true";                   
        Query q = em.createQuery(queryString);
        result = q.getResultList();
        return result;
    } 

    public PerFisica getExistente(long cuitCuil) {
        em = getEntityManager();
        List<PerFisica> result;
        String queryString = "SELECT pf FROM PerFisica pf "
                + "WHERE pf.cuitCuil = :cuitCuil";
        Query q = em.createQuery(queryString)
                .setParameter("cuitCuil", cuitCuil);
        result = q.getResultList();
        if(result.isEmpty()){
            return null;
        }else{
            return result.get(0);
        }
    }
}
