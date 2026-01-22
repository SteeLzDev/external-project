package com.zetra.econsig.delegate;

import java.util.List;

import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: TipoMotivoOperacaoDelegate</p>
 * <p>Description: Delegate de tipo de motivo da operacao</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoMotivoOperacaoDelegate extends AbstractDelegate {
    private TipoMotivoOperacaoController tipoMotivoOperacaoController = null;

    public TipoMotivoOperacaoDelegate() {
    }

    private TipoMotivoOperacaoController getTipoMotivoOperacaoController() throws TipoMotivoOperacaoControllerException {
        try {
            if (tipoMotivoOperacaoController == null) {
                tipoMotivoOperacaoController = ApplicationContextProvider.getApplicationContext().getBean(TipoMotivoOperacaoController.class);
            }
            return tipoMotivoOperacaoController;
        } catch (Exception ex) {
            throw new TipoMotivoOperacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private TipoMotivoOperacaoTransferObject findMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        return getTipoMotivoOperacaoController().findMotivoOperacao(motivoOperacao, responsavel);
    }

    public TipoMotivoOperacaoTransferObject findMotivoOperacao(String tmoCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject(tmoCodigo);
        return findMotivoOperacao(motivoOperacao, responsavel);
    }

    public TipoMotivoOperacaoTransferObject findMotivoOperacaoByCodIdent(String tmoIdentificador, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        TipoMotivoOperacaoTransferObject motivoOperacao = new TipoMotivoOperacaoTransferObject();
        motivoOperacao.setTmoIdentificador(tmoIdentificador);
        return findMotivoOperacao(motivoOperacao, responsavel);
    }

    public List<TipoMotivoOperacaoTransferObject> findByTmoExigeObsObrigatorio(AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException {
        return getTipoMotivoOperacaoController().findByTmoExigeObsObrigatorio(responsavel);
    }
}
