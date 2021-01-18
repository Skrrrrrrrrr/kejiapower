package com.longriver.kejiapower.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


//构造新实体，为了在配置页tableview展示，增加了一项：selected
public class InnerClient extends Client {

    private StringProperty controlled = new SimpleStringProperty();
    private BooleanProperty selected = new SimpleBooleanProperty();

    public String getControlled() {
        return controlled.get();
    }

    public StringProperty controlledProperty() {
        return controlled;
    }

    public void setControlled(String controlled) {
        this.controlled.set(controlled);
    }

//        private BooleanProperty control = new SimpleBooleanProperty();

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
