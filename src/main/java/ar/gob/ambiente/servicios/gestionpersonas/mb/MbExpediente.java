/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Expediente;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ExpedienteFacade;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
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

public class MbExpediente implements Serializable{
    
    private Expediente current;
    private List<Expediente> listado = null;
    private List<Expediente> listaFilter;    
    
    @EJB
    private ExpedienteFacade expedienteFacade;

    private boolean iniciado;    

    /**
     * Creates a new instance of MbExpediente
     */
    public MbExpediente() {
    }

    public Expediente getCurrent() {
        return current;
    }

    public void setCurrent(Expediente current) {
        this.current = current;
    }

    public List<Expediente> getListado() {
        if (listado == null || listado.isEmpty()) {
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<Expediente> listado) {
        this.listado = listado;
    }

    public List<Expediente> getListaFilter() {
        return listaFilter;
    }

    public void setListaFilter(List<Expediente> listaFilter) {
        this.listaFilter = listaFilter;
    }

    /****************************
     * Métodos de inicialización
     ****************************/
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa los datos del usuario
     */
    @PostConstruct
    public void init(){
        iniciado = false;
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
    
    /**
     * @return acción para el listado de entidades a mostrar en el list
     */
    public String prepareList() {
        //recreateModel();
        return "list";
    }
    
    /**
     * @return acción para el detalle de la entidad
     */
    public String prepareView() {
        return "view";
    }
    
    /** (Probablemente haya que embeberlo con el listado para una misma vista)
     * @return acción para el formulario de nuevo
     */
    public String prepareCreate() {
        current = new Expediente();
        return "new";
    }   
    
    /**
     * @return acción para la edición de la entidad
     */
    public String prepareEdit() {
        return "edit";
    }    

    /**********************
     * Métodos de operación
     **********************/
    /**
    * @return 
    */   
    public String create() {     
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ExpedienteCreated"));
            return "view";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ExpedienteCreatedErrorOccured"));
            return null;
        }
    }
    

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ExpedienteUpdated"));
            return "view";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ExpedienteUpdatedErrorOccured"));
            return null;
        }
    }    
    
    /**
     * Restea la entidad
     */
    private void recreateModel() {
        listado.clear();
    }    
    
    /**
     * @return La entidad gestionada
     */

    public Expediente getSelected() {
        if (current == null) {
            current = new Expediente();
        }
        return current;
    } 
    
    /**
     * @param id equivalente al id de la entidad persistida
     * @return la entidad correspondiente
     */
    public Expediente getExpediente(java.lang.Long id) {
        return expedienteFacade.find(id);
    }  
    
    
    /*********************
    ** Métodos privados **
    **********************/
    /**
     * @return el Facade
     */
    private ExpedienteFacade getFacade() {
        return expedienteFacade;
    }    
    
    
    /**
     * Opera el borrado de la entidad
     */
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ExpedienteDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ExpedienteDeletedErrorOccured"));
        }
    }    
    
    
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
    @FacesConverter(forClass = Expediente.class)
    public static class ExpedienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbExpediente controller = (MbExpediente) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbExpediente");
            return controller.getExpediente(getKey(value));
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
            if (object instanceof Expediente) {
                Expediente o = (Expediente) object;
                return getStringKey(o.getId());
           } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Expediente.class.getName());
            }
        }
    }            
}
