/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
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
     * Método que retorna un Establecimiento del tipo solicitado ubicado en un domicilio coincidente
     * @param calle
     * @param numero
     * @param tipo
     * @param idLocalidad
     * @return 
     */
    public List<Establecimiento> getExistente(String calle, String numero, Long idLocalidad, TipoEstablecimiento tipo){
        em = getEntityManager();       
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.tipo = :tipo "
                + "AND est.domicilio.calle = :calle "
                + "AND est.domicilio.numero = :numero "
                + "AND est.domicilio.idLocalidad = :idLocalidad";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo)
                .setParameter("calle", calle)
                .setParameter("numero", numero)
                .setParameter("idLocalidad", idLocalidad);
        return q.getResultList();
    }   
    
    /**
     * Retorna todos los Establecimientos habilitados
     * Ordenados por razón social
     * @return 
     */
    public List<Establecimiento> getAtivos(){
        em = getEntityManager();       
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.admin.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método para consultar por la existencia de un domicilio legal existente
     * @param cuit
     * @param tipoPer = "false" para Física y "true" para Jurídica
     * @return 
     */
    public boolean noExisteDomLegal(Long cuit, boolean tipoPer){
        String tipo = "Domicilio legal";
        em = getEntityManager();
        String queryString; 
        Query q;
        if(tipoPer){
            queryString = "SELECT est FROM Establecimiento est "
                    + "WHERE est.tipo.nombre = :tipo "
                    + "AND est.perJuridica.cuit = :cuit";
            q = em.createQuery(queryString)
                    .setParameter("tipo", tipo)
                    .setParameter("cuit", cuit);   
        }else{
            queryString = "SELECT est FROM Establecimiento est "
                    + "WHERE est.tipo.nombre = :tipo "
                    + "AND est.perFisica.cuitCuil = :cuit";
            q = em.createQuery(queryString)
                    .setParameter("tipo", tipo)
                    .setParameter("cuit", cuit);   
        }
        return q.getResultList().isEmpty();
    }  

    /**
     * Método para obtener los Establecimientos por el cuit de la razón social
     * @param cuit
     * @return 
     */
    public List<Establecimiento> getByCuit(Long cuit){
        List<Establecimiento> lstEst;
        em = getEntityManager();       
        
        // busco por persona jurídica
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perJuridica.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstEst = q.getResultList();
        
        // busco por persona física
        queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perFisica.cuitCuil = :cuit";
        q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstEst.addAll(q.getResultList());
        
        return lstEst;
    }
    
    /**
     * Método que retorna los Establecimientos según su razón social
     * @param razonSocial
     * @return 
     */
    public List<Establecimiento> getByRazonSocial(String razonSocial){
        List<Establecimiento> lstEst;
        em = getEntityManager();       
        
        // busco por persona jurídica
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perJuridica.razonSocial LIKE :razonSocial";
        Query q = em.createQuery(queryString)
                .setParameter("razonSocial", "%" + razonSocial + "%");
        lstEst = q.getResultList();
        
        // busco por persona física
        queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.perFisica.nombreCompleto LIKE :razonSocial";
        q = em.createQuery(queryString)
                .setParameter("razonSocial", "%" + razonSocial + "%");
        lstEst.addAll(q.getResultList());

        return lstEst;
    }
    
    /**
     * Método que retorna los Establecimientos según el expediente que tramitan
     * @param num
     * @param anio
     * @return 
     */
    public List<Establecimiento> getByExp(int num, int anio){
        em = getEntityManager();       
        String queryString = "SELECT est FROM Establecimiento est "
                + "WHERE est.expediente.numero = :num "
                + "AND est.expediente.anio = :anio";
        Query q = em.createQuery(queryString)
                .setParameter("num", num)
                .setParameter("anio", anio);
        return q.getResultList();
    }
}
