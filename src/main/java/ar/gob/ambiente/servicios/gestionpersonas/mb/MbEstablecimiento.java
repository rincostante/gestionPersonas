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
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
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
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentroPoblado;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService_Service;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Departamento;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Provincia;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.event.ValueChangeEvent;
import javax.xml.ws.WebServiceRef;
import org.primefaces.context.RequestContext;

/**
*
* @author rodriguezn
*/
public class MbEstablecimiento implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CentrosPobladosWebService/CentrosPobladosWebService.wsdl")
    private CentrosPobladosWebService_Service srvCentrosPob;    
    private static final Logger logger = Logger.getLogger(PerFisica.class.getName());
    
    private Establecimiento current;    
    private Domicilio domicilio;
    private Domicilio domVinc;
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
    
    // listados provistos por el servicio de centros poblados
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;    
    
    // campos para los agregados múltiples
    private List<Especialidad> listEspDisp;
    private List<Especialidad> listEspFilter;
    private List<Actividad> listActDisp;
    private List<Actividad> listActFilter;
    private boolean asignaDisp; 
    
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

    public Domicilio getDomVinc() {
        return domVinc;
    }

    public void setDomVinc(Domicilio domVinc) {
        this.domVinc = domVinc;
    }

    public List<Establecimiento> getListadoFilter() {
        return listadoFilter;
    }

    public void setListaFilter(List<Establecimiento> listadoFilter) {
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
        return "list";
    } 

    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        asignaDisp = false;
        domVinc = current.getDomicilio();
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
        asignaDisp = true;
        domVinc = current.getDomicilio();
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
        return "edit";
    }
    
    /**
     * Método que muestra el diálogo para registrar un nuevo expediente
     */
    public void prepareRegExp(){
        expediente = new Expediente();
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 500);
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
        // Creación de la entidad de administración y asignación
        Date date = new Date(System.currentTimeMillis());
        AdminEntidad admEnt = new AdminEntidad();
        admEnt.setFechaAlta(date);
        admEnt.setHabilitado(true);
        admEnt.setUsAlta(usLogeado);
        current.setAdmin(admEnt);
        //Asigno domicilio
        current.setDomicilio(domicilio);

        //current.setExpedientes(listExpedientes);
        //current.setDomicilios(listaDomicilios);
        getFacade().create(current);
        asignaDisp = false;
        return "view";
    
       /* if(current.getNombre().isEmpty()){
            JsfUtil.addSuccessMessage("La persona que está guardando debe tener un nombre.");
            return null;
        }else{
            try {
                if(getFacade().noExiste(current.getDni())){

                    // Inserción
                    getFacade().create(current);

                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoCreated"));
                   // limpiarListados();
                    return "view";

                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreateEstablecimientoExistente"));
                    return null;
                }
            } 
            catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("EstablecimientoCreatedErrorOccured"));
                return null;
            }
        }
        */
    }
    
    /**
     * Método para registrar un nuevo Expediente
     */
    public void createExp(){
        if(expediente.getNumero() < 1){
            JsfUtil.addErrorMessage("El N° del Expediente no puede ser menor a 1.");
        }else{
            if(expFacade.noExiste(expediente.getNumero(), expediente.getAnio())){
                try{
                    expFacade.create(expediente);
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
     * Método para actualizar el listados de expedientes
     */
    public void actualizarExpedientes(){
        listaExpedientes.clear();
        listaExpedientes = expFacade.findAllByOrder();
    }    

    /**
     * Método que actualiza una nueva Instancia en la base de datos.
     * Previamente actualiza los datos de administración
     * @return mensaje que notifica la actualización
     */
    public String update() {    
        boolean edito;
        Establecimiento establecimiento;
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
            current.getAdmin().setUsBaja(usLogeado);
        }
        if(update == 0){
            current.getAdmin().setFechaModif(date);
            current.getAdmin().setUsModif(usLogeado);
        }
        // acualizo según la operación seleccionada
        try {
            if(update == 0){
                //establecimiento = getFacade().getExistente(current.getDomicilio(), current.getActividad());
                /**
                 * Modificar el método getExistente() en el facade
                 */
                establecimiento = null;
                if(establecimiento == null){
                    edito = true;  
                }else{
                    edito = establecimiento.getId().equals(current.getId());
                }
                if(edito){
                    // Actualización de datos de administración de la entidad
                    current.getAdmin().setFechaModif(date);
                    current.getAdmin().setUsModif(usLogeado); 

                    // Actualizo
                    getFacade().edit(current);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Establecimiento", "Ha sido actualizado"));
                    asignaDisp = false;
                    return "view";
                }else{
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Establecimiento", "Ya Existe"));
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
     * Método para abrir el formulario para registrar una nueva Persona Física
     */
    public void addPerFisica(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 800);
        RequestContext.getCurrentInstance().openDialog("dlgAddPerFisica", options, null);
    }        
    
    /**
     * Método para abrir el formulario para registrar una nueva Persona Juridica
     */
    public void addPerJuridica(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 800);
        RequestContext.getCurrentInstance().openDialog("dlgAddPerJuridica", options, null);
    }        


    /****************************
     * Métodos de validación
     ****************************/    
    
    /**
     * Método para validar que no exista ya una entidad con este nombre al momento de crearla
     * @param arg0: vista jsf que llama al validador
     * @param arg1: objeto de la vista que hace el llamado
     * @param arg2: contenido del campo de texto a validar 
     */
    public void validarInsert(FacesContext arg0, UIComponent arg1, Object arg2){
        validarExistente(arg2);
    }
    
    /**
     * Método para validar que no exista una entidad con este nombre, siempre que dicho nombre no sea el que tenía originalmente
     * @param arg0: vista jsf que llama al validador
     * @param arg1: objeto de la vista que hace el llamado
     * @param arg2: contenido del campo de texto a validar 
     * @throws ValidatorException 
     */
    public void validarUpdate(FacesContext arg0, UIComponent arg1, Object arg2){
        if(!current.getDomicilio().equals((String)arg2)){
            validarExistente(arg2);
        }
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
 
    
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private EstablecimientoFacade getFacade() {
        return establecimientoFacade;
    }

        
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste((long)arg2)){
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreateEstablecimientoExistente")));
        }
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
            
            // lleno el list con los Departamentos como un objeto Entidad Servicio
            listDepartamentos = new ArrayList<>();
            
            for(Departamento dpt : listSrv){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
                //depto = null;
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
            
            // lleno el list con los Departamentos como un objeto Entidad Servicio
            listLocalidades = new ArrayList<>();
            
            for(CentroPoblado loc : listSrv){
                local = new EntidadServicio(loc.getId(), loc.getNombre());
                listLocalidades.add(local);
                //local = null;
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
     * Mátodo para cargar los Docentes disponibles para asignarlos a una Actividad Dispuesta
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
     * Mátodo para cargar los Docentes disponibles para asignarlos a una Actividad Dispuesta
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
     */    
    public void provinciaChangeListener(){     
        getDepartamentosSrv(provSelected.getId());
    }   
    
    /**
     * Método para actualizar el listado de localidades según el departamento seleccionado
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
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

