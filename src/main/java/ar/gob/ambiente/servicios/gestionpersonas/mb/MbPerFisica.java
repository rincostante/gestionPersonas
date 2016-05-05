/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.AdminEntidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.ReasignaRazonSocial;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.EntidadServicio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.DomicilioFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ReasignaRazonSocialFacade;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentroPoblado;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
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
import javax.faces.context.ExternalContext;
import javax.xml.ws.WebServiceRef;
import org.primefaces.context.RequestContext;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs_Service;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfip;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService_Service;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.CentrosPobladosWebService;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Provincia;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.centrosPoblados.Departamento;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
*
* @author rodriguezn
*/
public class MbPerFisica implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/vmdeswebjava.medioambiente.gov.ar_8080/CentrosPobladosWebService/CentrosPobladosWebService.wsdl")
    private CentrosPobladosWebService_Service srvCentrosPob;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/vmdeswebjava.medioambiente.gov.ar_8080/CuitAfipWs/CuitAfipWs.wsdl")
    private CuitAfipWs_Service srvCuitAfip;
    
    private PerFisica current;
    private Domicilio domicilio;
    private Expediente expediente;
    
    private List<PerFisica> listPerFisica;
    private Domicilio domVinc;
    
    @EJB
    private PerFisicaFacade perFisicaFacade;
    @EJB
    private ExpedienteFacade expedienteFacade;
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private DomicilioFacade domicilioFacade;
    @EJB
    private ReasignaRazonSocialFacade reasignaFacade;    
    
    private PerFisica perFisicaSelected;
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private List<Estado> listaEstado;
    private List<Expediente> listExpedientes;
    private CuitAfip personaAfip;
    private static final Logger logger = Logger.getLogger(PerFisica.class.getName());
    private Long cuit;
    
    // listados provistos por el servicio de centros poblados
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;
    
    // listados para ver los historiales de razón social
    private List<ReasignaRazonSocial> listEstAdquiridos;
    private List<ReasignaRazonSocial> listEstCedidos; 
    
    /**
     * Creates a new instance of MbPerFisica
     */
    public MbPerFisica() {
    }
    
    /****************************
     * Métodos de inicialización
     ****************************/
    /**
     * Método que se ejecuta luego de perFisicada la clase e inicializa los datos del usuario
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
    public List<ReasignaRazonSocial> getListEstAdquiridos() {
        return listEstAdquiridos;
    }

    public void setListEstAdquiridos(List<ReasignaRazonSocial> listEstAdquiridos) {
        this.listEstAdquiridos = listEstAdquiridos;
    }

    public List<ReasignaRazonSocial> getListEstCedidos() {
        return listEstCedidos;
    }

    public void setListEstCedidos(List<ReasignaRazonSocial> listEstCedidos) {
        this.listEstCedidos = listEstCedidos;
    }

    public List<Expediente> getListExpedientes() {
        return listExpedientes;
    }

    public void setListExpedientes(List<Expediente> listExpedientes) {
        this.listExpedientes = listExpedientes;
    }

    public List<EntidadServicio> getListProvincias() {
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<EntidadServicio> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDeptSrv(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public List<EntidadServicio> getListCentrosSrv() {
        return listLocalidades;
    }

    public void setListCentrosSrv(List<EntidadServicio> listCentrosSrv) {
        this.listLocalidades = listCentrosSrv;
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

    public PerFisica getCurrent() {
        return current;
    }

    public void setCurrent(PerFisica current) {
        this.current = current;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public List<PerFisica> getListPerFisica() {
        if(listPerFisica == null){
            listPerFisica = getFacade().findAll();
        }
        return listPerFisica;
    }

    public void setListPerFisica(List<PerFisica> listPerFisica) {
        this.listPerFisica = listPerFisica;
    }

    public Domicilio getDomVinc() {
        return domVinc;
    }

    public void setDomVinc(Domicilio domVinc) {
        this.domVinc = domVinc;
    }

    public PerFisica getPerFisicaSelected() {
        return perFisicaSelected;
    }

    public void setPerFisicaSelected(PerFisica perFisicaSelected) {
        this.perFisicaSelected = perFisicaSelected;
    }

    public MbLogin getLogin() {
        return login;
    }

    public void setLogin(MbLogin login) {
        this.login = login;
    }

    public Usuario getUsLogeado() {
        return usLogeado;
    }

    public void setUsLogeado(Usuario usLogeado) {
        this.usLogeado = usLogeado;
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public List<Estado> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<Estado> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public PerFisicaFacade getPerFisicaFacade() {
        return perFisicaFacade;
    }

    public void setPerFisicaFacade(PerFisicaFacade perFisicaFacade) {
        this.perFisicaFacade = perFisicaFacade;
    }

    public ExpedienteFacade getExpedienteFacade() {
        return expedienteFacade;
    }

    public void setExpedienteFacade(ExpedienteFacade expedienteFacade) {
        this.expedienteFacade = expedienteFacade;
    }

    public EstadoFacade getEstadoFacade() {
        return estadoFacade;
    }

    public void setEstadoFacade(EstadoFacade estadoFacade) {
        this.estadoFacade = estadoFacade;
    }

    public DomicilioFacade getDomicilioFacade() {
        return domicilioFacade;
    }

    public void setDomicilioFacade(DomicilioFacade domicilioFacade) {
        this.domicilioFacade = domicilioFacade;
    }
    
 
    /********************************
     ** Métodos para el datamodel **
     ********************************/
    public PerFisica getSelected() {
        if (current == null) {
            current = new PerFisica();
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
        recreateModel();
        limpiarHisEst();
        return "list";
    }

    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        domVinc = current.getDomicilio();
        getEstCedidos();
        getEstAdquiridos();
        return "view";
    }

    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        limpiarEntitadesSrv();
        //Se instancia current
        current = new PerFisica();      
        //Inicializamos la creacion de domicilio
        domicilio = new Domicilio();
        listaEstado = estadoFacade.findAll();
        listExpedientes = expedienteFacade.findAllByOrder();
        
        // cargo el listado de Provincias
        getProvinciasSrv();
        return "new";
    }
    
   /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        domVinc = current.getDomicilio();
        listaEstado = estadoFacade.findAll();
        listExpedientes = expedienteFacade.findAllByOrder();
        cargarEntidadesSrv();
        
        //limpio el cuit si es que hubiera
        personaAfip = null;
        personaAfip = new CuitAfip();
        
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
        recreateModel();
        return "/faces/index";
    }
    
   /**
     * @return mensaje que notifica la actualizacion de estado
     */    
    public String habilitar() {
        current.getAdmin().setHabilitado(true);
        update();        
        recreateModel();
        return "view";
    } 
    
    public String deshabilitar() {
        if (getFacade().noTieneDependencias(current.getId())){
            current.getAdmin().setHabilitado(false);
            update();        
            recreateModel();
        }
        else{
            //No Deshabilita 
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PersonaFisicaNoDesabilitable"));            
        }
        return "view";
    } 
    
    /**
     * 
     */
    public void prepareHabilitar(){
        update = 2;
        update();        
    }
    
    /**
     * 
     */
    public void prepareDesHabilitar(){
        update = 1;
        update();        
    }   

    /*************************
    ** Métodos de operación **
    **************************/
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
        //Asigno expediente
        current.setExpediente(expediente);
        
        //Actualizo domicilio
        if(localSelected != null){
            domicilio.setIdLocalidad(localSelected.getId());
            domicilio.setLocalidad(localSelected.getNombre());
        }

        domicilio.setDepartamento(deptoSelected.getNombre());
        domicilio.setProvincia(provSelected.getNombre());
        
        //Asigno domicilio
        current.setDomicilio(domicilio);

        if(current.getNombreCompleto() == null){
            JsfUtil.addErrorMessage("La persona que está guardando debe tener un nombre.");
            return null;
        }else if(current.getCuitCuil() == 0){
            JsfUtil.addErrorMessage("La persona que está guardando debe tener un CUIT o CUIL.");
            return null;
        }else{
            try {
                if(getFacade().noExiste(current.getCuitCuil())){
                    // Inserción
                    getFacade().create(current);
                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaCreated"));
                    //recreateModel();
                    return "view";
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerFisicaExistente"));
                    return null;
                }
            } 
            catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerFisicaCreatedErrorOccured"));
                return null;
            }
        }  
    }
    
    /**
     * Método para registrar un nuevo Expediente
     */
    public void createExp(){
        if(expediente.getNumero() < 1){
            JsfUtil.addErrorMessage("El N° del Expediente no puede ser menor a 1.");
        }else{
            if(expedienteFacade.noExiste(expediente.getNumero(), expediente.getAnio())){
                try{
                    expedienteFacade.create(expediente);
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
        listExpedientes.clear();
        listExpedientes = expedienteFacade.findAllByOrder();
    }
    
    /**
     * Método que actualiza una nueva Instancia en la base de datos.
     * Previamente actualiza los datos de administración
     * @return mensaje que notifica la actualización
     */
    public String update() {    
        boolean edito;
        PerFisica perFisica;
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
                perFisica = getFacade().getExistente(current.getCuitCuil());
                if(perFisica == null){
                    edito = true;  
                }else{
                    edito = perFisica.getId().equals(current.getId());
                }
                if(edito){
                    // valido que el nombre y el cuil/cuit esté completo
                    if(current.getNombreCompleto().equals("")){
                        JsfUtil.addErrorMessage("La persona que está guardando debe tener un nombre");
                        return null;
                    }else if(current.getCuitCuil() == 0 || current.getCuitCuil() < 1){
                        JsfUtil.addErrorMessage("La persona que está guardando debe tener un CUIT o CUIL");
                        return null;
                    }else{
                        // Actualización de datos de administración de la entidad
                        current.getAdmin().setFechaModif(date);
                        current.getAdmin().setUsModif(usLogeado); 

                        // Actualizo
                        getFacade().edit(current);
                        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaUpdated"));
                        return "view";
                    }
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerFisicaExistente"));
                    return null; 
                    }
                
            }else if(update == 1){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("Deshabilitado"));
                return "view";
            }else{
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("Habilitado"));
                return "view";
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerFisicaUpdatedErrorOccured"));
            return null;
        }
    } 

    
    /*************************
     ** Métodos de selección **
     **************************/ 
    /**
     * @param id equivalente al id de la entidad persistida
     * @return la entidad correspondiente
     */
    public PerFisica getPerFisica(java.lang.Long id) {
        return getFacade().find(id);
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
    
    
    /**
     * Método para mostrar los Expedientes vinculados
     */
    public void verExpedientes(){
        expediente = current.getExpediente();
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 950);
        RequestContext.getCurrentInstance().openDialog("", options, null);
    }      
        /**
     * Método para mostrar las Domicilios vinculados
     */
    public void verDomicilios(){
        domicilio = current.getDomicilio();
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 950);
        RequestContext.getCurrentInstance().openDialog("", options, null);
    }  

    /****************************
     * Métodos de validación
     ****************************/    
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
        if(!current.getNombreCompleto().equals((String)arg2)){
            validarExistente(arg2);
        }
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
            
            // Si valida seteo los datos correspondientes de la persona
            current.setCuitCuil(personaAfip.getPejID());
            current.setNombreCompleto(personaAfip.getPejRazonSocial());
            
            JsfUtil.addSuccessMessage("El CUIT ingresado fue validado con exito, puede cerrar la ventana. Luego actualice los datos personales");
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"), ex.getMessage()});
        }
    }    
    
    /**
     * Método para limpiar los datos AFIP seleccionado
     */
    public void limpiarCuit(){
        personaAfip = null;
        personaAfip = new CuitAfip();
        current.setNombreCompleto("");
        current.setCuitCuil(Long.valueOf(0));
    }
    
    /**
     * Método para limpiar todo el formulario new
     */
    public void limpiarForm(){
        limpiarCuit();
        limpiarEntitadesSrv();
    }
    
    /**
     * Método para limpiar todo el formulario edit
     */
    public void limpiarFormEdit(){
        cargarEntidadesSrv();
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
    private PerFisicaFacade getFacade() {
        return perFisicaFacade;
    }
            
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste((long)arg2)){
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerFisicaExistente")));
        }
    }  
    
    /**
     * Restea la entidad
     */
    private void recreateModel() {
        listPerFisica.clear();
        listPerFisica = null;  
        cuit = Long.valueOf(0);
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
     * Método para cargar los establecimientos cedidos
     */
    private void getEstCedidos(){
        listEstCedidos = reasignaFacade.getHistorialXRazonSocial(current, null, true);
    }
    
    /**
     * Método para cargar los establecimientos adquiridos
     */
    private void getEstAdquiridos(){
        listEstAdquiridos = reasignaFacade.getHistorialXRazonSocial(current, null, false);
    }    
    
    /**
     * Método para limpiar el historial de establecimientos adquiridos y cedidos
     */
    private void limpiarHisEst(){
        if(listEstAdquiridos != null){
            listEstAdquiridos.clear();
            listEstAdquiridos = null;
        }
        if(listEstCedidos != null){
            listEstCedidos.clear();
            listEstCedidos = null;
        }
    }    
    
    
    /******************************************************************************
    ** Converter. Se debe actualizar la entidad principal y el facade respectivo **
    *******************************************************************************/
    @FacesConverter(forClass = PerFisica.class)
    public static class PerFisicaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbPerFisica controller = (MbPerFisica) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbPerFisica");
            return controller.getPerFisica(getKey(value));
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
            if (object instanceof PerFisica) {
                PerFisica o = (PerFisica) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PerFisica.class.getName());
            }
        }
    }       
}


