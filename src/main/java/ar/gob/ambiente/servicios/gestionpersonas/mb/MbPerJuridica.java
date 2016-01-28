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
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoEstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoPersonaJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfip;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs;
import ar.gob.ambiente.servicios.gestionpersonas.wsClient.validarCuit.CuitAfipWs_Service;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.xml.ws.WebServiceRef;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
*
* @author rodriguezn
*/
public class MbPerJuridica implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CuitAfipWs/CuitAfipWs.wsdl")
    private CuitAfipWs_Service srvCuitAfip;
    
    private PerJuridica current;

    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private TipoPersonaJuridicaFacade tipoFacade;
    
    private PerJuridica perJuridicaSelected;
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private CuitAfip personaAfip;
    private static final Logger logger = Logger.getLogger(PerFisica.class.getName());
    private Long cuit;    
    
    private List<PerJuridica> listaPerJuridica;
    private List<Estado> listaEstado;
    private List<TipoPersonaJuridica> listaTipoPersonaJuridica;

    
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
        return "list";
    }

    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        listaEstado = estadoFacade.findAll();
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
     * Método para revocar la sesión del MB
     * @return 
     */
    public String cleanUp(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.removeAttribute("mbPerJuridica");

        return "inicio";
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
            current.setCuit(personaAfip.getPejID());
            current.setRazonSocial(personaAfip.getPejRazonSocial());
            
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
    
   
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private PerJuridicaFacade getFacade() {
        return perJuridicaFacade;
    }
    /* Método para editar desde la tabla con sólo pararse en el campo a editar
    */
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Establecimiento editado", ((Establecimiento) event.getObject()).getTelefono());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Establecimiento) event.getObject()).getTelefono());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
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
    
    public void pruebaChangeListener(AjaxBehaviorEvent event){
        System.out.println("cambió");
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
