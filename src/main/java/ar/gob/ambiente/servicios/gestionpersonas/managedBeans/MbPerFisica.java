/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.managedBeans;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.AdminEntidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Especialidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Perfil;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ActividadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.DomicilioFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EspecialidadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerfilFacade;
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
import org.primefaces.context.RequestContext;

/**
*
* @author rodriguezn
*/
public class MbPerFisica implements Serializable{
    
    private PerFisica current;
    private Domicilio domicilio;
    private Expediente expediente;
    
    private List<Domicilio> listDomicilios;
    private List<PerFisica> listPerFisica;
    private Domicilio domVinc;
    
    private List<Expediente> listExpedientes;
    private List<Expediente> expVinc;
    
    @EJB
    private PerFisicaFacade perFisicaFacade;
    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private ExpedienteFacade expedienteFacade;
    @EJB
    private EspecialidadFacade especialidadFacade;
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private DomicilioFacade domicilioFacade;
    @EJB
    private PerfilFacade perfilFacade;
    @EJB
    private ActividadFacade actividadFacade;
    
    private PerFisica perFisicaSelected;
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private List<Especialidad> listaEspecialidad;
    private List<PerJuridica> listaPerJuridica;
    private List<Estado> listaEstado;
    private List<Perfil> listaPerfil;
    private List<Actividad> listaActividad;
    
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
     * @return 
     ********************************/
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

    public List<Expediente> getExpVinc() {
        return expVinc;
    }

    public void setExpVinc(List<Expediente> expVinc) {
        this.expVinc = expVinc;
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

    public List<Especialidad> getListaEspecialidad() {
        return listaEspecialidad;
    }

    public void setListaEspecialidad(List<Especialidad> listaEspecialidad) {
        this.listaEspecialidad = listaEspecialidad;
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

    public List<Perfil> getListaPerfil() {
        return listaPerfil;
    }

    public void setListaPerfil(List<Perfil> listaPerfil) {
        this.listaPerfil = listaPerfil;
    }

    public List<Actividad> getListaActividad() {
        return listaActividad;
    }

    public void setListaActividad(List<Actividad> listaActividad) {
        this.listaActividad = listaActividad;
    }

    public PerFisicaFacade getPerFisicaFacade() {
        return perFisicaFacade;
    }

    public void setPerFisicaFacade(PerFisicaFacade perFisicaFacade) {
        this.perFisicaFacade = perFisicaFacade;
    }

    public PerJuridicaFacade getPerJuridicaFacade() {
        return perJuridicaFacade;
    }

    public void setPerJuridicaFacade(PerJuridicaFacade perJuridicaFacade) {
        this.perJuridicaFacade = perJuridicaFacade;
    }

    public ExpedienteFacade getExpedienteFacade() {
        return expedienteFacade;
    }

    public void setExpedienteFacade(ExpedienteFacade expedienteFacade) {
        this.expedienteFacade = expedienteFacade;
    }

    public EspecialidadFacade getEspecialidadFacade() {
        return especialidadFacade;
    }

    public void setEspecialidadFacade(EspecialidadFacade especialidadFacade) {
        this.especialidadFacade = especialidadFacade;
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

    public PerfilFacade getPerfilFacade() {
        return perfilFacade;
    }

    public void setPerfilFacade(PerfilFacade perfilFacade) {
        this.perfilFacade = perfilFacade;
    }

    public ActividadFacade getActividadFacade() {
        return actividadFacade;
    }

    public void setActividadFacade(ActividadFacade actividadFacade) {
        this.actividadFacade = actividadFacade;
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
        return "list";
    }

    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        domVinc = current.getDomicilio();
        //expVinc = current.getExpediente();
        return "view";
    }

    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        //Se instancia current
        current = new PerFisica();      
        //Inicializamos la creacion de expediente y domicilio
        expediente = new Expediente();
        domicilio = new Domicilio();
        listaPerfil = perfilFacade.findAll();
        listaActividad = actividadFacade.findAll();
        listaEstado = estadoFacade.findAll();
        listaEspecialidad = especialidadFacade.findAll();
        return "new";
    }
    
   /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        domVinc = current.getDomicilio();
        //expVinc = current.getExpediente();
        listaPerfil = perfilFacade.findAll();
        listaActividad = actividadFacade.findAll();
        listaEstado = estadoFacade.findAll();
        listaEspecialidad = especialidadFacade.findAll();
        return "edit";
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
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("DeshabilitarError"));            
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
        //Asigno domicilio
        current.setDomicilio(domicilio);

        //current.setExpedientes(listExpedientes);
        //current.setDomicilios(listDomicilios);
        //getFacade().create(current);
        //return "view";
        if(current.getNombre().isEmpty()){
            JsfUtil.addSuccessMessage("La persona que está guardando debe tener un nombre.");
            return null;
        }else{
            try {
                if(getFacade().noExiste(current.getDni())){
                    // Inserción
                    getFacade().create(current);
                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaCreated"));
                   // recreateModel();
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
                perFisica = getFacade().getExistente(current.getActividad());
                if(perFisica == null){
                    edito = true;  
                }else{
                    edito = perFisica.getId().equals(current.getId());
                }
                if(edito){
                    // Actualización de datos de administración de la entidad
                    current.getAdmin().setFechaModif(date);
                    current.getAdmin().setUsModif(usLogeado); 

                    // Actualizo
                    getFacade().edit(current);
                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaUpdated"));
                    return "view";
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaExistente"));
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
     * Método para revocar la sesión del MB
     * @return 
     */
    public String cleanUp(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.removeAttribute("mbPerFisica");

        return "inicio";
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
        if(!current.getNombre().equals((String)arg2)){
            validarExistente(arg2);
        }
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

    private void validarExpedienteExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExisteExpediente(null, expediente)){ 
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreateInstanciaExistente")));
        }
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
    } 
    
        /**
     * Método para validar si una instacia ya existe en el list que las guarda en memoria
     */
    private boolean compararDomicilio(Domicilio dom){
      boolean retorno = false;
     /**   Iterator domIt = listDomicilios.iterator();
        while(domIt.hasNext()){
            Domicilio domicilio = (Domicilio)domIt.next();
            if(domicilio.getCalle().equals(dom.getCalle())
                    && domicilio.getNumero().equals(dom.getNumero())){
                retorno = true;
            }
        }*/
        return retorno;
    }
    
    
     //Método para validar si una instacia ya existe en el list que las guarda en memoria
     
    private boolean compararExpediente(Expediente exp){
        boolean retorno = false;
     /**   Iterator expIt = listExpedientes.iterator();
        while(expIt.hasNext()){
            Expediente expediente = (Expediente)expIt.next();
            if(expediente.getNumero().equals(exp.getNumero()))
                    && expediente.getAnio().equals(exp.getAnio())){
                retorno = true;
            }
        }*/
        return retorno;
    }

    /**
     * Opera el borrado de la entidad
     */
    private void performDestroy() {
        try {
            // Actualización de datos de administración de la entidad
            Date date = new Date(System.currentTimeMillis());
            current.getAdmin().setFechaBaja(date);
            current.getAdmin().setUsBaja(usLogeado);
            current.getAdmin().setHabilitado(false);
            
            // Deshabilito la entidad
            //getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerFisicaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerFisicaDeletedErrorOccured"));
        }
    }      
    
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
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


