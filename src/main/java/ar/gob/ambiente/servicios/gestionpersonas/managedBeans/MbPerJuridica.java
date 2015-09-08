/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.managedBeans;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.AdminEntidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Especialidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Perfil;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ActividadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EspecialidadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerfilFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoEstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoPersonaJuridicaFacade;
import java.io.Serializable;
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
import javax.faces.context.ExternalContext;
import org.primefaces.context.RequestContext;

/**
*
* @author rodriguezn
*/
public class MbPerJuridica implements Serializable{
    
    private PerJuridica current;
    private Expediente expediente;
    private Establecimiento establecimiento;
    
    private List<Expediente> listaExpedientes;
    private List<Establecimiento> listaEstablecimiento;
    private List<Expediente> expVinc;
    
    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private PerFisicaFacade perFisicaFacade;
    @EJB
    private ExpedienteFacade expedienteFacade;
    @EJB
    private EspecialidadFacade especialidadFacade;
    @EJB
    private EstadoFacade estadoFacade;
    @EJB
    private PerfilFacade perfilFacade;
    @EJB
    private ActividadFacade actividadFacade;
    @EJB
    private TipoPersonaJuridicaFacade tipoFacade;
    @EJB
    private TipoEstablecimientoFacade tipoEstablecimientoFacade;
    
    private PerJuridica perJuridicaSelected;
    private MbLogin login;
    private Usuario usLogeado;
    
    private boolean iniciado;
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private List<Especialidad> listaEspecialidad;
    private List<PerJuridica> listaPerJuridica;
    private List<Estado> listaEstado;
    private List<Perfil> listaPerfil;
    private List<Actividad> listaActividad;
    private List<TipoPersonaJuridica> listaTipoPersonaJuridica;
    private List<TipoEstablecimiento> listaTipoEstablecimiento;
    private String razonSocial;
    
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
     * @return 
     ********************************/
   
    public List<TipoEstablecimiento> getListaTipoEstablecimiento() {
        return listaTipoEstablecimiento;
    }

    public void setListaTipoEstablecimiento(List<TipoEstablecimiento> listaTipoEstablecimiento) {
        this.listaTipoEstablecimiento = listaTipoEstablecimiento;
    }

   
    public PerJuridica getCurrent() {
        return current;
    }

    public void setCurrent(PerJuridica current) {
        this.current = current;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public List<Expediente> getListaExpedientes() {
        return listaExpedientes;
    }

    public void setListaExpedientes(List<Expediente> listaExpedientes) {
        this.listaExpedientes = listaExpedientes;
    }

    public List<Establecimiento> getListaEstablecimiento() {
        return listaEstablecimiento;
    }

    public void setListaEstablecimiento(List<Establecimiento> listaEstablecimiento) {
        this.listaEstablecimiento = listaEstablecimiento;
    }

    public List<Expediente> getExpVinc() {
        return expVinc;
    }

    public void setExpVinc(List<Expediente> expVinc) {
        this.expVinc = expVinc;
    }

    public PerJuridicaFacade getPerJuridicaFacade() {
        return perJuridicaFacade;
    }

    public void setPerJuridicaFacade(PerJuridicaFacade perJuridicaFacade) {
        this.perJuridicaFacade = perJuridicaFacade;
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

    public List<Especialidad> getListaEspecialidad() {
        return listaEspecialidad;
    }

    public void setListaEspecialidad(List<Especialidad> listaEspecialidad) {
        this.listaEspecialidad = listaEspecialidad;
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
        //expVinc = current.getExpediente();
        return "view";
    }

    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        //Se instancia current
        current = new PerJuridica();      
        //Inicializamos la creacion de expediente y establecimiento
        expediente = new Expediente();
        establecimiento = new Establecimiento();
        
        listaPerfil = perfilFacade.findAll();
        listaEstado = estadoFacade.findAll();
        listaEspecialidad = especialidadFacade.findAll();
        listaTipoPersonaJuridica = tipoFacade.findAll();
        return "new";
    }
    
   /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        //expVinc = current.getExpediente();
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
     * Método para guardar los expediente creados en el listExpedientes que irán en la nueva persona fisica
     */
    public void createExpediente(){
        //if(!compararExpediente(expediente)){
            // se agregan los datos del AdminEntidad
            Date date = new Date(System.currentTimeMillis());
            AdminEntidad admEnt = new AdminEntidad();
            admEnt.setFechaAlta(date);
            admEnt.setHabilitado(true);
            admEnt.setUsAlta(usLogeado);
            current.setAdmin(admEnt);
            // reseteo la expediente
            expediente = null;
            expediente = new Expediente();
      //  } else{
       //     JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ExpedienteExistente"));
      //  }

    }
    
  public void agregarEstablecimientos(){

            listaEstado = estadoFacade.findAll();
            listaActividad = actividadFacade.findAll();
            listaTipoEstablecimiento = tipoEstablecimientoFacade.findAll();
            Map<String,Object> options = new HashMap<>();
            options.put("contentWidth", 1200);
            RequestContext.getCurrentInstance().openDialog("dlgAddEstablecimientos", options, null); 
              
        }


            
    public void editarEstablecimientos(){
            establecimiento = current.getEstablecimiento();
           // listEstablecimiento = establecimientoFacade.findAll();

            //listaEstados = estadoFacade.getEstadosXapp(app);
            Map<String,Object> options = new HashMap<>();
            options.put("contentWidth", 1200);
            RequestContext.getCurrentInstance().openDialog("dlgEditEstablecimientos", options, null);
    }
/*-----------------------------------------------------------------------------------------------------------*/    
     /**
     * Método para validar si una instacia ya existe en el list que las guarda en memoria
     */
    private boolean compararEstablecimiento(Establecimiento esta){
        boolean retorno = false;
        Iterator estaIt;
        
        // Si estoy creando un procedimiento nuevo, uso el iterator del listInstancias
        // Si no, lo uso del current.getInstancias
        if(current.getId() != null){
            estaIt = current.getEstablecimientos().iterator();
        }else{
            estaIt = listaEstablecimiento.iterator(); 
        }
        
        while(estaIt.hasNext()){
            Establecimiento establecimiento = (Establecimiento)estaIt.next();
            if(establecimiento.getActividad().equals(esta.getActividad())
                    && establecimiento.getDomicilio().equals(esta.getDomicilio())
                    && establecimiento.getEstado().equals(esta.getEstado())){
                retorno = true;
            }
        }
        return retorno;
    }    
    /**
     * Método para guardar los Establecimientos creados en el listaEstablecimiento que irán en la nueva perJuridica
     */
    public void createEstablecimiento(){
        if(!compararEstablecimiento(establecimiento)){ 

            // Si estoy creanto un procedimiento nuevo, agrego la instancia al list
            // Si no se la agrego a la propiedad instancias del procedimiento
             
            if(current.getId() != null){

                current.getEstablecimientos().add(establecimiento);
                // se agregan los datos del AdminEntidad
                Date date = new Date(System.currentTimeMillis());
                AdminEntidad admEnt = new AdminEntidad();
                admEnt.setFechaAlta(date);
                admEnt.setHabilitado(true);
                admEnt.setUsAlta(usLogeado);
                current.setAdmin(admEnt);
            }else{
                // se agregan los datos del AdminEntidad
                Date date = new Date(System.currentTimeMillis());
                AdminEntidad admEnt = new AdminEntidad();
                admEnt.setFechaAlta(date);
                admEnt.setHabilitado(true);
                admEnt.setUsAlta(usLogeado);
                establecimiento.setAdmin(admEnt);
                listaEstablecimiento.add(establecimiento);    
            }
            // reseteo la instancia
            establecimiento = null;
            establecimiento = new Establecimiento();
        } else{
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoExistente"));
        }
    }
   
/*-----------------------------------------------------------------------------------------------------------*/
    
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
        //Asigno Establecimiento
        current.setEstablecimientos(listaEstablecimiento);
  
        if(current.getRazonSocial().isEmpty()){
            JsfUtil.addSuccessMessage("La persona Jurídica que está guardando debe tener una Razón Social.");
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
        PerJuridica perJuridica;
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
                perJuridica = getFacade().getExistente(current.getActividad());
                if(perJuridica == null){
                    edito = true;  
                }else{
                    edito = perJuridica.getId().equals(current.getId());
                }
                if(edito){
                    // Actualización de datos de administración de la entidad
                    current.getAdmin().setFechaModif(date);
                    current.getAdmin().setUsModif(usLogeado); 

                    // Actualizo
                    getFacade().edit(current);
                    JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerJuridicaUpdated"));
                    return "view";
                }else{
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PerJuridicaExistente"));
                    return null; 
                    }
                
            }else if(update == 1){
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerJuridicaDeshabilitado"));
                return "view";
            }else{
                getFacade().edit(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PerJuridicaHabilitado"));
                return "view";
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PerJuridicaUpdatedErrorOccured"));
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
    
    
    /**
     * Método para mostrar los Expedientes vinculados
     */
    public void verExpedientes(){
        expediente = current.getExpediente();
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
    private PerJuridicaFacade getFacade() {
        return perJuridicaFacade;
    }

    private void validarExpedienteExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExisteExpediente(null, expediente)){ 
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreateInstanciaExistente")));
        }
    } 
            
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste((String)arg2)){
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreatePerJuridicaExistente")));
        }
    }  
    
    /**
     * Restea la entidad
     */
    private void recreateModel() {
        listaPerJuridica.clear();
        listaPerJuridica = null;
        if(listaExpedientes != null){
            listaExpedientes.clear();
            listaExpedientes =null;
        }
        if(listaEstablecimiento !=null){
            listaEstablecimiento.clear();
            listaEstablecimiento =null;
        }   
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
         

    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
    @FacesConverter(forClass = PerJuridica.class)
    public static class PerJuridicaControllerConverter implements Converter {

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