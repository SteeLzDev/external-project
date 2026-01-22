package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ServicoTransferObject</p>
 * <p>Description: Transfer Object do Servico</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoTransferObject extends CustomTransferObject {

    public ServicoTransferObject() {
        super();
    }

    public ServicoTransferObject(String svcCodigo) {
        this();
        setAttribute(Columns.SVC_CODIGO, svcCodigo);
    }

    public ServicoTransferObject(ServicoTransferObject servico) {
        this();
        setAtributos(servico.getAtributos());
    }

    // Getter
    public String getSvcCodigo() {
        return (String) getAttribute(Columns.SVC_CODIGO);
    }

    public String getSvcIdentificador() {
        return (String) getAttribute(Columns.SVC_IDENTIFICADOR);
    }

    public String getSvcDescricao() {
        return (String) getAttribute(Columns.SVC_DESCRICAO);
    }

    public Short getSvcAtivo() {
        return (Short) getAttribute(Columns.SVC_ATIVO);
    }

    public String getSvcTgsCodigo() {
        return (String) getAttribute(Columns.SVC_TGS_CODIGO);
    }

    public Integer getSvcPrioridade() {
        return (Integer) getAttribute(Columns.SVC_PRIORIDADE);
    }

    public String getSvcNseCodigo() {
        return (String) getAttribute(Columns.SVC_NSE_CODIGO);
    }

    public String getSvcObs() {
        return (String) getAttribute(Columns.SVC_OBS);
    }

    // Setter
    public void setSvcIdentificador(String svcIdentificador) {
        setAttribute(Columns.SVC_IDENTIFICADOR, svcIdentificador);
    }

    public void setSvcDescricao(String svcDescricao) {
        setAttribute(Columns.SVC_DESCRICAO, svcDescricao);
    }

    public void setSvcAtivo(Short svcAtivo) {
        setAttribute(Columns.SVC_ATIVO, svcAtivo);
    }

    public void setSvcTgsCodigo(String svcTgsCodigo) {
        setAttribute(Columns.SVC_TGS_CODIGO, svcTgsCodigo);
    }

    public void setSvcPrioridade(Integer svcPrioridade) {
        setAttribute(Columns.SVC_PRIORIDADE, svcPrioridade);
    }

    public void setSvcNseCodigo(String svcNseCodigo) {
        setAttribute(Columns.SVC_NSE_CODIGO, svcNseCodigo);
    }

    public void setSvcObs(String svcObs) {
        setAttribute(Columns.SVC_OBS, svcObs);
    }


    // Dados de motivo de operação
    public String getTmoCodigo() {
        return (String) getAttribute(Columns.TMO_CODIGO);
    }

    public String getOseObs() {
        return (String) getAttribute(Columns.OSE_OBS);
    }

    public void setTmoCodigo(String tmoCodigo) {
        setAttribute(Columns.TMO_CODIGO, tmoCodigo);
    }

    public void setOseObs(String oseObs) {
        setAttribute(Columns.OSE_OBS, oseObs);
    }
}
