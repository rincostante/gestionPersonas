/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades.util;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodriguezn
 */
@XmlRootElement
public class Categorias {
    private List<ParItemValor> lPares;

    public List<ParItemValor> getlPares() {
        return lPares;
    }

    @XmlElement
    public void setlPares(List<ParItemValor> lPares) {
        this.lPares = lPares;
    }
}