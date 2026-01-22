package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaDesfazerCancelamentoSolicitacaoBeneficiario;

/**
 * <p>Title: DesfazerCancelamentoSolicitacaoBeneficiarioJob</p>
 * <p>Description: Desfaz solicitação de cancelamento do beneficio feita pelo beneficiário</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2020-03-03 09:49:00 -0300 (ter, 03 mar 2020) $
 */
public class DesfazerCancelamentoSolicitacaoBeneficiarioJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DesfazerCancelamentoSolicitacaoBeneficiarioJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia reversão do Cancelamento Solicitado pelo beneficiário do contrato de benefício");
        ProcessoAgendado processo = new ProcessaDesfazerCancelamentoSolicitacaoBeneficiario(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
