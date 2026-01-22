package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class ParamPostoCsaSvcId implements Serializable {

    private static final long serialVersionUID = 2L;

    private String posCodigo;
    private String svcCodigo;
    private String tpsCodigo;
    private String csaCodigo;

    public ParamPostoCsaSvcId() {
        super();
    }

    public ParamPostoCsaSvcId(String posCodigo, String svcCodigo, String tpsCodigo, String csaCodigo) {
        super();
        this.csaCodigo = csaCodigo;
        this.svcCodigo = svcCodigo;
        this.tpsCodigo = tpsCodigo;
        this.posCodigo = posCodigo;
    }

    public String getPosCodigo() {
        return posCodigo;
    }

    public void setPosCodigo(String posCodigo) {
        this.posCodigo = posCodigo;
    }

    public String getSvcCodigo() {
        return svcCodigo;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public String getTpsCodigo() {
        return tpsCodigo;
    }

    public void setTpsCodigo(String tpsCodigo) {
        this.tpsCodigo = tpsCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(csaCodigo, posCodigo, svcCodigo, tpsCodigo);
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
        ParamPostoCsaSvcId other = (ParamPostoCsaSvcId) obj;
        return Objects.equals(csaCodigo, other.csaCodigo) && Objects.equals(posCodigo, other.posCodigo) && Objects.equals(svcCodigo, other.svcCodigo) && Objects.equals(tpsCodigo, other.tpsCodigo);
    }
}
