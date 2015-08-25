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
     * Metodo que verifica si ya existe la entidad.
     * @param nombre
     * @return: devuelve True o False
     */
    public boolean noExiste(String nombre, Establecimiento establecimiento){
        em = getEntityManager();
        String queryString = "SELECT act FROM Establecimiento act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.establecimiento = :establecimiento";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("establecimiento", establecimiento);
        return q.getResultList().isEmpty();
    }  
    
    /**
     * Método que obtiene un Centro Poblado existente según los datos recibidos como parámetro
     * @param nombre
     * @return 
     */ 
    public Establecimiento getExistente(String nombre, Establecimiento establecimiento){
        List<Establecimiento> lCp;
        em = getEntityManager();
        String queryString = "SELECT act FROM Establecimiento act "
                + "WHERE act.nombre = :stringParam "
                + "AND act.establecimiento = :establecimiento";
        Query q = em.createQuery(queryString)
                .setParameter("stringParam", nombre)
                .setParameter("establecimiento", establecimiento);
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
        String queryString = "SELECT act FROM Establecimiento act " 
                + "WHERE act.establecimiento.id = :idParam ";        
        Query q = em.createQuery(queryString)
                .setParameter("idParam", id);
        return q.getResultList().isEmpty();
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


