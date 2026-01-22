package com.zetra.econsig.job.process.agendado;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaOfertaAutomaticaLeilao;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEncerramentoLeilaoExpirado</p>
 * <p>Description: Processo para execução do processo de encerramento de
 * leilão de solicitação expirado, ou seja, aquele que o servidor
 * não efetou a escolha da proposta.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEncerramentoLeilaoExpirado extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEncerramentoLeilaoExpirado.class);

    private final LeilaoSolicitacaoController leilaoSolicitacaoController;

    public ProcessaEncerramentoLeilaoExpirado(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, getResponsavel())) {
            LOG.info("Inicia processo para encerramento de leilão de solicitação expirado.");
            try {
                final List<TransferObject> solicitacoes = leilaoSolicitacaoController.lstSolicitacaoLeilaoEncerrado(getResponsavel());
                for (TransferObject solicitacao : solicitacoes) {
                    final String adeCodigo = solicitacao.getAttribute(Columns.ADE_CODIGO).toString();
                    encerrarLeilaoExpirado(adeCodigo);
                }
            } catch (LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private void encerrarLeilaoExpirado(String adeCodigo) {
        try {
            // Antes de encerrar o leilão, processa as ofertas automáticas
            processarOfertasAutomaticasLeilao(adeCodigo);
            leilaoSolicitacaoController.encerrarLeilaoExpirado(adeCodigo, getResponsavel());
        } catch (LeilaoSolicitacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            // Caso tenha dado erro na conclusão, executa processo de cancelamento do leição
            cancelarLeilao(adeCodigo, ex);
        }
    }

    private void processarOfertasAutomaticasLeilao(String adeCodigo) {
        try {
            // Dispara a execução das ofertas automáticas e espera a finalização
            final ProcessaOfertaAutomaticaLeilao processo = new ProcessaOfertaAutomaticaLeilao(adeCodigo, DateHelper.getSystemDatetime());
            processo.start();
            processo.join();
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void cancelarLeilao(String adeCodigo, LeilaoSolicitacaoControllerException motivo) {
        try {
            leilaoSolicitacaoController.cancelarProcessoLeilaoPorErro(adeCodigo, getCausaRaiz(motivo), getResponsavel());
        } catch (LeilaoSolicitacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private String getCausaRaiz(Throwable causa) {
        final Throwable causaRaiz = ExceptionUtils.getRootCause(causa);
        final String mensagemCausaRaiz = causaRaiz != null ? causaRaiz.getMessage() : null;
        if (!TextHelper.isNull(mensagemCausaRaiz)) {
            return mensagemCausaRaiz;
        }
        return causa.getMessage();
    }
}
