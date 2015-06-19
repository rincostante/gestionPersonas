/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.managedBeans;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Especialidad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EspecialidadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.managedBeans.MbLogin;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rodriguezn
 */
public class MbEspecialidad implements Serializable{
    
    private Especialidad current;
    private DataModel items = null;
    private List<Especialidad> listado = null;
    private List<Especialidad> listaFilter;
    
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private MbLogin login;
    private Usuario usLogeado;   
    private boolean iniciado;

    @EJB
    private EspecialidadFacade especialidadFacade;
    
    private String nombre;
    //private List<perJuridica> listaPerJuridicas;
    //private List<perFisica> listaPerFisicas;

    /**
     * Creates a new instance of MbEspecialidad
     */
    public MbEspecialidad() {
    }

    public Especialidad getCurrent() {
        return current;
    }

    public void setCurrent(Especialidad current) {
        this.current = current;
    }

    public List<Especialidad> getListado() {
        if (listado == null || listado.isEmpty()) {
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<Especialidad> listado) {
        this.listado = listado;
    }

    public List<Especialidad> getListaFilter() {
        return listaFilter;
    }

    public void setListaFilter(List<Especialidad> listaFilter) {
        this.listaFilter = listaFilter;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
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

    public EspecialidadFacade getEspecialidadFacade() {
        return especialidadFacade;
    }

    public void setEspecialidadFacade(EspecialidadFacade especialidadFacade) {
        this.especialidadFacade = especialidadFacade;
    }
    
    /**
     * METODOS DE INICIALIZACION
     */
    @PostConstruct
    public void init(){
        iniciado = false;
       /* ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        login = (MbLogin)ctx.getSessionMap().get("mbLogin");
        usLogeado = login.getUsLogeado(); */
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
    
    public String prepareList() {
        recreateModel();
        return "list";
    }
    
    public String prepareView() {
        return "view";
    }
        
    public String prepareCreate() {
       return "new"; 
    }
    
    public String prepareEdit() {
        return "edit";
    }
    
    public String prepareDestroy(){
       return "view"; 
    }
    
    public String prepareInicio(){
        recreateModel();
        return "/faces/index";
    }
    
    public String prepareSelect(){
        return "list";
    }
    
    /**
     * METODOS DE VALIDACION
     */

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
        if(!current.getId().equals((String)arg2)){
            validarExistente(arg2);
        }
    }
    
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste(null,current)){ 
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreateGeneroExistente")));
        }
    }
    
    
    /**
     * METODOS PARA LA NAVEGACION
     * @return 
     */
    
    public Especialidad getSelected() {
        if (current == null) {
            current = new Especialidad();
            //selectedItemIndex = -1;
        }
        return current;
    } 

    public DataModel getItems() {
        if (items == null) {
            //items = getPagination().createPageDataModel();
            items = new ListDataModel(getFacade().findAll());
        }
        return items;
    }    
    
    
    /**
     * METODOS PRIVADOS
     */
    
    private EspecialidadFacade getFacade() {
        return especialidadFacade;
    }
    
    /**
     * METODOS DE OPERACION
     * @return 
     */
    
    public String create() {
        return "view";
    }
    
    public String update() {
        return "view";
    }
    
    private void recreateModel() {
        items = null;
    }
    
    private void performDestroy() {
        try {
            //getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EspecialidadDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("EspecialidadDeletedErrorOccured"));
        }
    }
    
    public void habilitar() {
        update = 2;
        update();        
        recreateModel();
    }  
    
    public void deshabilitar() {
       if (getFacade().noTieneDependencias(current.getId())){
          update = 1;
          update();        
          recreateModel();
       } 
        else{
            //No Deshabilita 
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EspecialidadNonDeletable"));            
        }
    } 
    
    /**
     * METODOS DE SELECCION
     */
        /**
     * @return la totalidad de las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(especialidadFacade.findAll(), false);
    }

    /**
     * @return de a una las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(especialidadFacade.findAll(), true);
    }

    private Especialidad getEspecialidad(java.lang.Long id) {
        return especialidadFacade.find(id);
    }
 
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
   
    @FacesConverter(forClass = Especialidad.class)
    public static class EspecialidadControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbEspecialidad controller = (MbEspecialidad) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbEspecialidad");
            return controller.getEspecialidad(getKey(value));
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
            if (object instanceof Especialidad) {
                Especialidad o = (Especialidad) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Especialidad.class.getName());
            }
        }
    }        

 
}