/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.Usuario;
import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.CriptPass;
import ar.gob.ambiente.servicios.gestionpersonas.facades.UsuarioFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.omnifaces.util.Faces;

/**
*
* @author carmendariz
*/
public class MbLogin implements Serializable{
    
    private static final long serialVersionUID = -2152389656664659476L;
    private String nombre;
    private String clave;
    private boolean logeado = false;   
    private String ambito;
    private Usuario usLogeado;
    private String claveAnterior_1;
    private String claveAnterior_2;
    private String claveNueva;
    @EJB
    private UsuarioFacade usuarioFacade;
    private List<String> listMbActivos;
    private boolean iniciado;
    
    /**
     * Creates a new instance of MbLogin
     */
    public MbLogin() {
    }
    
    /**
     * Método para inicializar el listado de los Mb activos
     */
    @PostConstruct
    public void init(){
        iniciado = false;
        listMbActivos = new ArrayList();
    }
/**
     * Método que borra de la memoria los MB innecesarios al cargar el listado 
     * @throws java.io.IOException
     */
    public void iniciar() throws IOException{
        if(iniciado){
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
        }else{
            // obtengo el nombre del usuario logeado de la cookie correspondiente
            String nomUsuario;
            String valueEnc = Faces.getRequestCookie(ResourceBundle.getBundle("/Bundle").getString("nameCookieUser"));
            
            try {
                // desencripto el nombre
                nomUsuario = CriptPass.desencriptar(valueEnc);
                
                // obtebtengo el usuario correspondiente al nombre desencriptado
                if(usuarioFacade.getUsuario(nomUsuario) != null){
                    usLogeado = usuarioFacade.getUsuario(nomUsuario);
                    ambito = usLogeado.getRol().getNombre();
                    iniciado = true;
                }else{
                    // si no tengo un usuario registrado en la aplicación para este nombre, redirecciono a la vista de error de acceso
                    FacesContext fc=FacesContext.getCurrentInstance();
                    fc.getExternalContext().redirect(ResourceBundle.getBundle("/Bundle").getString("logError"));
                }
            } catch (Exception ex) {
                Logger.getLogger(MbLogin.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }
    }      

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }
    
    public List<String> getListMbActivos() {
        return listMbActivos;
    }

    public void setListMbActivos(List<String> listMbActivos) {
        this.listMbActivos = listMbActivos;
    }

    public String getClaveAnterior_1() {
        return claveAnterior_1;
    }

    public void setClaveAnterior_1(String claveAnterior_1) {
       this.claveAnterior_1 = claveAnterior_1;
    }

    public String getClaveAnterior_2() {
        return claveAnterior_2;
    }

    public void setClaveAnterior_2(String claveAnterior_2) {
        this.claveAnterior_2 = claveAnterior_2;
    }

    public String getClaveNueva() {
        return claveNueva;
    }

    public void setClaveNueva(String claveNueva) {
        this.claveNueva = claveNueva;
    }

    public Usuario getUsLogeado() {
        return usLogeado;
    }

    public void setUsLogeado(Usuario usLogeado) {
        this.usLogeado = usLogeado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isLogeado() {
        return logeado;
    }

    public void setLogeado(boolean logeado) {
        this.logeado = logeado;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String rol) {
        this.ambito = rol;
    }
/*    
    public void login(ActionEvent actionEvent){
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;

        if (nombre != null && clave != null){
            // valido las credenciales recibidas
            if(validarInt()){
                logeado = true;
                
                //harcodeo los datos del usuario, por ahora Ceci
                usLogeado = new Usuario();
                usLogeado.setNombre("carmendariz");
                usLogeado.setId(Long.valueOf(1));

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", usLogeado.getNombre());
            }else{
                logeado = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error de inicio de sesión", "Usuario y/o contraseña invalidos");
            }
        }
        
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("estaLogeado", logeado);
        clave = "";
        
        if (logeado){
            context.addCallbackParam("view", ResourceBundle.getBundle("/Bundle").getString("RutaAplicacion"));
        }
    }
*/    
    
     /**
     * Método para revocar la sesión del MB
     * @return 
     */
/*    
    public String cleanUp(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.removeAttribute("mbLogin");
   
        return "inicio";
    }      
*/    
    
    
    public void logout(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        iniciado = false;
    }  
/*    
    private boolean validarInt(){
        return "admin".equals(nombre) && "admin".equals(clave);
    }
*/
}
