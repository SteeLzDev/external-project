package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaAtualizaMargemRenegociacaoPrazoExpirado;

/**
 * <p>Title: AtualizaMargemRenegociacaoPrazoExpiradoJob</p>
 * <p>Description: Atualiza as margens presas fruto de contratos de renegocição</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizaMargemRenegociacaoPrazoExpiradoJob extends AbstractJob {

    @Override
    public void executar() {
        final ProcessaAtualizaMargemRenegociacaoPrazoExpirado processo = new ProcessaAtualizaMargemRenegociacaoPrazoExpirado(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
