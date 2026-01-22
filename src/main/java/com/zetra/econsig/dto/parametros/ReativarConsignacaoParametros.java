package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Title: ReativarConsiganacaoParametros</p>
 * <p>Description: Parâmetros necessários na reativação de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 28481 $
 * $Date: 2019-12-26 16:54:10 -0300 (qui, 26 dez 2019) $
 */
public class ReativarConsignacaoParametros extends Parametros {
    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String cidCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;
    private boolean alteraIncidenciaMargem;
    private Short marCodigo;

    private String[] visibilidadeAnexos;
    private Collection<File> anexos;

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
}
