/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Especialidad;
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
public class EspecialidadFacade extends AbstractFacade<Especialidad> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EspecialidadFacade() {
        super(Especialidad.class);
    }
      /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre: es la cadena que buscara para ver si ya existe en la BDD
     * @return: devuelve True o False
     */
    public boolean existe(String nombre){
        em = getEntityManager();       
        String queryString = "SELECT esp.nombre FROM Especialidad esp "
                + "WHERE esp.nombre = :stringParam ";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList().isEmpty();
    }    
    
    /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre
     * @return: devuelve True o False
     */
    public boolean noExiste(String nombre){
        em = getEntityManager();
        String queryString = "SELECT esp FROM Especialidad esp "
                + "WHERE esp.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que obtiene un Centro Poblado existente según los datos recibidos como parámetro
     * @param nombre
     * @return 
     */ 
    public Especialidad getExistente(String nombre){
        List<Especialidad> lCp;
        em = getEntityManager();
        String queryString = "SELECT esp FROM Especialidad esp "
                + "WHERE esp.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre);
        lCp = q.getResultList();
        if(!lCp.isEmpty()){
            return lCp.get(0);
        }else{
            return null;
        }
    }    

    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();        
        String queryString = "SELECT est FROM Establecimiento est "
                + "INNER JOIN est.especialidades esp " 
                + "WHERE esp.id = :id";        
        Query q = em.createQuery(queryString)
                .setParameter("id", id);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método para ordenar las Especialidades alfabéticamente
     * @return 
     */
    public List<Especialidad> findAllByOrder(){
        em = getEntityManager();
        String queryString = "SELECT esp FROM Especialidad esp "
                + "ORDER BY esp.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }    
}

