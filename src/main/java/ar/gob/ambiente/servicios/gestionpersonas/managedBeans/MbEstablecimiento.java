/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.managedBeans;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Actividad;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Domicilio;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Establecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Estado;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerFisica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.PerJuridica;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.TipoEstablecimiento;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.JsfUtil;
import ar.gob.ambiente.servicios.gestionpersonas.facades.ActividadFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.DomicilioFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstablecimientoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.EstadoFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerFisicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.PerJuridicaFacade;
import ar.gob.ambiente.servicios.gestionpersonas.facades.TipoEstablecimientoFacade;
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
public class MbEstablecimiento implements Serializable{
    
    private Establecimiento current;
    private DataModel items = null;
    private List<Establecimiento> listado = null;
    private List<Establecimiento> listaFilter;
    
    private int update; // 0=updateNormal | 1=deshabiliar | 2=habilitar
    private MbLogin login;
    private Usuario usLogeado;   
    private boolean iniciado;

    @EJB
    private EstablecimientoFacade establecimientoFacade;
    @EJB
    private PerJuridicaFacade perJuridicaFacade;
    @EJB
    private PerFisicaFacade perFisicaFacade;
    @EJB
    private TipoEstablecimientoFacade tipoEstablecimientoFacade;
    @EJB
    private ActividadFacade actividadFacade;
    @EJB
    private DomicilioFacade domicilioFacade;
    @EJB
    private EstadoFacade estadoFacade;
    
    private List<PerJuridica> listaPerJuridicas;
    private List<PerJuridica> listaPerJuridicasFilter;
    private PerJuridica selectPerJuridica;
    private List<PerJuridica> comboPerJuridicas;
    private List<PerJuridica> listaPerJuridica;
    
    private List<TipoEstablecimiento> listaTipoEstablecimientos;
    private List<TipoEstablecimiento> listaTipoEstablecimientosFilter;
    private TipoEstablecimiento selectTipoEstablecimiento;
    private List<TipoEstablecimiento> comboTipoEstablecimientos;
    private List<TipoEstablecimiento> listaTipoEstablecimiento;
    
    private List<Actividad> listaActividad;
    private List<Actividad> listaActividadFilter;
    private Actividad selectActividad;
    private List<Actividad> comboActividad;
    
    private List<Domicilio> listaDomicilio;
    private List<Domicilio> listaDomicilioFilter;
    private Domicilio selectDomicilio;
    private List<Domicilio> comboDomicilio;
    
    private List<Estado> listaEstado;
    private List<Estado> listaEstadoFilter;
    private Estado selectEstado;
    private List<Estado> comboEstado;
    
    private List<PerFisica> listaPerFisicas;
    private List<PerFisica> listaPerFisicasFilter;

    
    

    /**
     * Creates a new instance of MbEstablecimiento
     */
    public MbEstablecimiento() {
    }

    public EstadoFacade getEstadoFacade() {
        return estadoFacade;
    }

    public void setEstadoFacade(EstadoFacade estadoFacade) {
        this.estadoFacade = estadoFacade;
    }

    public List<Estado> getListaEstado() {
        return listaEstado;
    }

    public void setListaEstado(List<Estado> listaEstado) {
        this.listaEstado = listaEstado;
    }

    public List<Estado> getListaEstadoFilter() {
        return listaEstadoFilter;
    }

    public void setListaEstadoFilter(List<Estado> listaEstadoFilter) {
        this.listaEstadoFilter = listaEstadoFilter;
    }

    public Estado getSelectEstado() {
        return selectEstado;
    }

    public void setSelectEstado(Estado selectEstado) {
        this.selectEstado = selectEstado;
    }

    public List<Estado> getComboEstado() {
        return comboEstado;
    }

    public void setComboEstado(List<Estado> comboEstado) {
        this.comboEstado = comboEstado;
    }

    public DomicilioFacade getDomicilioFacade() {
        return domicilioFacade;
    }

    public void setDomicilioFacade(DomicilioFacade domicilioFacade) {
        this.domicilioFacade = domicilioFacade;
    }

    public List<Domicilio> getListaDomicilio() {
        return listaDomicilio;
    }

    public void setListaDomicilio(List<Domicilio> listaDomicilio) {
        this.listaDomicilio = listaDomicilio;
    }

    public List<Domicilio> getListaDomicilioFilter() {
        return listaDomicilioFilter;
    }

    public void setListaDomicilioFilter(List<Domicilio> listaDomicilioFilter) {
        this.listaDomicilioFilter = listaDomicilioFilter;
    }

    public Domicilio getSelectDomicilio() {
        return selectDomicilio;
    }

    public void setSelectDomicilio(Domicilio selectDomicilio) {
        this.selectDomicilio = selectDomicilio;
    }

    public List<Domicilio> getComboDomicilio() {
        return comboDomicilio;
    }

    public void setComboDomicilio(List<Domicilio> comboDomicilio) {
        this.comboDomicilio = comboDomicilio;
    }
    
    public TipoEstablecimientoFacade getTipoEstablecimientoFacade() {
        return tipoEstablecimientoFacade;
    }

    public void setTipoEstablecimientoFacade(TipoEstablecimientoFacade tipoEstablecimientoFacade) {
        this.tipoEstablecimientoFacade = tipoEstablecimientoFacade;
    }

    public ActividadFacade getActividadFacade() {
        return actividadFacade;
    }

    public void setActividadFacade(ActividadFacade actividadFacade) {
        this.actividadFacade = actividadFacade;
    }

    public List<Actividad> getListaActividad() {
        return listaActividad;
    }

    public void setListaActividad(List<Actividad> listaActividad) {
        this.listaActividad = listaActividad;
    }

    public List<Actividad> getListaActividadFilter() {
        return listaActividadFilter;
    }

    public void setListaActividadFilter(List<Actividad> listaActividadFilter) {
        this.listaActividadFilter = listaActividadFilter;
    }

    public Actividad getSelectActividad() {
        return selectActividad;
    }

    public void setSelectActividad(Actividad selectActividad) {
        this.selectActividad = selectActividad;
    }

    public List<Actividad> getComboActividad() {
        return comboActividad;
    }

    public void setComboActividad(List<Actividad> comboActividad) {
        this.comboActividad = comboActividad;
    }

    public List<TipoEstablecimiento> getListaTipoEstablecimientos() {
        return listaTipoEstablecimientos;
    }

    public void setListaTipoEstablecimientos(List<TipoEstablecimiento> listaTipoEstablecimientos) {
        this.listaTipoEstablecimientos = listaTipoEstablecimientos;
    }

    public List<TipoEstablecimiento> getListaTipoEstablecimientosFilter() {
        return listaTipoEstablecimientosFilter;
    }

    public void setListaTipoEstablecimientosFilter(List<TipoEstablecimiento> listaTipoEstablecimientosFilter) {
        this.listaTipoEstablecimientosFilter = listaTipoEstablecimientosFilter;
    }

    public TipoEstablecimiento getSelectTipoEstablecimiento() {
        return selectTipoEstablecimiento;
    }

    public void setSelectTipoEstablecimiento(TipoEstablecimiento selectTipoEstablecimiento) {
        this.selectTipoEstablecimiento = selectTipoEstablecimiento;
    }

    public List<TipoEstablecimiento> getComboTipoEstablecimientos() {
        return comboTipoEstablecimientos;
    }

    public void setComboTipoEstablecimientos(List<TipoEstablecimiento> comboTipoEstablecimientos) {
        this.comboTipoEstablecimientos = comboTipoEstablecimientos;
    }

    public List<TipoEstablecimiento> getListaTipoEstablecimiento() {
        return listaTipoEstablecimiento;
    }

    public void setListaTipoEstablecimiento(List<TipoEstablecimiento> listaTipoEstablecimiento) {
        this.listaTipoEstablecimiento = listaTipoEstablecimiento;
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

    public List<PerJuridica> getListaPerJuridicas() {
        return listaPerJuridicas;
    }

    public void setListaPerJuridicas(List<PerJuridica> listaPerJuridicas) {
        this.listaPerJuridicas = listaPerJuridicas;
    }

    public List<PerFisica> getListaPerFisicas() {
        return listaPerFisicas;
    }

    public void setListaPerFisicas(List<PerFisica> listaPerFisicas) {
        this.listaPerFisicas = listaPerFisicas;
    }

    public List<PerJuridica> getListaPerJuridicasFilter() {
        return listaPerJuridicasFilter;
    }

    public void setListaPerJuridicasFilter(List<PerJuridica> listaPerJuridicasFilter) {
        this.listaPerJuridicasFilter = listaPerJuridicasFilter;
    }

    public List<PerFisica> getListaPerFisicasFilter() {
        return listaPerFisicasFilter;
    }

    public void setListaPerFisicasFilter(List<PerFisica> listaPerFisicasFilter) {
        this.listaPerFisicasFilter = listaPerFisicasFilter;
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
    
    public Establecimiento getCurrent() {
        return current;
    }

    public void setCurrent(Establecimiento current) {
        this.current = current;
    }

    public List<Establecimiento> getListado() {
        if (listado == null || listado.isEmpty()) {
            listado = getFacade().findAll();
        }
        return listado;
    }

    public void setListado(List<Establecimiento> listado) {
        this.listado = listado;
    }

    public List<Establecimiento> getListaFilter() {
        return listaFilter;
    }

    public void setListaFilter(List<Establecimiento> listaFilter) {
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

    public EstablecimientoFacade getEstablecimientoFacade() {
        return establecimientoFacade;
    }

    public void setEstablecimientoFacade(EstablecimientoFacade establecimientoFacade) {
        this.establecimientoFacade = establecimientoFacade;
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
    
    public Establecimiento getSelected() {
        if (current == null) {
            current = new Establecimiento();
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
    
    private EstablecimientoFacade getFacade() {
        return establecimientoFacade;
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("EstablecimientoDeletedErrorOccured"));
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
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EstablecimientoNonDeletable"));            
        }
    } 
    
    /**
     * METODOS DE SELECCION
     */
        /**
     * @return la totalidad de las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(establecimientoFacade.findAll(), false);
    }

    /**
     * @return de a una las entidades persistidas formateadas
     */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(establecimientoFacade.findAll(), true);
    }

    private Establecimiento getEstablecimiento(java.lang.Long id) {
        return establecimientoFacade.find(id);
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

    public void perJuridicaChangeListener(ValueChangeEvent event) {      
        selectPerJuridica = (PerJuridica)event.getNewValue();      
        comboPerJuridicas = perJuridicaFacade.getNombres(selectPerJuridica);      
    }
    
    public void tipoEstablecimientoChangeListener(ValueChangeEvent event) {      
        selectTipoEstablecimiento = (TipoEstablecimiento)event.getNewValue();      
        comboTipoEstablecimientos = tipoEstablecimientoFacade.getNombres(selectTipoEstablecimiento);      
    }
    
    public void actividadChangeListener(ValueChangeEvent event) {      
        selectActividad = (Actividad)event.getNewValue();      
        comboActividad = actividadFacade.getNombres(selectActividad);      
    }
    
    public void domicilioChangeListener(ValueChangeEvent event) {      
        selectDomicilio = (Domicilio)event.getNewValue();      
        comboDomicilio = domicilioFacade.getNombres(selectDomicilio);      
    }
    
    public void estadoChangeListener(ValueChangeEvent event) {      
        selectEstado = (Estado)event.getNewValue();      
        comboEstado = estadoFacade.getNombres(selectEstado);      
    }
 
}
