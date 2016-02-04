/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.facades;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.ReasignaRazonSocial;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rincostante
 */
@Stateless
public class ReasignaRazonSocialFacade extends AbstractFacade<ReasignaRazonSocial> {
    @PersistenceContext(unitName = "gestionPersonasPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReasignaRazonSocialFacade() {
        super(ReasignaRazonSocial.class);
    }
    
    /**
     * Método para obtener el registro del último cambio de razón social para un Establecimiento
     * @param est
     * @return 
     */
    public ReasignaRazonSocial getUltimaActiva(Establecimiento est){
        List<ReasignaRazonSocial> listRs;
        em = getEntityManager();       
        String queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                + "WHERE rs.establecimiento = :est "
                + "AND rs.activa = true";
        Query q = em.createQuery(queryString)
                .setParameter("est", est);
        listRs = q.getResultList();
        if(!listRs.isEmpty()){
            return listRs.get(0);
        }else{
            return null;
        }
    }
    
    /**
     * Método para controlar errores en la edición con cambio de razón social
     * @param est: Establecimiento al cual se le realizó el cambio
     * @param rsAnterior: Ultima asignación de Razón social vigente, puede ser nula
     * @param rsNueva: Reasignación de la Razón social actual.
     */
    public void cambiarRazonSocial(Establecimiento est, ReasignaRazonSocial rsAnterior, ReasignaRazonSocial rsNueva){
        em = getEntityManager();
        EntityTransaction tx = em.getTransaction(); 
        // abro la transacción
        tx.begin(); 
        try{
            em.merge(est);
            em.persist(rsNueva);
            if(rsAnterior != null){
                em.merge(rsAnterior);
            }
            tx.commit();
        }catch(Exception ex){
            tx.rollback();
            System.out.println("Hubo un error persistiendo el cambio de Razón Social " + ex.getMessage());
        }
    }    
    
    /**
     * Método para leer el historial de cambio de razones sociales de un establecimiento
     * @param est
     * @return 
     */
    public List<ReasignaRazonSocial> getHistorial(Establecimiento est){
        em = getEntityManager();       
        String queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                + "WHERE rs.establecimiento = :est";
        Query q = em.createQuery(queryString)
                .setParameter("est", est);
        return q.getResultList();
    }
    
    /**
     * Método para listar los historiales para una persona
     * @param perFis: Persona Jurídica que cede o adquiere. Puede ser nulo
     * @param perJur: Persona Física que cede o adquiere. Puede ser nulo
     * @param esCedente: Booleano que indica true si es cedente y false si es adquirente
     * @return 
     */
    public List<ReasignaRazonSocial> getHistorialXRazonSocial(PerFisica perFis, PerJuridica perJur, boolean esCedente){
        List<ReasignaRazonSocial> listRs;
        em = getEntityManager();       
        String queryString;
        Query q;
        
        if(esCedente){
            if(perJur != null){
                queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                        + "WHERE rs.exPerJuridica = :perJur";
                    q = em.createQuery(queryString)
                            .setParameter("perJur", perJur);
                    listRs = q.getResultList();
            }else{
                queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                        + "WHERE rs.exPerFisica = :perFis";
                q = em.createQuery(queryString)
                        .setParameter("perFis", perFis);
                listRs = q.getResultList();
            }
        }else{
            if(perJur != null){
                queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                        + "WHERE rs.perJuridica = :perJur";
                q = em.createQuery(queryString)
                        .setParameter("perJur", perJur);
                listRs = q.getResultList();
            }else{
                queryString = "SELECT rs FROM ReasignaRazonSocial rs "
                        + "WHERE rs.perFisica = :perFis";
                q = em.createQuery(queryString)
                        .setParameter("perFis", perFis);
                listRs = q.getResultList();
            }
        }

        return listRs;
    }
}
