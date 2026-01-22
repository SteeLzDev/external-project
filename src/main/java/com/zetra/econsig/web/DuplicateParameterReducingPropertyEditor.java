package com.zetra.econsig.web;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

public class DuplicateParameterReducingPropertyEditor extends PropertyEditorSupport {

    private Object property;

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String[]) {
            String[] strings = (String[]) value;
            Set<String> unique = new HashSet<String>();
            for (String string : strings) {
                unique.add(string);
            }
            property = unique.toArray();
        } else {
            property = value;
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        property = text;
    }

    @Override
    public String getAsText() {
        return (property != null ? property.toString() : null);
    }

    @Override
    public Object getValue() {
        return property;
    }
}
