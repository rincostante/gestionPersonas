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
                + "WHERE pf.admin.habilitado = true "
                + "ORDER BY pf.nombreCompleto";                   
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
    
    /**
     * Retorna todas las personas físicas registradas
     * @return 
     */
    @Override
    public List<PerFisica> findAll(){
        return findAllByField("nombreCompleto");
    }    
    
    /**
     * Retorna las personasas físicas vinculadas al cuit
     * @param cuit
     * @return 
     */
    public PerFisica getByCuit(Long cuit){
        em = getEntityManager();    
        List<PerFisica> result;
        String queryString = "SELECT pf FROM PerFisica pf " 
                + "WHERE pf.cuitCuil = :cuit";                   
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        result = q.getResultList();
        if(result.isEmpty()){
            return null;
        }else{
            return result.get(0);
        }        
    }    
    
    /**
     * Retorna las personas físicas cuya nombre responda a la búsqueda
     * @param nombreCompleto
     * @return 
     */
    public List<PerFisica> getByNombre(String nombreCompleto){
        em = getEntityManager();
        List<PerFisica> result;
        String queryString = "SELECT perFis FROM PerFisica perFis "
                + "WHERE perFis.nombreCompleto LIKE :nombreCompleto "
                + "AND perFis.admin.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("nombreCompleto", "%" + nombreCompleto + "%");
        result = q.getResultList();
        return result;
    }     

    /**
     * Método que retorna las Personas Físicas según el expediente que tramitan
     * @param num
     * @param anio
     * @return 
     */
    public List<PerFisica> getByExp(int num, int anio){
        em = getEntityManager();       
        String queryString = "SELECT perFis FROM PerFisica perFis "
                + "WHERE perFis.expediente.numero = :num "
                + "AND perFis.expediente.anio = :anio";
        Query q = em.createQuery(queryString)
                .setParameter("num", num)
                .setParameter("anio", anio);
        return q.getResultList();
    }    
}
