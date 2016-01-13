/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.entidades.util;

import java.io.Serializable;

/**
 *
 * @author rincostante
 */
public class ExampleEntity implements Serializable{
private static final long serialVersionUID = 1L;

    private Long id;
    private String value;

    public ExampleEntity() {
        //
    }

    public ExampleEntity(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof ExampleEntity) && (id != null)
            ? id.equals(((ExampleEntity) other).id)
            : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null)
            ? (this.getClass().hashCode() + id.hashCode())
            : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ExampleEntity[%d, %s]", id, value);
    }
}
