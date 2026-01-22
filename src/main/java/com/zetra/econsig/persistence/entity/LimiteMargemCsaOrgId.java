package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class LimiteMargemCsaOrgId implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY KEY ATTRIBUTES
    private Short marCodigo;
    private String csaCodigo;
    private String orgCodigo;

    public LimiteMargemCsaOrgId() {
        super();
    }

    public Short getMarCodigo() {
        return marCodigo;
    }

    public void setMarCodigo(Short marCodigo) {
        this.marCodigo = marCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;

        result = prime * result + ((csaCodigo == null) ? 0 : csaCodigo.hashCode());
        result = prime * result + ((marCodigo == null) ? 0 : marCodigo.hashCode());
        result = prime * result + ((orgCodigo == null) ? 0 : orgCodigo.hashCode());

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
        LimiteMargemCsaOrgId other = (LimiteMargemCsaOrgId) obj;
        return Objects.equals(csaCodigo, other.csaCodigo) && Objects.equals(marCodigo, other.marCodigo) && Objects.equals(orgCodigo, other.orgCodigo);
    }

    @Override
    public String toString() {
        return "LimiteMargemCsaOrgId [marCodigo=" + marCodigo + ", csaCodigo=" + csaCodigo + ", orgCodigo=" + orgCodigo + "]";
    }
}
