package com.zetra.econsig.dto.entidade;

import java.sql.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OcorrenciaUsuarioTransferObject</p>
 * <p>Description: Transfer Object para Ocorrência de Usuário.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaUsuarioTransferObject extends CustomTransferObject {

    private boolean utilizacaoSenhaAutServidor;

    public OcorrenciaUsuarioTransferObject() {
        super();
    }

    public OcorrenciaUsuarioTransferObject(String ousCodigo) {
        this();
        setAttribute(Columns.OUS_CODIGO, ousCodigo);
    }

    public void setOusCodigo(String ousCodigo) {
        setAttribute(Columns.OUS_CODIGO, ousCodigo);
    }

    public String getOusCodigo() {
        return (String) getAttribute(Columns.OUS_CODIGO);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.OUS_USU_CODIGO);
    }

    public void setTocCodigo(String tocCodigo) {
        setAttribute(Columns.OUS_TOC_CODIGO, tocCodigo);
    }

    public String getTocCodigo() {
        return (String) getAttribute(Columns.OUS_TOC_CODIGO);
    }

    public void setOusUsuCodigo(String ousUsuCodigo) {
        setAttribute(Columns.OUS_OUS_USU_CODIGO, ousUsuCodigo);
    }

    public String getOusUsuCodigo() {
        return (String) getAttribute(Columns.OUS_OUS_USU_CODIGO);
    }

    public void setOusData(Date ousData) {
        setAttribute(Columns.OUS_DATA, ousData);
    }

    public String getOusData() {
        return (String) getAttribute(Columns.OUS_DATA);
    }

    public void setOusObs(String ousObs) {
        setAttribute(Columns.OUS_OBS, ousObs);
    }

    public String getOusObs() {
        return (String) getAttribute(Columns.OUS_OBS);
    }

    public void setOusIpAcesso(String ousIpAcesso) {
        setAttribute(Columns.OUS_IP_ACESSO, ousIpAcesso);
    }

    public String getOusIpAcesso() {
        return (String) getAttribute(Columns.OUS_IP_ACESSO);
    }

    public boolean isUtilizacaoSenhaAutServidor() {
        return utilizacaoSenhaAutServidor;
    }

    public void setUtilizacaoSenhaAutServidor(boolean utilizacaoSenhaAutServidor) {
        this.utilizacaoSenhaAutServidor = utilizacaoSenhaAutServidor;
    }
}
