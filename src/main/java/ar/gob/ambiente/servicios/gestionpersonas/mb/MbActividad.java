/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ActividadFacade;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import java.io.IOException;
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

/**
 *
 * @author rodriguezn
 */
public class MbActividad implements Serializable{
    
    private Actividad current;
    private List<Actividad> listado = null;
    private List<Actividad> listaFilter;
    
    private boolean iniciado;
    
    // listado para los establecimientos vinculados
    private List<Establecimiento> listEstVincFilter;
    
    @EJB
    private ActividadFacade actividadFacade;

    /**
     * Creates a new instance of MbActividad
     */
    public MbActividad() {
    }

    public Actividad getCurrent() {
        return current;
    }

    public void setCurrent(Actividad current) {
        this.current = current;
    }

    public List<Actividad> getListado() {
        if (listado == null || listado.isEmpty()) {
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<Actividad> listado) {
        this.listado = listado;
    }

    public List<Actividad> getListaFilter() {
        return listaFilter;
    }

    public void setListaFilter(List<Actividad> listaFilter) {
        this.listaFilter = listaFilter;
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public ActividadFacade getActividadFacade() {
        return actividadFacade;
    }

    public void setActividadFacade(ActividadFacade actividadFacade) {
        this.actividadFacade = actividadFacade;
    }
    
    private Actividad getActividad(java.lang.Long id) {
        return actividadFacade.find(id);
    }

    public List<Establecimiento> getListEstVincFilter() {
        return listEstVincFilter;
    }

    public void setListEstVincFilter(List<Establecimiento> listEstVincFilter) {
        this.listEstVincFilter = listEstVincFilter;
    }
    
    
    /**
     * METODOS DE INICIALIZACION
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
    
    public String prepareList() {
        recreateModel();
        return "list";
    }
    
    public String prepareView() {
        return "view";
    }
        
    public String prepareCreate() {
        current = new Actividad();
        return "new"; 
    }
    
    public String prepareEdit() {
        return "edit";
    }
    
    public String prepareDestroy(){
        boolean libre = getFacade().noTieneDependencias(current.getId());

        if (libre){
            // Elimina
            performDestroy();
            recreateModel();
        }else{
            //No Elimina 
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ActividadNonDeletable"));
        }
        return "view";
    }
    
    public String prepareInicio(){
        recreateModel();
        return "/faces/index";
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
        if(!current.getNombre().equals((String)arg2)){
            validarExistente(arg2);
        }
    }
    
    private void validarExistente(Object arg2) throws ValidatorException{
        if(!getFacade().noExiste((String)arg2)){ 
            throw new ValidatorException(new FacesMessage(ResourceBundle.getBundle("/Bundle").getString("CreateActividadExistente")));
        }
    }
    
    
    /**
     * METODOS PARA LA NAVEGACION
     * @return 
     */
    
    public Actividad getSelected() {
        if (current == null) {
            current = new Actividad();
        }
        return current;
    } 

    
    /**
     * METODOS PRIVADOS
     */
    
    private ActividadFacade getFacade() {
        return actividadFacade;
    }
    
    /**
     * METODOS DE OPERACION
     * @return 
     */
    
    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ActividadCreated"));
            return "view";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ActividadCreatedErrorOccured"));
            return null;
        }
    }
    
    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ActividadUpdated"));
            return "view";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ActividadUpdatedErrorOccured"));
            return null;
        }
    }
    
    private void recreateModel() {
        listado.clear();
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ActividadDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("ActividadDeletedErrorOccured"));
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
   
 
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
    @FacesConverter(forClass = Actividad.class)
    public static class ActividadControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbActividad controller = (MbActividad) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbActividad");
            return controller.getActividad(getKey(value));
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
            if (object instanceof Actividad) {
                Actividad o = (Actividad) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Actividad.class.getName());
            }
        }
    }        

    
}
