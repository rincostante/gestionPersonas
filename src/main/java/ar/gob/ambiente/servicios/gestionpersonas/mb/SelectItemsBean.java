/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.servicios.gestionpersonas.mb;

import ar.gob.ambiente.servicios.gestionpersonas.entidades.util.ExampleEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import org.omnifaces.util.selectitems.SelectItemsBuilder;

/**
 *
 * @author rincostante
 */
public class SelectItemsBean implements Serializable{

private static final long serialVersionUID = 1L;

    // 3 different forms of the defining data to which SelectItems can bind.
    private List<ExampleEntity> exampleEntities;
    private List<SelectItem> selectItems;
    private SelectItem[] selectItemArray;

    private ExampleEntity selectedEntity;

    @PostConstruct
    public void init() {
        exampleEntities = new ArrayList<>();
        exampleEntities.add(new ExampleEntity(1L, "Amsterdam"));
        exampleEntities.add(new ExampleEntity(2L, "Frankfurt"));
        exampleEntities.add(new ExampleEntity(3L, "London"));

        selectItems = new SelectItemsBuilder()
                            .add(new ExampleEntity(4L, "New York"), "New York")
                            .add(new ExampleEntity(5L, "Miami"), "Miami")
                            .add(new ExampleEntity(6L, "Los Angeles"), "Los Angeles")
                            .buildList();

        selectItemArray = new SelectItemsBuilder()
                            .add(new ExampleEntity(7L, "Willemstad"), "Willemstad")
                            .add(new ExampleEntity(8L, "Oranjestad"), "Oranjestad")
                            .add(new ExampleEntity(9L, "Kralendijk"), "Kralendijk")
                            .build();
    }

    public List<ExampleEntity> getExampleEntities() {
        return exampleEntities;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public SelectItem[] getSelectItemArray() {
        return selectItemArray;
    }

    public ExampleEntity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(ExampleEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
    
    public void pruebaChanceListener(){
        System.out.println("Pasó el listener!");
        ExampleEntity prueba = selectedEntity;
        
        System.out.println("La entidad seleccionada es: " + selectedEntity.getValue());
        System.out.println("Y su id es: " + selectedEntity.getId());
    }
    
}
