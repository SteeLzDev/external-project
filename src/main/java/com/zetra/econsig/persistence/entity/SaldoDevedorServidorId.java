package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>Title: SaldoDevedorServidorId</p>
 * <p>Description: Classe de Id para entidade saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */


public class SaldoDevedorServidorId implements Serializable {

    private static final long serialVersionUID = 2L;

    //--- ENTITY KEY ATTRIBUTES
    private String rseCodigo;

    private String csaCodigo;

    public SaldoDevedorServidorId() {
    }

    /**
     * Constructor with values
     *
     * @param rseCodigo
     * @param csaCodigo
     */
    public SaldoDevedorServidorId(String rseCodigo, String csaCodigo) {
        this.rseCodigo = rseCodigo;
        this.csaCodigo = csaCodigo;
    }

    //--- GETTERS & SETTERS FOR KEY FIELDS
    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    //--- equals METHOD
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        final SaldoDevedorServidorId other = (SaldoDevedorServidorId) obj;
        //--- Attribute rseCodigo
        //--- Attribute csaCodigo
        if (!Objects.equals(rseCodigo, other.rseCodigo) || !Objects.equals(csaCodigo, other.csaCodigo)) {
            return false;
        }
        return true;
    }

    //--- hashCode METHOD
    @Override
    public int hashCode() {
        return Objects.hash(rseCodigo, csaCodigo);
    }

    //--- toString METHOD
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(rseCodigo);
        sb.append("|");
        sb.append(csaCodigo);
        return sb.toString();
    }
}
