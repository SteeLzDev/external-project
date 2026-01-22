package com.zetra.econsig.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: ControladorAgendamentosInstantaneosJob</p>
 * <p>Description: Controla a fila de agendamentos instantâneos que devem ser executados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class ControladorAgendamentosInstantaneosJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControladorAgendamentosInstantaneosJob.class);

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private SistemaController sistemaController;

    @Scheduled(cron = "0 */5 * * * *")
    public void executar() {
        LOG.info("Controlador de Agendamentos Instantâneos.");

        // Se o sistema está bloquead ou inativo, nenhum processo pode ser agendado
        if (sistemaController.isSistemaBloqueado(AcessoSistema.getAcessoUsuarioSistema())) {
            LOG.info("Sistema bloqueado ou inativo, processos não podem ser agendados.");
            return;
        }

        // Reinicializa a fila de agendamentos
        List<TransferObject> agendamentos = recuperaAgendamentos();

        // Se não existe nenhum agendamento na fila, termina execução do processo
        if (agendamentos.isEmpty()) {
            return;
        }

        for (TransferObject agendamento : agendamentos) {
            String agdCodigo = agendamento.getAttribute(Columns.AGD_CODIGO).toString();
            String agdJavaClassName = agendamento.getAttribute(Columns.AGD_JAVA_CLASS_NAME).toString();
            String usuCodigo = agendamento.getAttribute(Columns.USU_CODIGO).toString();

            try {
                // Se existe processo está ativo, não pode ser agendado o mesmo processo em paralelo
                if (ControladorProcessos.getInstance().processoAtivo(agdCodigo)) {
                    LOG.info("Processo ativo, não pode ser agendado o mesmo processo em paralelo.");
                    return;
                }

                Class<AbstractJob> clazz = (Class<AbstractJob>) Class.forName(agdJavaClassName);
                String tipo = agendamento.getAttribute(Columns.TAG_CODIGO).toString();
                TipoAgendamentoEnum tipoAgendamento = TipoAgendamentoEnum.recuperaTipoAgendamento(tipo);

                LOG.info("Agenda trabalho para execução");
                Agendador.agendaTrabalho(agdCodigo, usuCodigo, clazz, tipoAgendamento);
            } catch (ClassCastException e) {
                LOG.error("Não foi possível agendar a tarefa: " + agdCodigo + " : " + agdJavaClassName + " - Classe não pode ser agendada.");
            } catch (ClassNotFoundException e) {
                LOG.error("Não foi possível agendar a tarefa: " + agdCodigo + " : " + agdJavaClassName + " - Classe não encontrada.");
            }
        }
    }

    /**
     * Recupera os possíveis agendamentos instantâneos que estão aguardando execução.
     * @return
     */
    private List<TransferObject> recuperaAgendamentos() {
        List<TransferObject> agendamentos = null;
        try {
            agendamentos = agendamentoController.lstAgendamentosInstantaneosParaExecucao(AcessoSistema.getAcessoUsuarioSistema());

            LOG.info("Quantidade de agendamentos encontrados: " + agendamentos.size());

        } catch (AgendamentoControllerException ex) {
            agendamentos = new ArrayList<>();
            LOG.error("Não foi possível recuperar os possíveis agendamentos.");
            LOG.error(ex.getMessage(), ex);
        }

        return agendamentos;
    }
}
