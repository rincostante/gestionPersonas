/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
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
     * @param dni
     * @return 
     */
    public boolean noExiste(long dni){
        em = getEntityManager();
        String queryString = "SELECT pf FROM PerFisica pf "
                + "WHERE pf.dni = :dni";
        Query q = em.createQuery(queryString)
                .setParameter("dni", dni);
        return q.getResultList().isEmpty();
    }    
    
        /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre
     * @param depto
     * @return: devuelve True o False
     */
    public boolean noExisteDomicilio(String nombre, Domicilio domicilio){
        em = getEntityManager();
        String queryString = "SELECT act FROM Domicilio act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.domicilio = :domicilio";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("domicilio", domicilio);
        return q.getResultList().isEmpty();
    } 
    
        /**
     * Metodo que verifica si ya existe la entidad.
     * @param 
     * @return: devuelve True o False
     */
    public boolean noExisteExpediente(String nombre, Expediente expediente){
        em = getEntityManager();
        String queryString = "SELECT act FROM Expediente act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.expediente = :expediente";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("expediente", expediente);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();        
        String queryString = "SELECT pf FROM PerFisica pf " 
                + "WHERE pf.perFisica.id = :idParam "
                + "AND usu.adminentidad.habilitado = true";        
        Query q = em.createQuery(queryString)
                .setParameter("idParam", id);
        return q.getResultList().isEmpty();
    } 
     
    /**
     * Metodo para el autocompletado de la búsqueda por nombre
     * @return 
     */  
    public List<String> getNombres(){
        em = getEntityManager();
        String queryString = "SELECT pf.nombre FROM PerFisica pf "
                + "WHERE us.adminentidad.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
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

    public PerFisica getExistente(Actividad actividad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
