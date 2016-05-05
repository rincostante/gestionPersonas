

package ar.gob.ambiente.servicios.gestionpersonas.ws;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.servicio.PersonasServicio;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * Servicio web para la gestión de personas físicas y jurídicas vinculadas al Ministerio
 * @author rincostante
 */
@WebService(serviceName = "PersonasWebService")
@Stateless()
public class PersonasWebService {

    @EJB
    private PersonasServicio perSrv;
    
    /**************************************
     * Métodos para los Establecimientos **
     **************************************/
    
    /**
     * Método para buscar un establecimiento por su id
     * @param idEstablecimiento
     * @return 
     */
    @WebMethod(operationName = "buscarEstablecimientoPorId")
    public Establecimiento getEstablecimientoPorId(@WebParam(name = "idEstablecimiento") Long idEstablecimiento) {
        return perSrv.getEstablecimientoPorId(idEstablecimiento);
    }
    
    /**
     * Método para ver todos los establecimientos, más allá de su estado
     * @return 
     */
    @WebMethod(operationName = "verEstablecimientos")
    public List<Establecimiento> getEstablecimientos(){
        return perSrv.getEstablecimientos();
    }
    
    /**
     * Método para buscar establecimientos según el cuit de su razón social
     * @param cuitEst
     * @return 
     */
    @WebMethod(operationName = "buscarEstablecimientosPorCUIT")
    public List<Establecimiento> getEstablecimientosPorCUIT(@WebParam(name = "cuitEst") Long cuitEst){
        return perSrv.getEstablecimientosPorCUIT(cuitEst);
    }
    
    /**
     * Método para buscar establecimientos segén el nombre de su razón social
     * @param razonSocialEst
     * @return 
     */
    @WebMethod(operationName = "buscarEstablecimientoPorRazonSocial")
    public List<Establecimiento> getEstablecimientoPorRazonSocial(@WebParam(name = "razonSocialEst") String razonSocialEst){
        return perSrv.getEstablecimientoPorRazonSocial(razonSocialEst);
    }
    
    /**
     * Método para buscar establecimientos según su expediente asociado
     * @param numExpEst
     * @param anioExpEst
     * @return 
     */
    @WebMethod(operationName = "buscarEstablecimientosPorExp")
    public List<Establecimiento> getEstablecimientosPorExp(@WebParam(name = "numExpEst") int numExpEst, @WebParam(name = "anioExpEst") int anioExpEst){
        return perSrv.getEstablecimientosPorExp(numExpEst, anioExpEst);
    }
    
    /**
     * Método ver los establecimientos habilitados
     * @return 
     */
    @WebMethod(operationName = "verEstablecimientosHabilitados")
    public List<Establecimiento> getEstablecimientosHabilitados(){
        return perSrv.getEstablecimientosHabilitados();
    }
    
    
    /**************************************
     * Métodos para las Personas Físicas **
     **************************************/    
    
    /**
     * Método para buscar una Persona Jurídica por su id
     * @param idPerFis
     * @return 
     */
    @WebMethod(operationName = "buscarPerFisicaPorId")
    public PerFisica getPerFisicaPorId(@WebParam(name = "idPerFis") Long idPerFis){
        return perSrv.getPerFisicaPorId(idPerFis);
    }
    
    /**
     * Método para ver todas las personas físicas, más allá de su estado
     * @return 
     */
    @WebMethod(operationName = "verPerFisicas")
    public List<PerFisica> getPerFisicas(){
        return perSrv.getPerFisicas();
    }
    
    /**
     * Método para ver las personas físicas habilitadas
     * @return 
     */
    @WebMethod(operationName = "verPerFisicasHabilitadas")
    public List<PerFisica> getPerFisicasHabilitadas(){
        return perSrv.getPerFisicasHabilitadas();
    }
    
    /**
     * Método para buscar una Persona Física según su cuit/cuil
     * @param cuitPerFis
     * @return 
     */
    @WebMethod(operationName = "buscarPerFisicasPorCuit")
    public PerFisica getPerFisicasPorCuit(@WebParam(name = "cuitPerFis") Long cuitPerFis){
        return perSrv.getPerFisicasPorCuit(cuitPerFis);
    }
    
    /**
     * Método para buscar Personas Físicas por su nombre
     * @param nombrePerFis
     * @return 
     */
    @WebMethod(operationName = "buscarPerFisicasPorNombre")
    public List<PerFisica> getPerFisicasPorNombre(@WebParam(name = "nombrePerFis") String nombrePerFis){
        return perSrv.getPerFisicasPorNombre(nombrePerFis);
    }    
    
    /**
     * Método para buscar Personas Físicas según su expediente asociado
     * @param numExpEst
     * @param anioExpEst
     * @return 
     */
    @WebMethod(operationName = "buscarPerFisicasPorExp")
    public List<PerFisica> getPerFisicasPorExp(@WebParam(name = "numExpPerFis") int numExpEst, @WebParam(name = "anioExpPerFis") int anioExpEst){
        return perSrv.getPerFisicasPorExp(numExpEst, anioExpEst);
    }
    

    /****************************************
     * Métodos para las Personas Jurídicas **
     ****************************************/
    
    /**
     * Método para buscar una Persona Jurídica por su id
     * @param idPerJur
     * @return 
     */
    @WebMethod(operationName = "buscarPerJuridicaPorId")
    public PerJuridica getPerJuridicaPorId(@WebParam(name = "idPerJur") Long idPerJur){
        return perSrv.getPerJuridicaPorId(idPerJur);
    }
    
    /**
     * Método para ver todas las Personas Jurídicas sin inportar su estado
     * @return 
     */
    @WebMethod(operationName = "verPerJuridicas")
    public List<PerJuridica> getPerJuridicas(){
        return perSrv.getPerJuridicas();
    }
    
    /**
     * Método para ver las Personas Jurídicas habilitadas
     * @return 
     */
    @WebMethod(operationName = "verPerJuridicasHabilitadas")
    public List<PerJuridica> getPerJuridicasHabilitadas(){
        return perSrv.getPerJuridicasHabilitadas();
    }
    
    /**
     * Método para buscar la Persona Juridica según su cuit
     * @param cuitPerJur
     * @return 
     */
    @WebMethod(operationName = "buscarPerJuridicasPorCuit")
    public PerJuridica getPerJuridicasPorCuit(@WebParam(name = "cuitPerJur") Long cuitPerJur){
        return perSrv.getPerJuridicasPorCuit(cuitPerJur);
    }
    
    /**
     * Método para buscar Personas Jurídicas según su Razón Social
     * @param razonSocialPerJur
     * @return 
     */
    @WebMethod(operationName = "buscarPerJuridicasPorRazonSocial")
    public List<PerJuridica> getPerJuridicasPorRazonSocial(@WebParam(name = "razonSocialPerJur") String razonSocialPerJur){
        return perSrv.getPerJuridicasPorRazonSocial(razonSocialPerJur);
    }    
}
