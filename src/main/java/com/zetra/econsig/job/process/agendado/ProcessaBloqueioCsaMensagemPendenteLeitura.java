package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaBloqueioCsaMensagemPendenteLeitura</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias com mensagem pendente de leitura</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioCsaMensagemPendenteLeitura extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaMensagemPendenteLeitura.class);

    public ProcessaBloqueioCsaMensagemPendenteLeitura(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();

        Object objQtdeDiasBloqCsaNaoConfLeituraMsg = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_CSA_MENSAGEM_NAO_LIDA, responsavel);
        Integer qtdeDiasBloqCsaNaoConfLeituraMsg = !TextHelper.isNull(objQtdeDiasBloqCsaNaoConfLeituraMsg) ? Integer.parseInt(objQtdeDiasBloqCsaNaoConfLeituraMsg.toString()) : 0;

        if (qtdeDiasBloqCsaNaoConfLeituraMsg > 0) {
            // Executa cancelamento automático de consignações, se ainda não foi feito no dia
            LOG.debug("Executa Bloqueio de Consignatárias com mensagem pendente de confirmação de leitura");
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            csaDelegate.bloqueiaCsaMensagemNaoLida();
        }
    }
}
