package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaNotificaoEnvioCsaNovosVinculos;

/**
 * <p>Title: EnviaNotificaoCsaNovosVinculosJob</p>
 * <p>Description: Envia notifição por email para as consignatárias que possuem convenio de vinculo que um novo foi criado.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviaNotificaoCsaNovosVinculosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaNotificaoCsaNovosVinculosJob.class);

    @Override
    public void executar() {
        LOG.info("Envia notificação para as consignatárias caso exista um novo vinículo criado Job");
        ProcessoAgendado processo = new ProcessaNotificaoEnvioCsaNovosVinculos(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
