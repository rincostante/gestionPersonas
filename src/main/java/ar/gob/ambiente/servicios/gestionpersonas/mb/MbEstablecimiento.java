/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.AdminEntidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Especialidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.ReasignaRazonSocial;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.EntidadServicio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ActividadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EspecialidadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ReasignaRazonSocialFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoEstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoPersonaJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentroPoblado;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService_Service;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Departamento;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Provincia;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfip;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs_Service;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.xml.ws.WebServiceRef;
import org.primefaces.context.RequestContext;

/**
*
* @author rodriguezn
*/
public class MbEstablecimiento implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CentrosPobladosWebService/CentrosPobladosWebService.wsdl")
    private CentrosPobladosWebService_Service srvCentrosPob;    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CuitAfipWs/CuitAfipWs.wsdl")
    private CuitAfipWs_Service srvCuitAfip;
    private CuitAfip personaAfip;
    private Long cuit;
    private static final Logger logger = Logger.getLogger(Establecimiento.class.getName());
    
    private Establecimiento current;    
    private Domicilio domicilio;
    private List<Establecimiento> listado;
    private List<Establecimiento> listadoFilter;
    
    private boolean esFisica;
    private boolean esJuridica;    

    @EJB
    private EstablecimientoFacade establecimientoFacade;
    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private PerFisicaFacade perFisicaFacade;    
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private TipoEstablecimientoFacade tipoEstablecimientoFacade;
    @EJB
    private ActividadFacade actividadFacade;
    @EJB
    private ReasignaRazonSocialFacade reasignaFacade;
    @EJB
    private EspecialidadFacade espFacade;
    @EJB
    private ExpedienteFacade expFacade;
    @EJB
    private TipoPersonaJuridicaFacade tipoPerJurFacade;
    
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private List<PerJuridica> listaPerJuridica;
    private List<PerFisica> listaPerFisica;
    private List<Estado> listaEstado;
    private List<TipoEstablecimiento> listaTipoEstablecimiento;
    private List<Expediente> listaExpedientes;
    private Expediente expediente;
    
    // listados provistos por el servicio de centros poblados de Establecimientos
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;    
    
    // campos para los agregados múltiples
    private List<Especialidad> listEspDisp;
    private List<Especialidad> listEspVinc;
    private List<Especialidad> listEspFilter;
    private List<Actividad> listActDisp;
    private List<Actividad> listActVinc;
    private List<Actividad> listActFilter;
    private boolean asignaDisp; 
    
    // campos para el agregado de razones sociales
    private EntidadServicio provRazSocSelected;
    private List<EntidadServicio> listDepRazSoc;
    private EntidadServicio deptoRazSocSelected;
    private List<EntidadServicio> listLocRazSoc;
    private List<TipoPersonaJuridica> listaTipoPersonaJuridica;
    private EntidadServicio localRazSocSelected; 
    private Expediente expRazSoc;
    private PerJuridica perJuridica;
    private PerJuridica tempPerJuridica;
    private PerFisica perFisica;
    private PerFisica tempPerFisica;
    private boolean esRazonSocial;
    private List<ReasignaRazonSocial> listHist;
    private List<ReasignaRazonSocial> listHistFilter;
    private ReasignaRazonSocial rsNueva;
    
    /**
     * Creates a new instance of MbEstablecimiento
     */
    public MbEstablecimiento() {
    }
    
    /****************************
     * Métodos de inicialización
     ****************************/
    /**
     * Método que se ejecuta luego de establecimientoda la clase e inicializa los datos del usuario
     */
    @PostConstruct
    public void init(){
        iniciado = false;
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        login = (MbLogin)ctx.getSessionMap().get("mbLogin");
        usLogeado = login.getUsLogeado();  
    }
    
    /**
     * Método que borra de la memoria los MB innecesarios al cargar el listado 
     */
    public void iniciar(){
        if(!iniciado){
            String s;
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(true);
            Enumeration enume = session.getAttributeNames();
            while(enume.hasMoreElements()){
                s = (String)enume.nextElement();
                if(s.substring(0, 2).equals("mb")){
                    if(!s.equals("mbUsuario") && !s.equals("mbLogin")){
                        session.removeAttribute(s);
                    }
                }
            }
        }
    }     
    
    /********************************
     ****** Getters y Setters *******
     ********************************/
    public ReasignaRazonSocial getRsNueva() {
        return rsNueva;
    }

    public void setRsNueva(ReasignaRazonSocial rsNueva) {
        this.rsNueva = rsNueva;
    }

    public List<ReasignaRazonSocial> getListHist() {
        return listHist;
    }

    public void setListHist(List<ReasignaRazonSocial> listHist) {
        this.listHist = listHist;
    }

    public List<ReasignaRazonSocial> getListHistFilter() {
        return listHistFilter;
    }

    public void setListHistFilter(List<ReasignaRazonSocial> listHistFilter) {
        this.listHistFilter = listHistFilter;
    }

    public PerJuridica getTempPerJuridica() {
        return tempPerJuridica;
    }

    public void setTempPerJuridica(PerJuridica tempPerJuridica) {
        this.tempPerJuridica = tempPerJuridica;
    }

    public PerFisica getTempPerFisica() {
        return tempPerFisica;
    }

    public void setTempPerFisica(PerFisica tempPerFisica) {
        this.tempPerFisica = tempPerFisica;
    }

    public List<TipoPersonaJuridica> getListaTipoPersonaJuridica() {
        return listaTipoPersonaJuridica;
    }

    public void setListaTipoPersonaJuridica(List<TipoPersonaJuridica> listaTipoPersonaJuridica) {
        this.listaTipoPersonaJuridica = listaTipoPersonaJuridica;
    }

    public boolean isEsRazonSocial() {
        return esRazonSocial;
    }

    public void setEsRazonSocial(boolean esRazonSocial) {
        this.esRazonSocial = esRazonSocial;
    }

    public PerJuridica getPerJuridica() {
        return perJuridica;
    }

    public void setPerJuridica(PerJuridica perJuridica) {
        this.perJuridica = perJuridica;
    }

    public PerFisica getPerFisica() {
        return perFisica;
    }

    public void setPerFisica(PerFisica perFisica) {
        this.perFisica = perFisica;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public CuitAfip getPersonaAfip() {
        return personaAfip;
    }

    public void setPersonaAfip(CuitAfip personaAfip) {
        this.personaAfip = personaAfip;
    }

    public Expediente getExpRazSoc() {
        return expRazSoc;
    }

    public void setExpRazSoc(Expediente expRazSoc) {
        this.expRazSoc = expRazSoc;
    }

    public EntidadServicio getProvRazSocSelected() {
        return provRazSocSelected;
    }

    public void setProvRazSocSelected(EntidadServicio provRazSocSelected) {
        this.provRazSocSelected = provRazSocSelected;
    }

    public List<EntidadServicio> getListDepRazSoc() {
        return listDepRazSoc;
    }

    public void setListDepRazSoc(List<EntidadServicio> listDepRazSoc) {
        this.listDepRazSoc = listDepRazSoc;
    }

    public EntidadServicio getDeptoRazSocSelected() {
        return deptoRazSocSelected;
    }

    public void setDeptoRazSocSelected(EntidadServicio deptoRazSocSelected) {
        this.deptoRazSocSelected = deptoRazSocSelected;
    }

    public List<EntidadServicio> getListLocRazSoc() {
        return listLocRazSoc;
    }

    public void setListLocRazSoc(List<EntidadServicio> listLocRazSoc) {
        this.listLocRazSoc = listLocRazSoc;
    }

    public EntidadServicio getLocalRazSocSelected() {
        return localRazSocSelected;
    }

    public void setLocalRazSocSelected(EntidadServicio localRazSocSelected) {
        this.localRazSocSelected = localRazSocSelected;
    }

    public boolean isEsFisica() {
        return esFisica;
    }

    public void setEsFisica(boolean esFisica) {
        this.esFisica = esFisica;
    }

    public boolean isEsJuridica() {
        return esJuridica;
    }

    public void setEsJuridica(boolean esJuridica) {
        this.esJuridica = esJuridica;
    }
    
    public boolean isAsignaDisp() {
        return asignaDisp;
    }
    
    public List<Especialidad> getListEspDisp() {
        return listEspDisp;
    }

    public void setListEspDisp(List<Especialidad> listEspDisp) {
        this.listEspDisp = listEspDisp;
    }

    public List<Especialidad> getListEspFilter() {
        return listEspFilter;
    }

    public void setListEspFilter(List<Especialidad> listEspFilter) {
        this.listEspFilter = listEspFilter;
    }

    public List<Actividad> getListActDisp() {
        return listActDisp;
    }

    public void setListActDisp(List<Actividad> listActDisp) {
        this.listActDisp = listActDisp;
    }

    public List<Actividad> getListActFilter() {
        return listActFilter;
    }

    public void setListActFilter(List<Actividad> listActFilter) {
        this.listActFilter = listActFilter;
    }
    
    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

    
    public List<EntidadServicio> getListProvincias() {
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public List<EntidadServicio> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public List<EntidadServicio> getListLocalidades() {
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    
    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    
    public List<PerFisica> getListaPerFisica() {
        return listaPerFisica;
    }

    public void setListaPerFisica(List<PerFisica> listaPerFisica) {
        this.listaPerFisica = listaPerFisica;
    }

    
    public Establecimiento getCurrent() {
        return current;
    }

    public List<Expediente> getListaExpedientes() {
        return listaExpedientes;
    }

    public void setListaExpedientes(List<Expediente> listaExpedientes) {
        this.listaExpedientes = listaExpedientes;
    }

    public void setCurrent(Establecimiento current) {
        this.current = current;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public List<Establecimiento> getListado() {
        if(listado == null){
            listado = getFacade().findAll();
        }
        return listado;
    }

    public List<Establecimiento> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<Establecimiento> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    public List<PerJuridica> getListaPerJuridica() {
        return listaPerJuridica;
    }

    public void setListaPerJuridica(List<PerJuridica> listaPerJuridica) {
        this.listaPerJuridica = listaPerJuridica;
    }

    public List<Estado> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<Estado> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public List<TipoEstablecimiento> getListaTipoEstablecimiento() {
        return listaTipoEstablecimiento;
    }

    public void setListaTipoEstablecimiento(List<TipoEstablecimiento> listaTipoEstablecimiento) {
        this.listaTipoEstablecimiento = listaTipoEstablecimiento;
    }

    public TipoEstablecimientoFacade getTipoEstablecimientoFacade() {
        return tipoEstablecimientoFacade;
    }

    /**
     * @return La entidad gestionada
     */
    public Establecimiento getSelected() {
        if (current == null) {
            current = new Establecimiento();
        }
        return current;
    } 
    
    /*******************************
     ** Métodos de inicialización **
     *******************************/
    /**
     * Método para inicializar el listado de los Actividades Planificadass habilitadas
     * @return acción para el listado de entidades
     */
    public String prepareList() {
        iniciado = true;
        asignaDisp = false;
        limpiarListados();
        limpiarEntitadesSrv();
        return "list";
    } 

    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        asignaDisp = false;
        return "view";
    }

    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        asignaDisp = true;
        //Se instancia current
        current = new Establecimiento();      
        //Inicializamos la creacion de domicilio
        domicilio = new Domicilio();
        listaTipoEstablecimiento= tipoEstablecimientoFacade.findAll();
        listaEstado = estadoFacade.findAll();
        listaPerJuridica = perJuridicaFacade.findAll();
        listaPerFisica = perFisicaFacade.findAll();
        listaExpedientes = expFacade.findAllByOrder();
        // cargo el listado de Provincias
        getProvinciasSrv();    
        // cargo los listados de los disponibles
        cargarListadosDisp();
        esFisica = true;
        esJuridica = true;        
        return "new";
    }
    
   /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        // guardo los datos recibidos de perfil y razón social
        update = 0;
        guardarTempRazonSocial();
	guardarTempPerfil();
        asignaDisp = true;
        listaPerJuridica = perJuridicaFacade.findAll();
        listaPerFisica = perFisicaFacade.findAll();
        listaExpedientes = expFacade.findAllByOrder();
        listaTipoEstablecimiento= tipoEstablecimientoFacade.findAll();
        listaEstado = estadoFacade.findAll();
	if(current.getPerFisica()!= null){
            esFisica = true;
            esJuridica = false;
        }else{
            esFisica = false;
            esJuridica = true;
        } 
        cargarListadosDisp();
        cargarEntidadesSrv();
        esFisica = true;
        esJuridica = true;            
        return "edit";
    }
    
    /**
     * Método que muestra el diálogo para registrar un nuevo expediente
     */
    public void prepareRegExp(){
        if(esRazonSocial){
            expRazSoc = new Expediente();
        }else{
            expediente = new Expediente();
        }
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("dlgRegistrarExpediente", options, null);        
    }    
           
    public String prepareInicio(){
        limpiarListados();
        return "/faces/index";
    }
    
    /**
     * Damos valor a la variable Update
     */
    public void prepareDeshabilitar(){
        update = 1;
        update();        
    }   
    
    /**
     * Damos valor a la variable Update
     */
    public void prepareHabilitar(){
        update = 2;
        update();        
    }


    /*************************
    ** Métodos de operación **
    **************************/
    
        /**
     * Método para guardar los domicilio creados en el listaDomicilios que irán en la nueva persona fisica
     */
    public void createDomicilio(){
            // se agregan los datos del AdminEntidad
            Date date = new Date(System.currentTimeMillis());
            AdminEntidad admEnt = new AdminEntidad();
            admEnt.setFechaAlta(date);
            admEnt.setHabilitado(true);
            admEnt.setUsAlta(usLogeado);
            current.setAdmin(admEnt);
            //reseteo la domicilio
            domicilio = null;
            domicilio = new Domicilio();
    }
           
    /**
     * Método que inserta una nueva instancia en la base de datos, previamente genera una entidad de administración
     * con los datos necesarios y luego se la asigna al persona fisica
     * @return mensaje que notifica la inserción
     */
    public String create() {
        List<Establecimiento> listEst;
        // verifico la razón social, debe ser una persona jurídica o una persona física, no ambas o ninguna
        if(current.getPerFisica() != null){
            // no debe haber persona jurídica vinculada
            if(current.getPerJuridica() != null){
                JsfUtil.addErrorMessage("El Establecimiento que está creando, ya tiente una Persona Física asignada como Razón Social, "
                        + "no puede tener además una Persona Jurídica.");
                return null;
            }
        }else if(current.getPerJuridica() != null){
            // no debe haber persona física vinculada
            if(current.getPerFisica() != null){
                JsfUtil.addErrorMessage("El Establecimiento que está creando, ya tiente una Persona Jurpidica asignada como Razón Social, "
                        + "no puede tener además una Persona Física.");
                return null;
            }
        }else{
            JsfUtil.addErrorMessage("El Establecimiento que está creando debe tener una Razón Social.");
            return null;
        }
        
        // verifico que tenga expediente
        if(current.getExpediente() == null){
            JsfUtil.addErrorMessage("El Establecimiento que está creando debe tener un Expediente asignado.");
            return null;
        }
        
        // verifico Actividades y Especialidades solo si no es un Domicilio legal
        if(!current.getTipo().getNombre().equals("Domicilio legal")){
            // verifico que tenga al menos una Actividad vinculada
            if(current.getActividades().isEmpty()){
                JsfUtil.addErrorMessage("El Establecimiento que está creando debe tener al menos una Actividad asociada.");
                return null;
            }

            // verifico que tenga al menos una Especialidad vinculada
            if(current.getEspecialidades().isEmpty()){
                JsfUtil.addErrorMessage("El Establecimiento que está creando debe tener al menos una Especialidad asociada.");
                return null;
            }
        }

        
        // si estoy registrando un domicilio legal, valido que no tenga uno ya
        if(current.getTipo().getNombre().equals("Domicilio legal")){
            Long tempCuit;
            if(current.getPerFisica() != null){
                tempCuit = current.getPerFisica().getCuitCuil();
                if(!getFacade().noExisteDomLegal(tempCuit, false)){
                    JsfUtil.addErrorMessage("El Establecimiento que está creando es de tipo Domicilio legal, pero la razón social asociada "
                            + "ya tiene registrado un Domicilio legal, solo puede haber uno, por favor, actualice los datos del Domicilio "
                                + "legal existente.");
                    return null;
                }
            }
            if(current.getPerJuridica() != null){
                tempCuit = current.getPerJuridica().getCuit();
                if(!getFacade().noExisteDomLegal(tempCuit, true)){
                    JsfUtil.addErrorMessage("El Establecimiento que está creando es de tipo Domicilio legal, pero la razón social asociada "
                            + "ya tiene registrado un Domicilio legal, solo puede haber uno, por favor, actualice los datos del Domicilio "
                                + "legal existente.");
                    return null;
                }
            }
        }
        
        // Creación de la entidad de administración y asignación
        Date date = new Date(System.currentTimeMillis());
        AdminEntidad admEnt = new AdminEntidad();
        admEnt.setFechaAlta(date);
        admEnt.setHabilitado(true);
        admEnt.setUsAlta(usLogeado);
        current.setAdmin(admEnt);

        //Actualizo domicilio
        if(localSelected != null){
            domicilio.setIdLocalidad(localSelected.getId());
            domicilio.setLocalidad(localSelected.getNombre());
        }

        domicilio.setDepartamento(deptoSelected.getNombre());
        domicilio.setProvincia(provSelected.getNombre());
        
        current.setDomicilio(domicilio);
        
        // verifico si hay otros Establecimientos del mismo tipo en el mismo domicilio. En cuyo caso seteo un alerta
        listEst = getFacade().getExistente(domicilio.getCalle(), domicilio.getNumero(), domicilio.getIdLocalidad(), current.getTipo());
        if(listEst.isEmpty()){
            current.setAlertaDomicilio(false);
        }else if(listEst.size() == 1){
            current.setAlertaDomicilio(!listEst.get(0).getId().equals(current.getId()));
        }else{
            current.setAlertaDomicilio(true);
        }
    
        // si todo está en condiciones creo el Establecimiento
        try{
            // Inserción
            getFacade().create(current);
            asignaDisp = false;

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoCreated"));
            limpiarListados();
            limpiarEntitadesSrv();
            return "view";
        } 
        catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("EstablecimientoCreatedErrorOccured"));
            return null;
        }
    }
    
    /**
     * Método para registrar un nuevo Expediente
     */
    public void createExp(){
        Expediente tempExp;
        if(esRazonSocial) tempExp = expRazSoc;
        else tempExp = expediente;
                
        if(tempExp.getNumero() < 1){
            JsfUtil.addErrorMessage("El N° del Expediente no puede ser menor a 1.");
        }else{
            if(expFacade.noExiste(tempExp.getNumero(), tempExp.getAnio())){
                try{
                    expFacade.create(tempExp);
                    JsfUtil.addSuccessMessage("El Expediente ya ha sido registrado, por favor cierre esta ventana, "
                            + "actualice el listado con el botón adjunto y seleccionelo para asignarlo a la Persona Física");
                }catch(Exception e){
                    JsfUtil.addErrorMessage(e, "Hubo un error registrando el Expediente. " + e.getMessage());
                }
            }else{
                JsfUtil.addErrorMessage("Ya existe un Expediente con el N° y año que está tratando de registrar.");
            }
        }
    }    
    
    /**
     * Método para agregar una Persona Física para luego asitnarla al Establecimiento
     */
    public void createPerFisica(){
        // Creación de la entidad de administración y asignación
        Date date = new Date(System.currentTimeMillis());
        AdminEntidad admEnt = new AdminEntidad();
        admEnt.setFechaAlta(date);
        admEnt.setHabilitado(true);
        admEnt.setUsAlta(usLogeado);
        perFisica.setAdmin(admEnt);
        //Asigno expediente
        perFisica.setExpediente(expRazSoc);
        
        //Actualizo domicilio
        if(localRazSocSelected != null){
            domicilio.setIdLocalidad(localRazSocSelected.getId());
            domicilio.setLocalidad(localRazSocSelected.getNombre());
        }

        domicilio.setDepartamento(deptoRazSocSelected.getNombre());
        domicilio.setProvincia(deptoRazSocSelected.getNombre());
        
        //Asigno domicilio
        perFisica.setDomicilio(domicilio);

        if(perFisica.getNombreCompleto() == null){
            JsfUtil.addErrorMessage("La persona que está guardando debe tener un nombre.");
        }else if(perFisica.getCuitCuil() == 0){
            JsfUtil.addErrorMessage("La persona que está guardando debe tener un CUIT o CUIL.");
        }else{
            try {
                if(perFisicaFacade.noExiste(perFisica.getCuitCuil())){
                    // Inserción
                    perFisicaFacade.create(perFisica);
                    limpiarFormRazSoc();
                    JsfUtil.addSuccessMessage("La Persona Física se creó satisfactoriamente, por favor, cierre la ventana y actualice el listado "
                            + "de Personas Físicas con el botón correspondiente para poder seleccionarla y asignarla al Establecimiento.");
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerFisicaExistente"));
                }
            } 
            catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerFisicaCreatedErrorOccured"));
            }
        }  
    }
    /**
     * Método para agregar una Persona Jurídica para luego asitnarla al Establecimiento
     */
    public void createPerJuridica(){
        // Creación de la entidad de administración y asignación
        Date date = new Date(System.currentTimeMillis());
        AdminEntidad admEnt = new AdminEntidad();
        admEnt.setFechaAlta(date);
        admEnt.setHabilitado(true);
        admEnt.setUsAlta(usLogeado);
        perJuridica.setAdmin(admEnt);
        
        if(perJuridica.getRazonSocial() == null){
            JsfUtil.addErrorMessage("La Persona Jurídica que está guardando debe tener una Razón Social.");
        }else if(perJuridica.getCuit() == 0){
            JsfUtil.addErrorMessage("La Persona Jurícia que está guardando debe tener un CUIT.");
        }else{
            try {
                if(perJuridicaFacade.noExiste(perJuridica.getCuit())){
                    // Inserción
                    perJuridicaFacade.create(perJuridica);
                    resetRazSoc();
                    JsfUtil.addSuccessMessage("La Persona Jurídica se registró satisfactoriamente, por favor, cierre la ventana y actualice "
                            + "el listado mediante el botón correspondiente para seleccionarla y asignarla al Establecimiento.");
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerJuridicaExistente"));
                }
            } 
            catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerJuridicaCreatedErrorOccured"));
            }
        }
    }
    
    
    /**
     * Método para actualizar el listados de expedientes
     */
    public void actualizarExpedientes(){
        listaExpedientes.clear();
        listaExpedientes = expFacade.findAllByOrder();
    }    
    
    /**
     * Método para actualizar el listado de Personas físicas
     */
    public void actualizarPerFisicas(){
        listaPerFisica.clear();
        listaPerFisica = perFisicaFacade.findAll();
    }
    
    /**
     * Método para actualizar el listado de Personas Jurídicas
     */
    public void actualizarPerJuridicas(){
        listaPerJuridica.clear();
        listaPerJuridica = perJuridicaFacade.findAll();
    }

    /**
     * Método que actualiza una nueva Instancia en la base de datos.
     * Previamente actualiza los datos de administración
     * @return mensaje que notifica la actualización
     */
    public String update() {    
        List<Establecimiento> listEst;
        Date date = new Date(System.currentTimeMillis());

        // actualizamos según el valor de update
        if(update == 1){
            current.getAdmin().setFechaBaja(date);
            current.getAdmin().setUsBaja(usLogeado);
            current.getAdmin().setHabilitado(false);
        }
        if(update == 2){
            current.getAdmin().setFechaModif(date);
            current.getAdmin().setUsModif(usLogeado);
            current.getAdmin().setHabilitado(true);
            current.getAdmin().setFechaBaja(null);
            current.getAdmin().setUsBaja(null);
        }
        if(update == 0){
            current.getAdmin().setFechaModif(date);
            current.getAdmin().setUsModif(usLogeado);
            
            // verifico la razón social, debe ser una persona jurídica o una persona física, no ambas o ninguna
            if(current.getPerFisica() != null){
                // no debe haber persona jurídica vinculada
                if(current.getPerJuridica() != null){
                    JsfUtil.addErrorMessage("El Establecimiento que está editando, ya tiente una Persona Física asignada como Razón Social, "
                            + "no puede tener además una Persona Jurídica.");
                    return null;
                }
            }else if(current.getPerJuridica() != null){
                // no debe haber persona física vinculada
                if(current.getPerFisica() != null){
                    JsfUtil.addErrorMessage("El Establecimiento que está editando, ya tiente una Persona Jurpidica asignada como Razón Social, "
                            + "no puede tener además una Persona Física.");
                    return null;
                }
            }else{
                JsfUtil.addErrorMessage("El Establecimiento que está editando debe tener una Razón Social.");
                return null;
            }

            // verifico que tenga expediente
            if(current.getExpediente() == null){
                JsfUtil.addErrorMessage("El Establecimiento que está editando debe tener un Expediente asignado.");
                return null;
            }

            // verifico Actividades y Especialidades solo si no es un Domicilio legal
            if(!current.getTipo().getNombre().equals("Domicilio legal")){
                // verifico que tenga al menos una Actividad vinculada
                if(current.getActividades().isEmpty()){
                    JsfUtil.addErrorMessage("El Establecimiento que está editando debe tener al menos una Actividad asociada.");
                    return null;
                }

                // verifico que tenga al menos una Especialidad vinculada
                if(current.getEspecialidades().isEmpty()){
                    JsfUtil.addErrorMessage("El Establecimiento que está editando debe tener al menos una Especialidad asociada.");
                    return null;
                }
            }

            // si estoy registrando un domicilio legal, valido que no tenga uno ya
            if(current.getTipo().getNombre().equals("Domicilio legal")){
                Long tempCuit;
                if(current.getPerFisica() != null){
                    tempCuit = current.getPerFisica().getCuitCuil();
                    if(!getFacade().noExisteDomLegal(tempCuit, false)){
                        JsfUtil.addErrorMessage("El Establecimiento que está editando es de tipo Domicilio legal, pero la razón social asociada "
                                + "ya tiene registrado un Domicilio legal, solo puede haber uno, por favor, actualice los datos del Domicilio "
                                + "legal existente.");
                        return null;
                    }
                }
                if(current.getPerJuridica() != null){
                    tempCuit = current.getPerJuridica().getCuit();
                    if(!getFacade().noExisteDomLegal(tempCuit, true)){
                        JsfUtil.addErrorMessage("El Establecimiento que está editando es de tipo Domicilio legal, pero la razón social asociada "
                                + "ya tiene registrado un Domicilio legal, solo puede haber uno, por favor, actualice los datos del Domicilio "
                                + "legal existente.");
                        return null;
                    }
                }
            }
        }
        // acualizo según la operación seleccionada
        try {
            if(update == 0){
                // verifico si hay otros Establecimientos del mismo tipo en el mismo domicilio. En cuyo caso seteo un alerta
                listEst = getFacade().getExistente(current.getDomicilio().getCalle(), current.getDomicilio().getNumero(), current.getDomicilio().getIdLocalidad(), current.getTipo());
                if(listEst.isEmpty()){
                    current.setAlertaDomicilio(false);
                }else if(listEst.size() == 1){
                    current.setAlertaDomicilio(!listEst.get(0).getId().equals(current.getId()));
                }else{
                    current.setAlertaDomicilio(true);
                }

                //Actualizo domicilio
                if(localSelected != null){
                    current.getDomicilio().setIdLocalidad(localSelected.getId());
                    current.getDomicilio().setLocalidad(localSelected.getNombre());
                }

                current.getDomicilio().setDepartamento(deptoSelected.getNombre());
                current.getDomicilio().setProvincia(provSelected.getNombre());
                
                // creo un string que me vaya guardando los pasos por la transacción
                String estTansac = "";

                // si todo está en condiciones edito el Establecimiento
                try{
                    // primero edito
                    getFacade().edit(current);
                    estTansac = estTansac + "Update Establecimiento: Ok. ";
                    
                    // luego verifico que no haya habido cambio de razón social
                    if(!verificarCambioRazonSocial()){
                        ReasignaRazonSocial rsAnterior = new ReasignaRazonSocial();
                        Date dt = new Date(System.currentTimeMillis());
                        // leo el último cambio de rs si lo hubiera
                        if(reasignaFacade.getUltimaActiva(current) != null){
                            rsAnterior = reasignaFacade.getUltimaActiva(current);
                        }
                        if(current.getPerJuridica() != null)rsNueva.setPerJuridica(current.getPerJuridica());
                        else rsNueva.setPerFisica(current.getPerFisica());
                        rsNueva.setEstablecimiento(current);
                        rsNueva.setFecha(dt);
                        rsNueva.setActiva(true);
                        rsNueva.setUsuario(usLogeado);

                        // si hubo una anterior la apago y seteo la razón social anterior
                        if(rsAnterior.getId() != null){
                            rsNueva.setExPerJuridica(rsAnterior.getPerJuridica());
                            rsNueva.setExPerFisica(rsAnterior.getPerFisica());
                            rsAnterior.setActiva(false);
                            reasignaFacade.edit(rsAnterior);
                            estTansac = estTansac + "Update Reasignación anterior: Ok. ";
                        }else{
                            // si no hubo asignación anterior seteo las ex con los valores temporales
                            rsNueva.setExPerJuridica(tempPerJuridica);
                            rsNueva.setExPerFisica(tempPerFisica);
                        }
                        // agrego la nueva como activa
                        reasignaFacade.create(rsNueva);
                        estTansac = estTansac + "Insert Reasignación nueva: Ok.";
                    }
                    
                    asignaDisp = false;
                    rsNueva = null;

                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoUpdated") + " " + estTansac);
                    limpiarListados();
                    limpiarEntitadesSrv();
                    return "view";
                } 
                catch (Exception e) {
                    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("EstablecimientoUpdatedErrorOccured")  + " " + estTansac);
                    return null;
                }
                
            }else if(update == 1){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoDeshabilitado"));
                asignaDisp = false;
                return "view";
            }else{
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoHabilitado"));
                asignaDisp = false;
                return "view";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Establecimiento", "Existe un error en la edición"));
            return null;
        }
    } 

    /**
     * Método para asignar una Especialidad al Establecimiento.
     * @param esp
     */
    public void asignarEspecialidad(Especialidad esp){
        current.getEspecialidades().add(esp);
        listEspDisp.remove(esp);
        if(listEspFilter != null){
            listEspFilter.clear();
        }
    }        
    
    /**
     * Método para asignar una Actividad al Establecimiento.
     * @param act
     */
    public void asignarActividad(Actividad act){
        current.getActividades().add(act);
        listActDisp.remove(act);
        if(listActFilter != null){
            listActFilter.clear();
        }
    }      
    
    /**
     * Método para desvincular una Especialidad al Establecimiento
     * @param esp
     */
    public void quitarEspecialidad(Especialidad esp){
        current.getEspecialidades().remove(esp);
        listEspDisp.add(esp);
        if(listEspFilter != null){
            listEspFilter.clear();
        }
    }       
    
    /**
     * Método para desvincular una Especialidad al Establecimiento
     * @param act
     */
    public void quitarActividad(Actividad act){
        current.getActividades().remove(act);
        listActDisp.add(act);
        if(listActFilter != null){
            listActFilter.clear();
        }
    }           
    
    
    /**************************
     ** Métodos de selección **
     **************************/ 
    /**
     * @param id equivalente al id de la entidad persistida
     * @return la entidad correspondiente
     */
    public Establecimiento getEstablecimiento(java.lang.Long id) {
        return getFacade().find(id);
    }  
    
    /**
     * Método para ver el listado de Especialidades disponibles para asignar al Establecimiento
     */
    public void verEspDisp(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
        RequestContext.getCurrentInstance().openDialog("dlgEspDisp", options, null);
    }  
    
    /**
     * Método para ver el listado de Especialidades vinculadas al Establecimiento
     */
    public void verEspVinc(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
        RequestContext.getCurrentInstance().openDialog("dlgEspVinc", options, null);
    }     
    
    /**
     * Método para ver el listado de Actividades disponibles para asignar al Establecimiento
     */
    public void verActDisp(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
        RequestContext.getCurrentInstance().openDialog("dlgActDisp", options, null);
    }  
    
    /**
     * Método para ver el listado de Actividades vinculadas al Establecimiento
     */
    public void verActVinc(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
        RequestContext.getCurrentInstance().openDialog("dlgActVinc", options, null);
    }    
    
    /**
     * Método para mostrar el historial de cambio de razones sociales para un establecimiento
     */
    public void verRsAnteriores(){
        listHist = reasignaFacade.getHistorial(current);

        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 900);
        RequestContext.getCurrentInstance().openDialog("dlgHistorialRazonesSociales", options, null);
    }
    
    /**
     * Método para abrir el diálogo para cambiar la razón social del Establecimiento
     */
    public void actualizarRazonSocial(){
        esFisica = true;
        esJuridica = true;     
        rsNueva = new ReasignaRazonSocial();
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 900);
        options.put("contentHeight", 800);
        RequestContext.getCurrentInstance().openDialog("dlgEditRazonSocial", options, null);
    }
    
    /**
     * Método para abrir el formulario para registrar una nueva Persona Física
     */
    public void addPerFisica(){
        esRazonSocial = true;
        perFisica = new PerFisica();
        domicilio = new Domicilio();
        esFisica = true;
        esJuridica = false;
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 800);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("dlgAddPerFisica", options, null);
    }        
    
    /**
     * Método para abrir el formulario para registrar una nueva Persona Juridica
     */
    public void addPerJuridica(){
        esRazonSocial = true;
        esFisica = false;
        esJuridica = true;
        perJuridica = new PerJuridica();
        listaTipoPersonaJuridica = tipoPerJurFacade.findAllByNombre();
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 800);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("dlgAddPerJuridica", options, null);
    }        

    /**
     * Método para procesar el pdf
     * @param document
     * @throws DocumentException
     * @throws IOException 
     */
    public void preProcessPDF(Object document) throws DocumentException, IOException {
        Document pdf = (Document) document;    
        pdf.open();
        pdf.setPageSize(PageSize.A4.rotate());
        pdf.newPage();
    }        
    
    /**
     * Método para limpiar todo el formulario new
     */
    public void limpiarForm(){
        limpiarRazonSocial();
        limpiarPerfil();
        limpiarEntitadesSrv();
    }
    
    /**
     * Método para restaurar los campos del formulario de edición
     */
    public void restForm(){
        restaurarRazonSocial();
        restaurarPerfil();
        cargarEntidadesSrv();
    }    
    
    /**
     * Método para limpiar todo el formulario new
     */
    public void limpiarFormRazSoc(){
        if(domicilio != null) domicilio = new Domicilio();
        limpiarCuit();
        limpiarEntitadesSrvRazSoc();
    }    
 
    /**
     * Método para abrir el diálogo para validar cuit 
     */
    public void prepareValidarCuit(){
        // seteo el objeto que recibirá los resultados de la validación
        personaAfip = new CuitAfip();
        cuit = Long.valueOf(0);
        
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 700);
        options.put("contentHeight", 250);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("dlgValidarCuit", options, null);
    }

    
    /**
     * Método que consume el servicio de validación de cuit
     * Recibe un cuit y retorna los datos de la persona asociada, en caso de ser válido.
     * Los datos recibidos los guarda en el campo personaAfip para mostrarlo al usuario.
     */
    public void validarCuit(){
        try { 
            CuitAfipWs port = srvCuitAfip.getCuitAfipWsPort();
            personaAfip = port.verPersona(cuit);
            
            // Si valida seteo los datos correspondientes de la persona (según el caso)
            if(esFisica){
                perFisica.setCuitCuil(personaAfip.getPejID());
                perFisica.setNombreCompleto(personaAfip.getPejRazonSocial());
            }else if(esJuridica){
                perJuridica.setCuit(personaAfip.getPejID());
                perJuridica.setRazonSocial(personaAfip.getPejRazonSocial());
            }

            JsfUtil.addSuccessMessage("El CUIT ingresado fue validado con exito, puede cerrar la ventana. Luego actualice los datos personales");
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"), ex.getMessage()});
        }
    }   
    
    /**
     * Método para validar que el año ingresado tenga un formato válido
     * @param arg0: vista jsf que llama al validador
     * @param arg1: objeto de la vista que hace el llamado
     * @param arg2: contenido del campo de texto a validar 
     */
    public void validarAnio(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException{
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        int anioMinimo = anioActual - 20;
        if((int)arg2 > anioActual){
            throw new ValidatorException(new FacesMessage("El año ingresado no puede ser mayor que el año actual = " + anioActual));
        }else{
            if((int)arg2 < anioMinimo){
                throw new ValidatorException(new FacesMessage("El año ingresado no puede ser menor a 20 años antes del año actual = " + anioMinimo));
            }
        }
    }       
    
    /**
     * Método para limpiar los datos AFIP seleccionado
     */
    public void limpiarCuit(){
        personaAfip = null;
        personaAfip = new CuitAfip();
        if(esFisica){
            perFisica.setNombreCompleto("");
            perFisica.setCuitCuil(Long.valueOf(0));
        }else if(esJuridica){
            perJuridica.setCuit(Long.valueOf(0));
            perJuridica.setRazonSocial("");
        }
    }   
    
    /**
     * Método para actualizar los datos del formulario de cambio de razón social
     */
    public void actualizarEditRazonSocial(){
        JsfUtil.addSuccessMessage("La Razón Social ha sido actualizada, por favor cierre esta ventana y "
                + "actualice los datos en el formulario principal mediante el botón correspondiente");
    }
    
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private EstablecimientoFacade getFacade() {
        return establecimientoFacade;
    }

    
    /**
     * Restea la entidad
     */
    private void limpiarListados() {
        if(listado != null){
            listado.clear();
            listado = null;
        }     
        if(listadoFilter != null){
            listadoFilter.clear();
            listadoFilter = null;
        }   
        if(listaPerJuridica != null) listaPerJuridica.clear();
        if(listaPerFisica != null) listaPerFisica.clear();   
        if(listaEstado != null) listaEstado.clear();   
        if(listaTipoEstablecimiento != null) listaTipoEstablecimiento.clear();
        if(listaExpedientes != null) listaExpedientes.clear();
        if(listEspDisp != null) listEspDisp.clear();
        if(listEspFilter != null) listEspFilter.clear();
        if(listActDisp != null) listActDisp.clear();
        if(listActFilter != null) listActFilter.clear();
        
        if(listProvincias != null) listProvincias = null;
        if(listDepartamentos != null) listDepartamentos = null;
        if(listLocalidades != null) listLocalidades = null;
    } 
    
    /**
     * Método para poblar el listado de provincias del servicio de centros poblados
     */
    private void getProvinciasSrv(){
        EntidadServicio provincia;
        List<Provincia> listSrv;
        try {
            CentrosPobladosWebService port = srvCentrosPob.getCentrosPobladosWebServicePort();
            
            // lleno el listado de provincias
            listSrv = port.verProvincias();
            
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();

            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
                //provincia = null;
            }
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetProvError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetProvError"), ex.getMessage()});
        }
    }
    
    /**
     * Método para poblar el listado de departamentos del servicio de centros poblados
     */
    private void getDepartamentosSrv(Long idProv){
        EntidadServicio depto;
        List<Departamento> listSrv;
        try { 
            CentrosPobladosWebService port = srvCentrosPob.getCentrosPobladosWebServicePort();
            listSrv = port.buscarDeptosPorProvincia(idProv);
            
            // lleno el list con los Departamentos (según corresponda) como un objeto Entidad Servicio
            if(esRazonSocial){
                listDepRazSoc = new ArrayList<>();
                for(Departamento dpt : listSrv){
                    depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                    listDepRazSoc.add(depto);
                }
            }
            else{
                listDepartamentos = new ArrayList<>();
                for(Departamento dpt : listSrv){
                    depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                    listDepartamentos.add(depto);
                }
            }
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetDeptosError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetDeptosError"), ex.getMessage()});
        }
    }

    /**
     * Método para poblar el listado de Localidades del servicio de centros poblados
     */
    private void getLocalidadesSrv(Long idDepto){
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        try { 
            CentrosPobladosWebService port = srvCentrosPob.getCentrosPobladosWebServicePort();
            listSrv = port.buscarCentrosPorDepto(idDepto);
            
            // lleno el list con las Localidades (según corresponda) como un objeto Entidad Servicio
            if(esRazonSocial){
                listLocRazSoc = new ArrayList<>();
                for(CentroPoblado loc : listSrv){
                    local = new EntidadServicio(loc.getId(), loc.getNombre());
                    listLocRazSoc.add(local);
                }
            }else{
                listLocalidades = new ArrayList<>();
                for(CentroPoblado loc : listSrv){
                    local = new EntidadServicio(loc.getId(), loc.getNombre());
                    listLocalidades.add(local);
                }
            }
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetLocalError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetLocalError"), ex.getMessage()});
        }
    }    
    
    
    /**
     * Método para limpiar las entidades del servicio
     */
    private void limpiarEntitadesSrv(){
        if(provSelected != null){
            provSelected = null;
        }
        if(deptoSelected != null){
            deptoSelected = null;
        }
        if(localSelected != null){
            localSelected = null;
        }
    }
    
    /**
     * Método para limpiar las entidades del servicio para razones sociales
     */
    private void limpiarEntitadesSrvRazSoc(){
        if(provRazSocSelected != null){
            provRazSocSelected = null;
        }
        if(deptoRazSocSelected != null){
            deptoRazSocSelected = null;
        }
        if(localRazSocSelected != null){
            localRazSocSelected = null;
        }
    }    

    /**
     * Método para cargar entidades de servicio y los listados, para actualizar la Persona
     */
    private void cargarEntidadesSrv(){
        CentrosPobladosWebService port = srvCentrosPob.getCentrosPobladosWebServicePort();
        CentroPoblado cp;
        List<Provincia> listProv;
        List<Departamento> listDeptos;
        List<CentroPoblado> listLocal;
        EntidadServicio provincia;
        EntidadServicio depto;
        EntidadServicio local;
        
        try{
            // obtengo el CentroPoblado a partir del idLocalidad del domicilio de la Persona y seteo la EntidadServicio correspondiente
            cp = port.buscarCentroPoblado(current.getDomicilio().getIdLocalidad());
            localSelected = new EntidadServicio(cp.getId(), cp.getNombre());

            // del CentroPoblado obtengo el Departamento y seteo la EntidadServicio correspondiente
            deptoSelected = new EntidadServicio(cp.getDepartamento().getId(), cp.getDepartamento().getNombre());

            // del Departamento del CentroPoblado obtengo la Provincia y seteo la EntidadServicio correspondiente
            provSelected = new EntidadServicio(cp.getDepartamento().getProvincia().getId(), cp.getDepartamento().getProvincia().getNombre());

            // cargo el listado de Provincias
            listProv = port.verProvincias();
            listProvincias = new ArrayList<>();
            for(Provincia prov : listProv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);                    
            }
            
            // lleno los Departamentos según la provincia que tiene asignada la persona
            listDeptos = port.buscarDeptosPorProvincia(provSelected.getId());
            listDepartamentos = new ArrayList<>();
            for(Departamento dpt : listDeptos){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
            }
            
            // lleno las Localidades según el Departamento que tiene asignado la persona
            listLocal = port.buscarCentrosPorDepto(deptoSelected.getId());
            listLocalidades = new ArrayList<>();
            for(CentroPoblado loc : listLocal){
                local = new EntidadServicio(loc.getId(), loc.getNombre());
                listLocalidades.add(local);
            }
            
        }catch(Exception ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetProvError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaGetProvError"), ex.getMessage()});
        }
    }    
    
    /**
     * Método para llenar los listados de elementos disponibles para su asignación
     */
    private void cargarListadosDisp(){
        cargarEspDisponibles();
        cargarActDisponibles();
    }    
    
    /**
     * Mátodo para cargar los Docentes disponibles para asignarlos a un Establecimiento
     */
    private void cargarEspDisponibles(){
        if(!current.getEspecialidades().isEmpty()){
            listEspDisp = new ArrayList<>();
            List<Especialidad> lEsp = espFacade.findAllByNombre();
            Iterator itEsp = lEsp.listIterator();
            while(itEsp.hasNext()){
                Especialidad esp = (Especialidad)itEsp.next();
                if(!current.getEspecialidades().contains(esp)){
                    listEspDisp.add(esp);
                }
            }     
        }else{
            listEspDisp = espFacade.findAllByNombre();
        }
    }    
    
    /**
     * Mátodo para cargar los Docentes disponibles para asignarlos a un Establecimiento
     */
    private void cargarActDisponibles(){
        if(!current.getActividades().isEmpty()){
            listActDisp = new ArrayList<>();
            List<Actividad> lAct = actividadFacade.findAllByNombre();
            Iterator itAct = lAct.listIterator();
            while(itAct.hasNext()){
                Actividad act = (Actividad)itAct.next();
                if(!current.getActividades().contains(act)){
                    listActDisp.add(act);
                }
            }     
        }else{
            listActDisp = actividadFacade.findAllByNombre();
        }
    }      
    
    /**
     * Método para resetear los campos asociados al registro de nuevas razones sociales
     */
    private void resetRazSoc(){
        limpiarEntitadesSrvRazSoc();
        listDepRazSoc.clear();
        listLocRazSoc.clear();
        listaTipoPersonaJuridica.clear();
        perJuridica = null;
        perFisica = null;
        expRazSoc = null;
        esFisica = true;
        esJuridica = true;
    }
    
    /**
     * Metodo para guardar temporalmente los datos del Perfil recibidos al abrir el formulario de edición
     */
    private void guardarTempPerfil(){
        listEspVinc = current.getEspecialidades();
        listActVinc = current.getActividades();
    }    
    
    /**
     * Metodo para guardar temporalmente los datos de la razón social recibidos al abrir el formulario de edición
     */
    private void guardarTempRazonSocial(){
        tempPerJuridica = current.getPerJuridica();
        tempPerFisica = current.getPerFisica();
    }
    
    /**
     * Método para verificar si hubo cambio de razón social
     */
    private boolean verificarCambioRazonSocial(){
        if(tempPerJuridica != null){
            return tempPerJuridica.equals(current.getPerJuridica());
        }else if(tempPerFisica != null){
            return tempPerFisica.equals(current.getPerFisica());
        }else{
            return false;
        }
    }
    
    /**
     * Método para limpiar los datos de la razón social que se hubieran cargado en el formulario new
     */
    private void limpiarRazonSocial(){
        esFisica = true;
        esJuridica = true;
    }
    
    /**
     * Método para restaurar la razón social a los valores recibidos al abrir el formulario de edición
     */
    private void restaurarRazonSocial(){
        current.setPerFisica(tempPerFisica);
        current.setPerJuridica(tempPerJuridica);
    }
    
    /**
     * Método para limpiar los datos del perfil seleccinados
     */
    private void limpiarPerfil(){
        if(listEspVinc != null) listEspVinc.clear();
        if(listActVinc != null) listActVinc.clear();
        
        current.setActividades(listActVinc);
        current.setEspecialidades(listEspVinc);

        listEspDisp = espFacade.findAllByNombre();
        listActDisp = actividadFacade.findAllByNombre();
    } 
    
    /**
     * Método para restaurar los datos del perfil recibidos al abrir el formulario de edición
     */
    private void restaurarPerfil(){
        current.setEspecialidades(listEspVinc);
        current.setActividades(listActVinc);
    }     
    

    /*********************
    ** Desencadenadores **
    **********************/    
    
    /**
     * Método para actualizar el combo de búsqueda de razón social en personas físicas
     * @param event
     */
    public void fisicaChangeListener(ValueChangeEvent event){
        PerFisica perFis = (PerFisica)event.getNewValue();
        if(perFis != null){
            esFisica = false;
            esJuridica = true;
        }else{
            current.setPerFisica(null);
            esFisica = true;
            esJuridica = false;
        }
    }
    
    /**
     * Método para actualizar el combo de búsqueda de razón social en personas jurídicas
     * @param event
     */
    public void juridicaChangeListener(ValueChangeEvent event){
        PerJuridica perJur = (PerJuridica)event.getNewValue();
        if(perJur != null){
            esFisica = true;
            esJuridica = false;
        }else{
            current.setPerJuridica(null);
            esFisica = false;
            esJuridica = true;
        }
    }
    
    /**
     * Método para actualizar el listado de departamentos según la provincia seleccionada
     * @param esRazSoc
     */    
    public void provinciaChangeListener(){  
        if(esRazonSocial) getDepartamentosSrv(provRazSocSelected.getId());
        else getDepartamentosSrv(provSelected.getId());
    }   
    
    /**
     * Método para actualizar el listado de localidades según el departamento seleccionado
     * @param esRazSoc
     */    
    public void deptoChangeListener(){
        if(esRazonSocial) getLocalidadesSrv(deptoRazSocSelected.getId());
        else getLocalidadesSrv(deptoSelected.getId());
    }     
        
    
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
    @FacesConverter(forClass = Establecimiento.class)
    public static class EstablecimientoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbEstablecimiento controller = (MbEstablecimiento) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbEstablecimiento");
            return controller.getEstablecimiento(getKey(value));
        }

        
        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
       
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Establecimiento) {
                Establecimiento o = (Establecimiento) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Establecimiento.class.getName());
            }
        }
    }        
}

