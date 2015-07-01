/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.managedBeans;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoPersonaJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoPersonaJuridicaFacade;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rodriguezn
 */
public class MbTipoPersonaJuridica implements Serializable{
    
    private TipoPersonaJuridica current;
    private DataModel items = null;
    private List<TipoPersonaJuridica> listado = null;
    private List<TipoPersonaJuridica> listaFilter;
    
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private MbLogin login;
    private Usuario usLogeado;   
    private boolean iniciado;
    private boolean hola;
    @EJB
    private TipoPersonaJuridicaFacade tipoPersonaJuridicaFacade;
    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    
    private List<PerJuridica> listaPerJuridicas;
    private List<PerJuridica> listaPerJuridicasFilter;
    private PerJuridica selectPerJuridica;
    private List<PerJuridica> comboPerJuridicas;
    private List<PerJuridica> listaPerJuridica;

    /**
     * Creates a new instance of MbTipoPersonaJuridica
     */
    public MbTipoPersonaJuridica() {
    }

    public boolean isHola() {
        return hola;
    }

    public void setHola(boolean hola) {
        this.hola = hola;
    }

    public PerJuridicaFacade getPerJuridicaFacade() {
        return perJuridicaFacade;
    }

    public void setPerJuridicaFacade(PerJuridicaFacade perJuridicaFacade) {
        this.perJuridicaFacade = perJuridicaFacade;
    }

    public List<PerJuridica> getListaPerJuridicas() {
        return listaPerJuridicas;
    }

    public void setListaPerJuridicas(List<PerJuridica> listaPerJuridicas) {
        this.listaPerJuridicas = listaPerJuridicas;
    }

    public List<PerJuridica> getListaPerJuridicasFilter() {
        return listaPerJuridicasFilter;
    }

    public void setListaPerJuridicasFilter(List<PerJuridica> listaPerJuridicasFilter) {
        this.listaPerJuridicasFilter = listaPerJuridicasFilter;
    }

    public PerJuridica getSelectPerJuridica() {
        return selectPerJuridica;
    }

    public void setSelectPerJuridica(PerJuridica selectPerJuridica) {
        this.selectPerJuridica = selectPerJuridica;
    }

    public List<PerJuridica> getComboPerJuridicas() {
        return comboPerJuridicas;
    }

    public void setComboPerJuridicas(List<PerJuridica> comboPerJuridicas) {
        this.comboPerJuridicas = comboPerJuridicas;
    }

    public List<PerJuridica> getListaPerJuridica() {
        return listaPerJuridica;
    }

    public void setListaPerJuridica(List<PerJuridica> listaPerJuridica) {
        this.listaPerJuridica = listaPerJuridica;
    }

    public TipoPersonaJuridica getCurrent() {
        return current;
    }

    public void setCurrent(TipoPersonaJuridica current) {
        this.current = current;
    }

    public List<TipoPersonaJuridica> getListado() {
        if (listado == null || listado.isEmpty()) {
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<TipoPersonaJuridica> listado) {
        this.listado = listado;
    }

    public List<TipoPersonaJuridica> getListaFilter() {
        return listaFilter;
    }

    public void setListaFilter(List<TipoPersonaJuridica> listaFilter) {
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

    public TipoPersonaJuridicaFacade getTipoPersonaJuridicaFacade() {
        return tipoPersonaJuridicaFacade;
    }

    public void setTipoPersonaJuridicaFacade(TipoPersonaJuridicaFacade tipoPersonaJuridicaFacade) {
        this.tipoPersonaJuridicaFacade = tipoPersonaJuridicaFacade;
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
        if(!current.getNombre().equals((String)arg2)){
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
    
    public TipoPersonaJuridica getSelected() {
        if (current == null) {
            current = new TipoPersonaJuridica();
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
    
    private TipoPersonaJuridicaFacade getFacade() {
        return tipoPersonaJuridicaFacade;
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("TipoPersonaJuridicaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("TipoPersonaJuridicaDeletedErrorOccured"));
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
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("TipoPersonaJuridicaNonDeletable"));            
        }
    } 
    
    /**
     * METODOS DE SELECCION
     */
        /**
     * @return la totalidad de las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(tipoPersonaJuridicaFacade.findAll(), false);
    }

    /**
     * @return de a una las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(tipoPersonaJuridicaFacade.findAll(), true);
    }

    private TipoPersonaJuridica getTipoPersonaJuridica(java.lang.Long id) {
        return tipoPersonaJuridicaFacade.find(id);
    }
 
    /********************************************************************
    ** Converter. Se debe actualizar la entidad y el facade respectivo **
    *********************************************************************/
   
    @FacesConverter(forClass = TipoPersonaJuridica.class)
    public static class TipoPersonaJuridicaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbTipoPersonaJuridica controller = (MbTipoPersonaJuridica) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbTipoPersonaJuridica");
            return controller.getTipoPersonaJuridica(getKey(value));
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
            if (object instanceof TipoPersonaJuridica) {
                TipoPersonaJuridica o = (TipoPersonaJuridica) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TipoPersonaJuridica.class.getName());
            }
        }
    }    
    
    public void perJuridicaChangeListener(ValueChangeEvent event) {      
        selectPerJuridica = (PerJuridica)event.getNewValue();      
        comboPerJuridicas = perJuridicaFacade.getNombres(selectPerJuridica);      
    }

 
}