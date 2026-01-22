package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaEmailCsaControleCoeficienteAtivo;

/**
 * <p>Title: EnviaEmailCsaControleCoeficienteAtivoJob</p>
 * <p>Description: Job a ser executado diariamente para enviar e-mail as consignatárias que tem o parâmetro de serviço TPS_DIAS_VIGENCIA_CET
 * com valor definido para atualizarem suas taxas próximas da expiração, expiradas e desbloqueadas.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviaEmailCsaControleCoeficienteAtivoJob extends AbstractJob {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaEmailCsaControleCoeficienteAtivoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de e-mail de alerta às consignatárias sobre desbloqueio, atualização e expiração de taxas de juros.");
        ProcessoAgendado processo = new ProcessaEmailCsaControleCoeficienteAtivo(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
