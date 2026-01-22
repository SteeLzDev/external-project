package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p>Title: ProcessaDesfazerCancelamentoSolicitacaoBeneficiario</p>
 * <p>Description: Processamento de reversão do cancelamento do benefício solicitado pelo beneficiário</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2020-03-03 09:49:00 -0300 (ter, 03 mar 2020) $
 */
public class ProcessaDesfazerCancelamentoSolicitacaoBeneficiario extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDesfazerCancelamentoSolicitacaoBeneficiario.class);

    public ProcessaDesfazerCancelamentoSolicitacaoBeneficiario(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        ContratoBeneficioController contratoBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(ContratoBeneficioController.class);
        LOG.debug("Executa reversão do cancelamento do benefício solicitado pelo beneficiário");
        contratoBeneficioController.desfazerCancelamentoAutomatico(getResponsavel());
    }
}
