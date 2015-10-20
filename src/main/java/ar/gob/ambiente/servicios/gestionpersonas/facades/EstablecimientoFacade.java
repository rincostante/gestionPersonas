/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
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
     * Metodo que verifica si ya existe la entidad.
     * @param aBuscar: es la cadena que buscara para ver si ya existe en la BDD
     * @return: devuelve True o False
     */
    public boolean existe(String aBuscar){
        em = getEntityManager();       
        String queryString = "SELECT act.nombre FROM Establecimiento establecimiento "
                + "WHERE act.nombre = :stringParam ";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", aBuscar);
        return q.getResultList().isEmpty();
    }    
    
    /**
     * Método para validad que no exista una Actividad Planificada con este nombre ya ingresado
     * @param telefono
     * @param domicilio
     * @return 
     */
    public boolean noExiste(String telefono, int domicilio){
        em = getEntityManager();       
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.telefono = :telefono "
                + "AND est.domicilio = :domicilio";
        Query q = em.createQuery(queryString)
                .setParameter("telefono", telefono)
                .setParameter("domicilio", domicilio);
        return q.getResultList().isEmpty();
    }   
    
    /**
     * Método que obtiene un Centro Poblado existente según los datos recibidos como parámetro
    * @param domicilio
    * @param actividad
     * @return 
   */
    public Establecimiento getExistente(Domicilio domicilio, Actividad actividad){
        List<Establecimiento> lCp;
        em = getEntityManager();
        String queryString = "SELECT act FROM Establecimiento act "
                + "WHERE act.domicilio = :stringParam "
                + "AND act.actividad = :actividad";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", domicilio)
                .setParameter("actividad", actividad);
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
        String queryString = "SELECT act.nombre FROM Establecimiento act ";
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
        String queryString = "SELECT est FROM Establecimiento est " 
                + "WHERE est.establecimiento.id = :idParam "
                + "AND est.adminentidad.habilitado = true"; 
        Query q = em.createQuery(queryString)
                .setParameter("idParam", id);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que devuelve todos los estados de una app 
     * @param actividad: ID de la entidad
     * @return: True o False
     */
    public List<Establecimiento> getEstabXactividad(int actividad){
        em = getEntityManager();        
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.actividad = :actividad"; 

      
        Query q = em.createQuery(queryString)
                .setParameter("actividad", actividad);
        return q.getResultList();
    }
    /**
     * Método que devuelve todos los estados de una app 
     * @param pjur: ID de la entidad
     * @return: True o False
     */
    public List<Establecimiento> getEstabXpJuridica(int pjur){
        em = getEntityManager();        
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perJuridica = :pjur"; 

      
        Query q = em.createQuery(queryString)
                .setParameter("pjur", pjur);
        return q.getResultList();
    }    

    public List<Establecimiento> findAll(int perJuridica) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean noExisteDomicilio(Object object, Domicilio domicilio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean noExiste(long l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Establecimiento getExistente(Actividad actividad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
