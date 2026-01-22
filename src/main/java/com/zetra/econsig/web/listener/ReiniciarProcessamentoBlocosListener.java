package com.zetra.econsig.web.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaBlocosProcessamentoFolha;
import com.zetra.econsig.persistence.entity.HistoricoProcessamento;
import com.zetra.econsig.service.folha.ProcessarFolhaController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ReiniciarProcessamentoBlocosListener</p>
 * <p>Description: Rotina que verifica na inicialização do sistema se há blocos ainda aguardando processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class ReiniciarProcessamentoBlocosListener {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReiniciarProcessamentoBlocosListener.class);

    @Autowired
    private ProcessarFolhaController processarFolhaController;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_PROCESSAMENTO_SISTEMA_DESBLOQUEADO, CodedValues.TPC_SIM, responsavel)) {
                List<HistoricoProcessamento> processamentosNaoFinalizados = processarFolhaController.obterProcessamentosNaoFinalizados(responsavel);
                if (processamentosNaoFinalizados != null && !processamentosNaoFinalizados.isEmpty()) {
                    for (HistoricoProcessamento processamento : processamentosNaoFinalizados) {
                        // Inicia o processo de execução dos blocos que estão aguardando processamento
                        ProcessaBlocosProcessamentoFolha processo = new ProcessaBlocosProcessamentoFolha(processamento, responsavel);
                        processo.start();
                        ControladorProcessos.getInstance().incluir(ProcessaBlocosProcessamentoFolha.CHAVE, processo);
                    }
                }
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
