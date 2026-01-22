package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioUnidadeId implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY KEY ATTRIBUTES
    private String usuCodigo;

    private String uniCodigo;

    public UsuarioUnidadeId() {
        super();
    }

    public UsuarioUnidadeId(String usuCodigo, String uniCodigo) {
        super();
        this.usuCodigo = usuCodigo;
        this.uniCodigo = uniCodigo;
    }

    //--- GETTERS & SETTERS FOR KEY FIELDS
    public void setUsuCodigo(String value) {
        usuCodigo = value;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUniCodigo(String value) {
        uniCodigo = value;
    }

    public String getUniCodigo() {
        return uniCodigo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((usuCodigo == null) ? 0 : usuCodigo.hashCode());
        result = prime * result + ((uniCodigo == null) ? 0 : uniCodigo.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UsuarioUnidadeId other = (UsuarioUnidadeId) obj;
        return Objects.equals(uniCodigo, other.uniCodigo) && Objects.equals(usuCodigo, other.usuCodigo);
    }

    @Override
    public String toString() {
        return "UsuarioUnidadeId [usuCodigo=" + usuCodigo + ", uniCodigo=" + uniCodigo + "]";
    }
}
