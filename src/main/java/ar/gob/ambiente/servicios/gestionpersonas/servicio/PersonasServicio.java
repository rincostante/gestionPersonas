

package ar.gob.ambiente.servicios.gestionpersonas.servicio;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Fachada que expone los métodos de acceso a datos para el servicio web
 * @author rincostante
 */
@Stateless
@LocalBean
public class PersonasServicio {

    @EJB
    private EstablecimientoFacade estFacade;
    @EJB
    private PerFisicaFacade perFisFacade;
    @EJB
    private PerJuridicaFacade perJurFacade;
    
    private static final Logger logger = Logger.getLogger(Establecimiento.class.getName());
    
    /**********************
     * Establecimientos ***
     **********************/
    /**
     * Retorna los establecimientos ordenados por razón social
     * @return 
     */
    public List<Establecimiento> getEstablecimientos(){
        List<Establecimiento> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = estFacade.findAll();
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientos() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientos() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }    
    
    /**
     * Retorna el Establecimiento correspondiente
     * @param idEstablecimiento
     * @return 
     */
    public Establecimiento getEstablecimientoPorId(Long idEstablecimiento){
        Establecimiento result;
        Date date;
        try{
            result = estFacade.find(idEstablecimiento);
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientoPorId() desde el servicio RUP");
            return result;
        }catch(Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientoPorId() desde el servicio RUP. " + date + ". ", ex);
            return null;
        }
    }
    
    /**
     * Retorna los establecimientos vinculados al cuit recibido
     * @param cuit
     * @return 
     */
    public List<Establecimiento> getEstablecimientosPorCUIT(Long cuit){
        List<Establecimiento> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = estFacade.getByCuit(cuit);
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientosPorCUIT() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientosPorCUIT() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }
    
    /**
     * Retorna los establecimientos vinculados a la razón social recibida
     * @param razonSocial
     * @return 
     */
    public List<Establecimiento> getEstablecimientoPorRazonSocial(String razonSocial){
        List<Establecimiento> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = estFacade.getByRazonSocial(razonSocial);
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientoPorRazonSocial() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientoPorRazonSocial() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }
    
    /**
     * Retorna los Establecimientos que tramitan con el expediente recibido
     * @param num
     * @param anio
     * @return 
     */
    public List<Establecimiento> getEstablecimientosPorExp(int num, int anio){
        List<Establecimiento> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = estFacade.getByExp(num, anio);
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientosPorExp() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientosPorExp() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }
    
    /**
     * Retorna solo los Establecimientos habilitados, ordanados por razón social
     */
    public List<Establecimiento> getEstablecimientosHabilitados(){
        List<Establecimiento> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = estFacade.getAtivos();
            logger.log(Level.INFO, "Ejecutando el método getEstablecimientosHabilitados() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getEstablecimientosHabilitados() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }

    
    /*********************
     * Personas Físicas **
     *********************/
    
    /**
     * Método que devuelve las personasa físicas más allá de su estado
     * @return 
     */
    public List<PerFisica> getPerFisicas(){
        List<PerFisica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perFisFacade.findAll();
            logger.log(Level.INFO, "Ejecutando el método getPerFisicas() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicas() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }    
    
    /**
     * Método que devuelve las personasa físicas habilitadas
     * @return 
     */
    public List<PerFisica> getPerFisicasHabilitadas(){
        List<PerFisica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perFisFacade.getHabilitados();
            logger.log(Level.INFO, "Ejecutando el método getPerFisicasHabilitadas() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicasHabilitadas() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst; 
    }
    
    /**
     * Método que devuelve las personasa físicas vinculadas al cuit
     * @param cuit
     * @return 
     */
    public PerFisica getPerFisicasPorCuit(Long cuit){
        PerFisica perFis = new PerFisica();
        Date date;
        try{
            perFis = perFisFacade.getByCuit(cuit);
            logger.log(Level.INFO, "Ejecutando el método getPerFisicasPorCuit() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicasPorCuit() desde el servicio RUP. " + date + ". ", ex);
        }      
        return perFis; 
    }    
    
    /**
     * Método que devuelve las personasa físicas vinculadas al nombre
     * @param nombre
     * @return 
     */
    public List<PerFisica> getPerFisicasPorNombre(String nombre){
        List<PerFisica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perFisFacade.getByNombre(nombre);
            logger.log(Level.INFO, "Ejecutando el método getPerFisicasPorNombre() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicasPorNombre() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst; 
    }    
    
    /**
     * Método que devuelve las persona física según su id
     * @param id
     * @return 
     */
    public PerFisica getPerFisicaPorId(Long id){
        PerFisica result;
        Date date;
        try{
            result = perFisFacade.find(id);
            logger.log(Level.INFO, "Ejecutando el método getPerFisicaPorId() desde el servicio RUP");
            return result;
        }catch(Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicaPorId() desde el servicio RUP. " + date + ". ", ex);
            return null;
        }
    }
    
    /**
     * Retorna los Establecimientos que tramitan con el expediente recibido
     * @param num
     * @param anio
     * @return 
     */
    public List<PerFisica> getPerFisicasPorExp(int num, int anio){
        List<PerFisica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perFisFacade.getByExp(num, anio);
            logger.log(Level.INFO, "Ejecutando el método getPerFisicasPorExp() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerFisicasPorExp() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }    
    
    
    /***********************
     * Personas Jurídicas **
     ***********************/
    
    /**
     * Método que devuelve las persona jurídica según su id
     * @param id
     * @return 
     */
    public PerJuridica getPerJuridicaPorId(Long id){
        PerJuridica result;
        Date date;
        try{
            result = perJurFacade.find(id);
            logger.log(Level.INFO, "Ejecutando el método getPerJuridicaPorId() desde el servicio RUP");
            return result;
        }catch(Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerJuridicaPorId() desde el servicio RUP. " + date + ". ", ex);
            return null;
        }
    }    
    
    /**
     * Método que devuelve las personas jurídicas más allá de su estado
     * @return 
     */
    public List<PerJuridica> getPerJuridicas(){
        List<PerJuridica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perJurFacade.findAll();
            logger.log(Level.INFO, "Ejecutando el método getPerJuridicas() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerJuridicas() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst;
    }    
    
    /**
     * Método que devuelve las personas jurídicas habilitadas
     * @return 
     */
    public List<PerJuridica> getPerJuridicasHabilitadas(){
        List<PerJuridica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perJurFacade.getHabilitados();
            logger.log(Level.INFO, "Ejecutando el método getPerJuridicasHabilitadas() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerJuridicasHabilitadas() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst; 
    }    
    
    /**
     * Método que devuelve las personas jurídica cuyo cuit coincide con el parámetros de búsqueda
     * @return 
     */
    public PerJuridica getPerJuridicasPorCuit(Long cuit){
        PerJuridica perJur = new PerJuridica();
        Date date;
        try{
            perJur = perJurFacade.getByCuit(cuit);
            logger.log(Level.INFO, "Ejecutando el método getPerJuridicasPorCuit() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerJuridicasPorCuit() desde el servicio RUP. " + date + ". ", ex);
        }      
        return perJur; 
    }    
    
    /**
     * Método que devuelve las personas jurídicas vinculadas a la razón social
     * @param razonSocial
     * @return 
     */
    public List<PerJuridica> getPerJuridicasPorRazonSocial(String razonSocial){
        List<PerJuridica> lstEst = new ArrayList();
        Date date;
        try{
            lstEst = perJurFacade.getByRazonSocial(razonSocial);
            logger.log(Level.INFO, "Ejecutando el método getPerJuridicasPorNombre() desde el servicio RUP");
        }
        catch (Exception ex){
            date = new Date(System.currentTimeMillis());
            logger.log(Level.SEVERE, "Hubo un error al ejecutar el método getPerJuridicasPorNombre() desde el servicio RUP. " + date + ". ", ex);
        }      
        return lstEst; 
    }      
}
