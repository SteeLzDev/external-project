package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Title: SuspenderConsignacaoParametros</p>
 * <p>Description: Parâmetros necessários na suspensão de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SuspenderConsignacaoParametros extends Parametros {

    private Collection<File> anexos;
    private String[] visibilidadeAnexos;
    private boolean removeIncidenciaMargem;
    private Date dataReativacaoAutomatica;
    private boolean alteraIncidenciaMargem;
    private Short marCodigo;

    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String cidCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;
    private boolean suspendeCse = true; //Define se deve ser uma suspensão por cse (sad_codigo = 10) ou não (sad_codigo = 6). Só deve ser verificado para papeis CSE/SUP/ORG. Default true.

    public Collection<File> getAnexos() {
        return anexos;
    }

    public void setAnexos(Collection<File> anexos) {
        this.anexos = anexos;
    }

    public String[] getVisibilidadeAnexos() {
        return visibilidadeAnexos;
    }

    public void setVisibilidadeAnexos(String[] visibilidadeAnexos) {
        this.visibilidadeAnexos = visibilidadeAnexos;
    }

    public boolean isRemoveIncidenciaMargem() {
        return removeIncidenciaMargem;
    }

    public void setRemoveIncidenciaMargem(boolean removeIncidenciaMargem) {
        this.removeIncidenciaMargem = removeIncidenciaMargem;
    }

    public Date getDataReativacaoAutomatica() {
        return dataReativacaoAutomatica;
    }

    public void setDataReativacaoAutomatica(Date dataReativacaoAutomatica) {
        this.dataReativacaoAutomatica = dataReativacaoAutomatica;
    }

    public boolean isAlteraIncidenciaMargem() {
        return alteraIncidenciaMargem;
    }

    public void setAlteraIncidenciaMargem(boolean alteraIncidenciaMargem) {
        this.alteraIncidenciaMargem = alteraIncidenciaMargem;
    }

    public Short getMarCodigo() {
        return marCodigo;
    }

    public void setMarCodigo(Short marCodigo) {
        this.marCodigo = marCodigo;
    }

    public String getTjuCodigo() {
        return tjuCodigo;
    }

    public void setTjuCodigo(String tjuCodigo) {
        this.tjuCodigo = tjuCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
    }

    public String getDjuNumProcesso() {
        return djuNumProcesso;
    }

    public void setDjuNumProcesso(String djuNumProcesso) {
        this.djuNumProcesso = djuNumProcesso;
    }

    public Date getDjuData() {
        return djuData;
    }

    public void setDjuData(Date djuData) {
        this.djuData = djuData;
    }

    public String getDjuTexto() {
        return djuTexto;
    }

    public void setDjuTexto(String djuTexto) {
        this.djuTexto = djuTexto;
    }

    public boolean isSuspendeCse() {
        return suspendeCse;
    }

    public void setSuspendeCse(boolean suspendeCse) {
        this.suspendeCse = suspendeCse;
    }
}
