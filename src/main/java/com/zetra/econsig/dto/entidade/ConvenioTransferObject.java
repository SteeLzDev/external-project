package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

public class ConvenioTransferObject extends CustomTransferObject {
    public ConvenioTransferObject() {
        super();
    }

    public ConvenioTransferObject(String cnvCodigo) {
        this();
        setAttribute(Columns.CNV_CODIGO, cnvCodigo);
    }

    public ConvenioTransferObject(ConvenioTransferObject convenio) {
        this();
        setAtributos(convenio.getAtributos());
    }

    public String getCnvCodigo() {
        return (String) getAttribute(Columns.CNV_CODIGO);
    }

    public void setCnvCodigo(String cnvCodigo) {
        setAttribute(Columns.CNV_CODIGO, cnvCodigo);
    }

    public String getOrgCodigo() {
        return (String) getAttribute(Columns.CNV_ORG_CODIGO);
    }

    public void setOrgCodigo(String orgCodigo) {
        setAttribute(Columns.CNV_ORG_CODIGO, orgCodigo);
    }

    public String getScvCodigo() {
        return (String) getAttribute(Columns.CNV_SCV_CODIGO);
    }

    public void setScvCodigo(String scvCodigo) {
        setAttribute(Columns.CNV_SCV_CODIGO, scvCodigo);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.CNV_SVC_CODIGO);
    }

    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.CNV_SVC_CODIGO, svcCodigo);
    }

    public String getCsaCodigo() {
        return (String) getAttribute(Columns.CNV_CSA_CODIGO);
    }

    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.CNV_CSA_CODIGO, csaCodigo);
    }

    public String getVceCodigo() {
        return (String) getAttribute(Columns.CNV_VCE_CODIGO);
    }

    public void setVceCodigo(String vceCodigo) {
        setAttribute(Columns.CNV_VCE_CODIGO, vceCodigo);
    }

    public String getCnvIdentificador() {
        return (String) getAttribute(Columns.CNV_IDENTIFICADOR);
    }

    public void setCnvIdentificador(String cnvIdentificador) {
        setAttribute(Columns.CNV_IDENTIFICADOR, cnvIdentificador);
    }

    public String getCnvDescricao() {
        return (String) getAttribute(Columns.CNV_DESCRICAO);
    }

    public void setCnvDescricao(String cnvDescricao) {
        setAttribute(Columns.CNV_DESCRICAO, cnvDescricao);
    }

    public String getCnvCodVerba() {
        return (String) getAttribute(Columns.CNV_COD_VERBA);
    }

    public void setCnvCodVerba(String cnvCodVerba) {
        setAttribute(Columns.CNV_COD_VERBA, cnvCodVerba);
    }

    public String getCnvCodVerbaRef() {
        return (String) getAttribute(Columns.CNV_COD_VERBA_REF);
    }

    public void setCnvCodVerbaRef(String cnvCodVerbaRef) {
        setAttribute(Columns.CNV_COD_VERBA_REF, cnvCodVerbaRef);
    }

    public String getCnvCodVerbaFerias() {
        return (String) getAttribute(Columns.CNV_COD_VERBA_FERIAS);
    }

    public void setCnvCodVerbaFerias(String cnvCodVerbaFerias) {
        setAttribute(Columns.CNV_COD_VERBA_FERIAS, cnvCodVerbaFerias);
    }

    public String getCnvConsolidaDescontos() {
        return (String) getAttribute(Columns.CNV_CONSOLIDA_DESCONTOS);
    }

    public void setCnvConsolidaDescontos(String cnvCnvConsolidaDescontos) {
        setAttribute(Columns.CNV_CONSOLIDA_DESCONTOS, cnvCnvConsolidaDescontos);
    }

    public String getCnvPrioridade() {
        return (String) getAttribute(Columns.CNV_PRIORIDADE);
    }

    public void setCnvPrioridade(String cnvCnvPrioridade) {
        setAttribute(Columns.CNV_PRIORIDADE, cnvCnvPrioridade);
    }
}
