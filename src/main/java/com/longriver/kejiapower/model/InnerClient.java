package com.longriver.kejiapower.model;

import javafx.beans.property.*;


//构造新实体，为了在配置页tableview展示，增加了一项：selected
public class InnerClient extends Client {

    private IntegerProperty gap = new SimpleIntegerProperty(0);//设置自动测试时，每个状态的时间间隔
    private StringProperty controlled = new SimpleStringProperty();
    private BooleanProperty selected = new SimpleBooleanProperty();

    public int getGap() {
        return gap.get();
    }

    public IntegerProperty gapProperty() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap.set(gap);
    }

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
