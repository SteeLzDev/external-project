package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaVerificacaoDocumentacaoAssinada</p>
 * <p>Description: Verifica se anexos de solicitações já foram assinados digitalmente e prossegue com a aprovação
 *                 destas.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaVerificacaoDocumentacaoAssinada extends ProcessoAgendadoPeriodico {

    private final AcessoSistema responsavel;

    public ProcessaVerificacaoDocumentacaoAssinada(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        this.responsavel = responsavel;
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, getResponsavel())) {
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
            SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
            simulacaoController.verificarAssinaturaAnexosSolicitacao(null, responsavel);
        }
    }

}
