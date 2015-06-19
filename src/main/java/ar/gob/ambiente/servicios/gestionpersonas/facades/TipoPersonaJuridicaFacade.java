/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
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
public class TipoPersonaJuridicaFacade extends AbstractFacade<TipoPersonaJuridica> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoPersonaJuridicaFacade() {
        super(TipoPersonaJuridica.class);
    }
      /**
     * Metodo que verifica si ya existe la entidad.
     * @param aBuscar: es la cadena que buscara para ver si ya existe en la BDD
     * @return: devuelve True o False
     */
    public boolean existe(String aBuscar){
        em = getEntityManager();       
        String queryString = "SELECT act.nombre FROM TipoPersonaJuridica tipoPersonaJuridica "
                + "WHERE act.nombre = :stringParam ";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", aBuscar);
        return q.getResultList().isEmpty();
    }    
    
    /**
     * Metodo que verifica si ya existe la entidad.
     * @param nombre
     * @return: devuelve True o False
     */
    public boolean noExiste(String nombre, TipoPersonaJuridica tipoPersonaJuridica){
        em = getEntityManager();
        String queryString = "SELECT act FROM TipoPersonaJuridica act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.tipoPersonaJuridica = :tipoPersonaJuridica";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("tipoPersonaJuridica", tipoPersonaJuridica);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que obtiene un Centro Poblado existente según los datos recibidos como parámetro
     * @param nombre
     * @return 
     */ 
    public TipoPersonaJuridica getExistente(String nombre, TipoPersonaJuridica tipoPersonaJuridica){
        List<TipoPersonaJuridica> lCp;
        em = getEntityManager();
        String queryString = "SELECT act FROM TipoPersonaJuridica act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.tipoPersonaJuridica = :tipoPersonaJuridica";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("tipoPersonaJuridica", tipoPersonaJuridica);
        lCp = q.getResultList();
        if(!lCp.isEmpty()){
            return lCp.get(0);
        }else{
            return null;
        }
    }    

     /**
     * Metodo para el autocompletado de la búsqueda por nombre
     * @return 
     */  

    public List<String> getNombres(){
        em = getEntityManager();
        String queryString = "SELECT act.nombre FROM TipoPersonaJuridica act ";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método que verifica si la entidad tiene dependencia (Hijos) en estado HABILITADO
     * @param id: ID de la entidad
     * @return: True o False
     */
    public boolean noTieneDependencias(Long id){
        em = getEntityManager();        
        String queryString = "SELECT act FROM TipoPersonaJuridica act " 
                + "WHERE act.tipoPersonaJuridica.id = :idParam ";        
        Query q = em.createQuery(queryString)
                .setParameter("idParam", id);
        return q.getResultList().isEmpty();
    }  
}
