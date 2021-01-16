package com.longriver.kejiapower.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class InnerClient extends Client {
    private BooleanProperty selected = new SimpleBooleanProperty();
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
