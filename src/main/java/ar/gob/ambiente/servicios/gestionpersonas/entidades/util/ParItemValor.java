/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades.util;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author rodriguezn
 */
@XmlType
public class ParItemValor{
    private List<String> lPar;

    public List<String> getlPar() {
        return lPar;
    }

    @XmlElement
    public void setlPar(List<String> lPar) {
        this.lPar = lPar;
    }
}
