package com.zetra.econsig.web.listener;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaControleProcessamentoLote;
import com.zetra.econsig.values.AgendamentoEnum;

/**
 * <p>Title: ReiniciarProcessamentoLoteListener</p>
 * <p>Description: Rotina que verifica na inicialização do sistema se há lotes sendo processados
 * e que foram interrompidos por uma reinicialização do sistema e precisam ser reiniciados.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class ReiniciarProcessamentoLoteListener {

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final ProcessoAgendadoPeriodico processo = new ProcessaControleProcessamentoLote(AgendamentoEnum.CONTROLE_PROCESSAMENTO_LOTE.getCodigo(), responsavel);
        processo.start();
        ControladorProcessos.getInstance().incluir(responsavel.getUsuCodigo(), processo);
    }
}
