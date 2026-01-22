package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class VinculoCsaRseId implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY KEY ATTRIBUTES
    private String vrsCodigo;

    private String vcsCodigo;

    public VinculoCsaRseId() {
        super();
    }

    public String getVrsCodigo() {
        return vrsCodigo;
    }

    public void setVrsCodigo(String vrsCodigo) {
        this.vrsCodigo = vrsCodigo;
    }

    public String getVcsCodigo() {
        return vcsCodigo;
    }

    public void setVcsCodigo(String vcsCodigo) {
        this.vcsCodigo = vcsCodigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vcsCodigo, vrsCodigo);
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
        VinculoCsaRseId other = (VinculoCsaRseId) obj;
        return Objects.equals(vcsCodigo, other.vcsCodigo) && Objects.equals(vrsCodigo, other.vrsCodigo);
    }

    @Override
    public String toString() {
        return "VinculoCsaRseId [vrsCodigo=" + vrsCodigo + ", vcsCodigo=" + vcsCodigo + "]";
    }
}
