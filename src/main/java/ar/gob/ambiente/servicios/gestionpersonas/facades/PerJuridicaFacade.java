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
    public boolean noExiste(long cuit){
        em = getEntityManager();
        String queryString = "SELECT cuit FROM PerJuridica pj "
                + "WHERE pj.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList().isEmpty();
    }        
    
    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();        
        String queryString = "SELECT pj FROM PerJuridica pj " 
                + "WHERE pj.perJuridica.id = :idParam "
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
        String queryString = "SELECT pj.nombre FROM PerJuridica pj "
                + "WHERE us.adminentidad.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
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

    public PerJuridica getExistente(String correoElectronico) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<PerJuridica> getNombres(PerJuridica selectPerJuridica) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
