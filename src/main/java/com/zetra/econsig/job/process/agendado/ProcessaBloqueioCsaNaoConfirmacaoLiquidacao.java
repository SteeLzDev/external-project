package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBloqueioCsaNaoConfirmacaoLiquidacao.java</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias que não confirmaram liquidação dentro do prazo.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class ProcessaBloqueioCsaNaoConfirmacaoLiquidacao extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaNaoConfirmacaoLiquidacao.class);

    public ProcessaBloqueioCsaNaoConfirmacaoLiquidacao(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();

        Object objQtdeDiasBloqCsaNaoConfLeituraMsg = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQUEIO_CSA_NAO_ATENDEU_SOLICITACAO_LIQUIDACAO, responsavel);
        Integer qtdeDiasBloqCsaNaoConfLeituraMsg = !TextHelper.isNull(objQtdeDiasBloqCsaNaoConfLeituraMsg) ? Integer.parseInt(objQtdeDiasBloqCsaNaoConfLeituraMsg.toString()) : 0;

        if (qtdeDiasBloqCsaNaoConfLeituraMsg > 0) {
            // Executa bloqueio de consignatárias que não confirmaram liquidação dentro do prazo.
            LOG.debug("Executa Bloqueio de Consignatárias que não confirmaram liquidação dentro do prazo");
            ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.bloqueiaCsaNaoConfirmacaoLiquidacao();
        }
    }
}
