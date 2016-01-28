/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
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
public class EstadoFacade extends AbstractFacade<Estado> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstadoFacade() {
        super(Estado.class);
    }   
    
    /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre
     * @return: devuelve True o False
     */
    public boolean noExiste(String nombre){
        em = getEntityManager();
        String queryString = "SELECT est FROM Estado est "
                + "WHERE est.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que obtiene un Centro Poblado existente según los datos recibidos como parámetro
     * @param nombre
     * @return 
     */ 
    public Estado getExistente(String nombre){
        List<Estado> lCp;
        em = getEntityManager();
        String queryString = "SELECT est FROM Estado est "
                + "WHERE est.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
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
        String queryString;
        Query q;
        boolean result = true;
                
        // verifico que no tenga personasa físicas vinculadas
        queryString = "SELECT perFis FROM PerFisica perFis "
                + "WHERE perFis.estado.id = :id";        
        q = em.createQuery(queryString)
                .setParameter("id", id);
        if(!q.getResultList().isEmpty()){
            result = false;
        }
        
        // verifico que no tenga personas jurídicas vinculadas
        queryString = "SELECT perJur FROM PerJuridica perJur "
                + "WHERE perJur.estado.id = :id";        
        q = em.createQuery(queryString)
                .setParameter("id", id);
        if(!q.getResultList().isEmpty()){
            result = false;
        }
        
        // verifico que no tenga establecimientos vinculados
        queryString = "SELECT establ FROM Establecimiento establ "
                + "WHERE establ.estado.id = :id";        
        q = em.createQuery(queryString)
                .setParameter("id", id);
        if(!q.getResultList().isEmpty()){
            result = false;
        }
        return result;
    }  
}
