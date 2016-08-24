/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.AdminEntidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.ReasignaRazonSocial;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.EntidadServicio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
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
import javax.xml.ws.WebServiceRef;
import org.primefaces.context.RequestContext;

/**
*
* @author rodriguezn
*/
public class MbPerJuridica implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CuitAfipWs/CuitAfipWs.wsdl")
    private CuitAfipWs_Service srvCuitAfip;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CentrosPobladosWebService/CentrosPobladosWebService.wsdl")
    private CentrosPobladosWebService_Service srvCentrosPob;
    
    private PerJuridica current;

    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private TipoPersonaJuridicaFacade tipoFacade;
    @EJB
    private ExpedienteFacade expedienteFacade;
    @EJB
    private TipoEstablecimientoFacade tipoEstFacade;
    @EJB
    private EstablecimientoFacade estFacade;
    @EJB
    private ReasignaRazonSocialFacade reasignaFacade;
    
    private PerJuridica perJuridicaSelected;
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private CuitAfip personaAfip;
    private static final Logger logger = Logger.getLogger(PerFisica.class.getName());
    private Long cuit;    
    private boolean noValidaCuit;
    private String razonSocialIng;
    
    private List<PerJuridica> listaPerJuridica;
    private List<Estado> listaEstado;
    private List<TipoPersonaJuridica> listaTipoPersonaJuridica;

    // listados provistos por el servicio de centros poblados
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;    
    
    // campos para la gestión de Establecimiento de tipo Domicilio legal
    private Establecimiento domLegal;
    private List<Expediente> listaExpedientes;
    private Expediente expediente;
    private Domicilio domicilio;
    private boolean tieneDomLegal;
    
    // listados para ver los historiales de razón social
    private List<ReasignaRazonSocial> listEstAdquiridos;
    private List<ReasignaRazonSocial> listEstCedidos; 
    
    /**
     * Creates a new instance of MbPerJuridica
     */
    public MbPerJuridica() {
    }
    /****************************
     * Métodos de inicialización
     ****************************/
    /**
     * Método que se ejecuta luego de perJuridicada la clase e inicializa los datos del usuario
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
   
    public String getRazonSocialIng() {
        return razonSocialIng;
    }

    public void setRazonSocialIng(String razonSocialIng) {
        this.razonSocialIng = razonSocialIng;
    }

    public boolean isNoValidaCuit() {
        return noValidaCuit;
    }

    public void setNoValidaCuit(boolean noValidaCuit) {
        this.noValidaCuit = noValidaCuit;
    }

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

   
    public boolean isTieneDomLegal() {
        return tieneDomLegal;
    }

    public void setTieneDomLegal(boolean tieneDomLegal) {
        this.tieneDomLegal = tieneDomLegal;
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

   
    public List<Expediente> getListaExpedientes() {
        return listaExpedientes;
    }

    public void setListaExpedientes(List<Expediente> listaExpedientes) {
        this.listaExpedientes = listaExpedientes;
    }

   
    public Establecimiento getDomLegal() {
        return domLegal;
    }

    public void setDomLegal(Establecimiento domLegal) {
        this.domLegal = domLegal;
    }

    public CuitAfipWs_Service getSrvCuitAfip() {
        return srvCuitAfip;
    }

    public void setSrvCuitAfip(CuitAfipWs_Service srvCuitAfip) {
        this.srvCuitAfip = srvCuitAfip;
    }

    public CentrosPobladosWebService_Service getSrvCentrosPob() {
        return srvCentrosPob;
    }

    public void setSrvCentrosPob(CentrosPobladosWebService_Service srvCentrosPob) {
        this.srvCentrosPob = srvCentrosPob;
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

    public List<EntidadServicio> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<EntidadServicio> getListLocalidades() {
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

   
    public CuitAfip getPersonaAfip() {
        return personaAfip;
    }

    public void setPersonaAfip(CuitAfip personaAfip) {
        this.personaAfip = personaAfip;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public PerJuridica getCurrent() {
        return current;
    }

    public void setCurrent(PerJuridica current) {
        this.current = current;
    }

    public PerJuridicaFacade getPerJuridicaFacade() {
        return perJuridicaFacade;
    }

    public void setPerJuridicaFacade(PerJuridicaFacade perJuridicaFacade) {
        this.perJuridicaFacade = perJuridicaFacade;
    }

    public EstadoFacade getEstadoFacade() {
       return estadoFacade;
    }

    public void setEstadoFacade(EstadoFacade estadoFacade) {
        this.estadoFacade = estadoFacade;
    }

    public PerJuridica getPerJuridicaSelected() {
        return perJuridicaSelected;
    }

    public void setPerJuridicaSelected(PerJuridica perJuridicaSelected) {
        this.perJuridicaSelected = perJuridicaSelected;
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

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public List<PerJuridica> getListaPerJuridica() {
            if(listaPerJuridica == null){
            listaPerJuridica = getFacade().findAll();
        }
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

    public TipoPersonaJuridicaFacade getTipoFacade() {
        return tipoFacade;
    }

    public void setTipoFacade(TipoPersonaJuridicaFacade tipoFacade) {
        this.tipoFacade = tipoFacade;
   }

    public List<TipoPersonaJuridica> getListaTipoPersonaJuridica() {
        return listaTipoPersonaJuridica;
    }

    public void setListaTipoPersonaJuridica(List<TipoPersonaJuridica> listaTipoPersonaJuridica) {
        this.listaTipoPersonaJuridica = listaTipoPersonaJuridica;
    }
    
    public PerJuridica getSelected() {
        if (current == null) {
            current = new PerJuridica();
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
        getEstCedidos();
        getEstAdquiridos();
        tieneDomLegal = tieneDomLegal();
        return "view";
    }
    

    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        //Se instancia current
        current = new PerJuridica();      
        listaTipoPersonaJuridica = tipoFacade.findAll();
        listaEstado = estadoFacade.findAll();
        return "new";
    }
    
    /**
     * Método para preparar la creación de un Domicilio legal para la Presona Jurídica
     */
    public void prepareCreateDomLegal(){
        domicilio = new Domicilio();
        domLegal = new Establecimiento();
        listaExpedientes = expedienteFacade.findAllByOrder();
        listaEstado = estadoFacade.findAllByNombre();
        
        // cargo las provincias
        getProvinciasSrv();
        
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 800);
        RequestContext.getCurrentInstance().openDialog("dlgAddDomLegal", options, null);
    }
    
   /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        //pueblo los combos
        listaEstado = estadoFacade.findAll();
        listaTipoPersonaJuridica = tipoFacade.findAll();
        
        //limpio el cuit si es que hubiera
        personaAfip = null;
        personaAfip = new CuitAfip();
        
        return "edit";
    }
           
    public String prepareInicio(){
        recreateModel();
        return "/faces/index";
    }
    
    /**
     * Damos valor a la variable Update
     */
    public void prepareHabilitar(){
        update = 2;
        update();        
    }
    
    /**
     * Damos valor a la variable Update
     */
    public void prepareDeshabilitar(){
        update = 1;
        update();        
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
        
        if(current.getRazonSocial() == null){
            JsfUtil.addErrorMessage("La Persona Jurídica que está guardando debe tener una Razón Social.");
            return null;
        }else if(current.getCuit() == 0){
            JsfUtil.addErrorMessage("La Persona Jurícia que está guardando debe tener un CUIT.");
            return null;
        }else{
            try {
                if(getFacade().noExiste(current.getCuit())){
                    // Inserción
                    getFacade().create(current);
                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerJuridicaCreated"));
                   // recreateModel();
                    return "view";
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerJuridicaExistente"));
                    return null;
                }
            } 
            catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerJuridicaCreatedErrorOccured"));
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
    
    public void createDomLegal(){
        // valido que no exista ya un domicilio legal para la empresa
        if(!tieneDomLegal()){
            // Creación de la entidad de administración y asignación
            Date date = new Date(System.currentTimeMillis());
            AdminEntidad admEnt = new AdminEntidad();
            admEnt.setFechaAlta(date);
            admEnt.setHabilitado(true);
            admEnt.setUsAlta(usLogeado);
            domLegal.setAdmin(admEnt);

            // Asigno la Persona Jurídica
            domLegal.setPerJuridica(current);

            // Asigno el tipo de Establecimiento, en este caso "Domicilio Legal"
            if(tipoEstFacade.getTipoDomLegal() != null){
                domLegal.setTipo(tipoEstFacade.getTipoDomLegal());
            }else{
                JsfUtil.addErrorMessage("No existe el tipo de Establecimiento Domicilio legal, debe registrarlo antes de continuar con la operación.");
            }

            //Actualizo domicilio
            if(localSelected != null){
                domicilio.setIdLocalidad(localSelected.getId());
                domicilio.setLocalidad(localSelected.getNombre());
            }

            domicilio.setDepartamento(deptoSelected.getNombre());
            domicilio.setProvincia(provSelected.getNombre());

            //Asigno domicilio
            domLegal.setDomicilio(domicilio);  
            try{
                estFacade.create(domLegal);
                JsfUtil.addSuccessMessage("El Domicilio legal ya ha sido registrado, por favor cierre esta ventana.");
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error registrando el domicilio legal. " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("Ya existe un Domicilio legal para esta Persona Jurídica, por favor, actualice los datos del existente.");
        }
    }    
    
    /**
     * Método para actualizar el listados de expedientes
     */
    public void actualizarExpedientes(){
        listaExpedientes.clear();
        listaExpedientes = expedienteFacade.findAllByOrder();
    }    
    
    /**
     * Método que actualiza una nueva Instancia en la base de datos.
     * Previamente actualiza los datos de administración
     * @return mensaje que notifica la actualización
     */
    public String update() {    
        boolean edito;
        PerJuridica pej;
        Date date = new Date(System.currentTimeMillis());

        //Actualiza según el valor de Update
        if(update == 1){
            //Deshabilitar
            current.getAdmin().setFechaBaja(date);
            current.getAdmin().setUsBaja(usLogeado);
            current.getAdmin().setHabilitado(false);
        }
        if(update == 2){
            //Habilitar
            current.getAdmin().setFechaModif(date);
            current.getAdmin().setUsModif(usLogeado);
            current.getAdmin().setHabilitado(true);
            current.getAdmin().setFechaBaja(null);
            current.getAdmin().setUsBaja(usLogeado);
        }
        if(update == 0){
            if(current.getRazonSocial().isEmpty()){
                JsfUtil.addErrorMessage("La Persona Jurídica que está guardando debe tener una Razón Social.");
                return null;
            }else if(current.getCuit() == 0){
                JsfUtil.addErrorMessage("La Persona Jurícia que está guardando debe tener un CUIT.");
                return null;
            }else{  
                current.getAdmin().setFechaModif(date);
                current.getAdmin().setUsModif(usLogeado);
            }
        }
       //Acualizo según la operación seleccionada
        try {
            if(update == 0){
                pej = getFacade().getExistente(current.getRazonSocial(), current.getCuit());
                if(pej == null){
                    edito = true;  
                }else{
                    edito = pej.getId().equals(current.getId());
                }
                if(edito){
                    // Actualización de datos de administración de la entidad PerJuridica
                    current.getAdmin().setFechaModif(date);
                    current.getAdmin().setUsModif(usLogeado); 

                    // Actualizo
                    getFacade().edit(current);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Persona Jurídica", "Ha sido actualizada"));
                    return "view";
                }else{
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Persona Jurídica", "Ya Existe"));
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Persona Jurídica", "Actualizado correctamente"));
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
    public PerJuridica getPerJuridica(java.lang.Long id) {
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
        if(!current.getRazonSocial().equals((String)arg2)){
            validarExistente(arg2);
        }
    }    
 
    /**
     * Método para abrir el diálogo para validar cuit 
     */
    public void prepareValidarCuit(){
        // seteo el objeto que recibirá los resultados de la validación
        noValidaCuit = false;
        razonSocialIng = "";
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
            
            if(personaAfip != null){
                // Si valida seteo los datos correspondientes de la persona
                current.setCuit(personaAfip.getPejID());
                current.setRazonSocial(personaAfip.getPejRazonSocial());
                JsfUtil.addSuccessMessage("El CUIT ingresado fue validado con exito, puede cerrar la ventana. Luego actualice los datos personales");
            }else{
                noValidaCuit = true;
                JsfUtil.addErrorMessage("No se han podido validar los datos correspondientes al CUIT ingresado, por favor, "
                        + "verifiquelos y de ser correctos, ingrese la razón social que corresponda en el formulario.");
            }
            
        } catch (Exception ex) {
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"));
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{ResourceBundle.getBundle("/Bundle").getString("PerFisicaCuitAfipWsError"), ex.getMessage()});
        }
    }    
    
    /**
     * Método nuevo que permite ingresar cuit y razón social sin validar mediante el servicio Afip
     */
    public void guardarSinValidar(){
        if(cuit > 0 && !razonSocialIng.equals("")){
            current.setCuit(cuit);
            String tempRs = razonSocialIng;
            current.setRazonSocial(tempRs.toUpperCase()); 
            JsfUtil.addSuccessMessage("Se agregaron el CUIT y la Razón Social, puede cerrar la ventana. Luego actualice los datos generales");
        }else{
            JsfUtil.addErrorMessage("Los campos CUIT y Razón Social son obligatorios.");
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
        cuit = Long.valueOf(0);
        noValidaCuit = false;
        razonSocialIng = "";
        personaAfip = null;
        personaAfip = new CuitAfip();
        current.setRazonSocial("");
        current.setCuit(Long.valueOf(0));
    }
    
    /**
     * Método para limpiar todo el formulario new
     */
    public void limpiarForm(){
        limpiarCuit();
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
    public void limpiarDlg(){
        limpiarEntitadesSrv();
    }    
    
   
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private PerJuridicaFacade getFacade() {
        return perJuridicaFacade;
    }
            
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste((long)arg2)){
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerJuridicaExistente")));
        }
    }  
    
    /**
     * Restea la entidad
     */
    private void recreateModel() {
        listaPerJuridica.clear();
        listaPerJuridica = null;
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
     * Método para verificar si la Persona Jurídica ya tiene un domicilio legal
     */
    private boolean tieneDomLegal(){
        boolean result = false;
        if(!current.getEstablecimientos().isEmpty()){
            for(Establecimiento est : current.getEstablecimientos()){
                if(est.getTipo().getNombre().equals("Domicilio legal")){
                    result = true;
                }
            }
        }else{
            result = false;
        }
        return result;
    }
    
    /**
     * Método para cargar los establecimientos cedidos
     */
    private void getEstCedidos(){
        listEstCedidos = reasignaFacade.getHistorialXRazonSocial(null, current, true);
    }
    
    /**
     * Método para cargar los establecimientos adquiridos
     */
    private void getEstAdquiridos(){
        listEstAdquiridos = reasignaFacade.getHistorialXRazonSocial(null, current, false);
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
    
    
  /********************************************************************
   ** Converter. Se debe actualizar la entidad y el facade respectivo **
   *********************************************************************/
    @FacesConverter(forClass = PerJuridica.class)
    public static class PerJuridicaControllerConverter implements Converter {

         /**
         *
         * @param facesContext
         * @param component
         * @param value
         * @return
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbPerJuridica controller = (MbPerJuridica) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbPerJuridica");
            return controller.getPerJuridica(getKey(value));
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

        /**
         *
         * @param facesContext
         * @param component
         * @param object
         * @return
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PerJuridica) {
                PerJuridica o = (PerJuridica) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PerJuridica.class.getName());
            }
        }
    }        
    
}
