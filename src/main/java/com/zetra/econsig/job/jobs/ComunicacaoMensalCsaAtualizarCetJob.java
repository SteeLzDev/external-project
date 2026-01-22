package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaEmailComunicadoAtualizarCet;

/**
 * <p>Title: ComunicacaoMensalCsaAtualizarCetJob</p>
 * <p>Description: job a ser executado mensalmente para enviar e-mail de comunicado às consignatárias que tem
 *                 o parâmetro de serviço TPS_DIAS_VIGENCIA_CET com valor definido para atualizarem suas taxas no
 *                 sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoMensalCsaAtualizarCetJob extends AbstractJob {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ComunicacaoMensalCsaAtualizarCetJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de e-mail de alerta às consignatárias sobre validade de taxas de juros.");
        ProcessoAgendado processo = new ProcessaEmailComunicadoAtualizarCet(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
