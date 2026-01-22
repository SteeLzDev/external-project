package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OcorrenciaParamSistCseTO</p>
 * <p>Description: Transfer Object das ocorrências de parametros de sistema da consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 */
public class OcorrenciaParamSistCseTO extends CustomTransferObject {

    public OcorrenciaParamSistCseTO() {
        super();
    }

    public OcorrenciaParamSistCseTO(String opsCodigo) {
        this();
        setAttribute(Columns.OPS_CODIGO, opsCodigo);
    }

    public OcorrenciaParamSistCseTO(OcorrenciaParamSistCseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    public OcorrenciaParamSistCseTO(TransferObject param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getOpsCodigo() {
        return (String) getAttribute(Columns.OPS_CODIGO);
    }

    public Date getOpsData() {
        return (Date) getAttribute(Columns.OPS_DATA);
    }

    public String getOpsObs() {
        return (String) getAttribute(Columns.OPS_OBS);
    }

    public String getOpsIpAcesso() {
        return (String) getAttribute(Columns.OPS_IP_ACESSO);
    }

    public String getTocCodigo() {
        return (String) getAttribute(Columns.TOC_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.USU_CODIGO);
    }

    public String getUsuLogin() {
        return (String) getAttribute(Columns.USU_LOGIN);
    }

    public String getTpcCodigo() {
        return (String) getAttribute(Columns.TPC_CODIGO);
    }

    public String getTpcDescricao() {
        return (String) getAttribute(Columns.TPC_DESCRICAO);
    }

    public String getCseCodigo() {
        return (String) getAttribute(Columns.CSE_CODIGO);
    }

    // Setter
    public void setOpsCodigo(String opsCodigo) {
        setAttribute(Columns.OPS_CODIGO, opsCodigo);
    }

    public void getOpsData(Date opsData) {
        setAttribute(Columns.OPS_DATA, opsData);
    }

    public void setOpsObs(String opsObs) {
        setAttribute(Columns.OPS_OBS, opsObs);
    }

    public void setOpsIpAcesso(String opsIpAcesso) {
        setAttribute(Columns.OPS_IP_ACESSO, opsIpAcesso);
    }

    public void setTocCodigo(String tocCodigo) {
        setAttribute(Columns.TOC_CODIGO, tocCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.USU_CODIGO, usuCodigo);
    }

    public void setTpcCodigo(String tpcCodigo) {
        setAttribute(Columns.TPC_CODIGO, tpcCodigo);
    }

    public void setUsuLogin(String usuLogin) {
        setAttribute(Columns.USU_LOGIN, usuLogin);
    }

    public void setTpcDescricao(String tpcDescricao) {
        setAttribute(Columns.TPC_DESCRICAO, tpcDescricao);
    }

    public void setCseCodigo(String cseCodigo) {
        setAttribute(Columns.CSE_CODIGO, cseCodigo);
    }

}
