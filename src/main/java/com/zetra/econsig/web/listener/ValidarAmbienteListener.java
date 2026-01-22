package com.zetra.econsig.web.listener;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaValidacaoAmbiente;
import com.zetra.econsig.values.AgendamentoEnum;


/**
 * <p>Title: ValidarAmbienteListener</p>
 * <p>Description: Listener que realiza validações de ambiente no sistema</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class ValidarAmbienteListener {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarAmbienteListener.class);

        @EventListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            LOG.info("Validação do Ambiente do eConsig");
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            final ProcessoAgendadoPeriodico processo = new ProcessaValidacaoAmbiente(AgendamentoEnum.VALIDACAO_AMBIENTE_ECONSIG.getCodigo(), responsavel);
            processo.start();
            ControladorProcessos.getInstance().incluir(responsavel.getUsuCodigo(), processo);
        }
}
