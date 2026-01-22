package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TipoMotivoOperacaoTransferObject</p>
 * <p>Description: Transfer Object do tipo motivo da operação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoMotivoOperacaoTransferObject extends CustomTransferObject {

    public TipoMotivoOperacaoTransferObject() {
        super();
    }

    public TipoMotivoOperacaoTransferObject(String tmoCodigo) {
        this();
        setAttribute(Columns.TMO_CODIGO, tmoCodigo);
    }

    public TipoMotivoOperacaoTransferObject(TipoMotivoOperacaoTransferObject tmo) {
        this();
        setAtributos(tmo.getAtributos());
    }

    public String getTmoCodigo() {
        return (String) getAttribute(Columns.TMO_CODIGO);
    }

    public String getTenCodigo() {
        return (String) getAttribute(Columns.TMO_TEN_CODIGO);
    }

    public String getTmoDescricao() {
        return (String) getAttribute(Columns.TMO_DESCRICAO);
    }

    public String getTmoIdentificador() {
        return (String) getAttribute(Columns.TMO_IDENTIFICADOR);
    }

    public Short getTmoAtivo() {
        return (Short) getAttribute(Columns.TMO_ATIVO);
    }

    public String getTmoExigeObs() {
        return (String) getAttribute(Columns.TMO_EXIGE_OBS);
    }

    public String getTmoDecisaoJudicial() {
        return (String) getAttribute(Columns.TMO_DECISAO_JUDICIAL);
    }

    public void setTmoCodigo(String tmoCodigo) {
        setAttribute(Columns.TMO_CODIGO, tmoCodigo);
    }

    public void setTenCodigo(String tenCodigo) {
        setAttribute(Columns.TMO_TEN_CODIGO, tenCodigo);
    }

    public void setTmoDescricao(String tmoDescricao) {
        setAttribute(Columns.TMO_DESCRICAO, tmoDescricao);
    }

    public void setTmoIdentificador(String tmoIdentificador) {
        setAttribute(Columns.TMO_IDENTIFICADOR, tmoIdentificador);
    }

    public void setTmoAtivo(Short tmoAtivo) {
        setAttribute(Columns.TMO_ATIVO, tmoAtivo);
    }

    public void setTmoExigeObs(String tmoExigeObs) {
        setAttribute(Columns.TMO_EXIGE_OBS, tmoExigeObs);
    }

    public void setTmoDecisalJudicial(String tmoDecisaoJudicial) {
        setAttribute(Columns.TMO_DECISAO_JUDICIAL, tmoDecisaoJudicial);
    }
}
