package com.zetra.econsig.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Title: CustomTransferObject</p>
 * <p>Description: Custom Transfer Object</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class CustomTransferObject implements Serializable, TransferObject {

    private static final long serialVersionUID = -6773945400648786212L;

    private final Map<String, Object> atributos;

    public CustomTransferObject() {
        atributos = new HashMap<>();
    }

    public CustomTransferObject(TransferObject to) {
        atributos = new HashMap<>();
        atributos.putAll(to.getAtributos());
    }

    @Override
    public Map<String, Object> getAtributos() {
        return Collections.unmodifiableMap(atributos);
    }

    @Override
    public void setAtributos(Map<String, Object> atributos) {
        this.atributos.putAll(atributos);
    }

    @Override
    public void setAttribute(String name, Object value) {
        atributos.put(name, value);
    }

    public void remove(String name) {
        atributos.remove(name);
    }

    public void removeAll(CustomTransferObject cto) {
        for (final String name : cto.atributos.keySet()) {
            remove(name);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return atributos.get(name);
    }

    @Override
    public String toString() {
        final StringBuilder retorno = new StringBuilder();
        final Iterator<String> ite = atributos.keySet().iterator();
        while (ite.hasNext()) {
            final String chave = ite.next();
            final String valor = atributos.get(chave) != null ? atributos.get(chave).toString() : "";

            retorno.append("{").append(chave).append(": ").append(valor);
            if (ite.hasNext()) {
                retorno.append("}, ");
            } else {
                retorno.append("}");
            }
        }

        return retorno.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomTransferObject) {
            return false;
        }

        final Map<String, Object> thisMap = getAtributos();
        final Map<String, Object> outerMap = ((CustomTransferObject) obj).getAtributos();

        if (thisMap == null && outerMap == null) {
            return true;
        } else if (thisMap == null || outerMap == null) {
            return false;
        }

        return thisMap.equals(outerMap);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}