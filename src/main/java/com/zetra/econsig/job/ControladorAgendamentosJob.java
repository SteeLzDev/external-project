package com.zetra.econsig.job;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: ControladorAgendamentosJob</p>
 * <p>Description: Controla a fila de agendamentos que devem ser executados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class ControladorAgendamentosJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ControladorAgendamentosJob.class);

    private static final int HORA_LIMITE = 4;
    private static boolean suspenso = false;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private SistemaController sistemaController;

    @Scheduled(cron = "0 0/1 1-23 * * ?")
    public void executar() {
        LOG.info("Controlador de Agendamentos.");
        List<TransferObject> agendamentos = null;

        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);

        // Se o controlador de agendamentos estiver suspenso, verifica se é a hora de reiniciá-lo
        if (suspenso) {
            if (hora < HORA_LIMITE) {
                LOG.info("Agendamento de processos reiniciado.");
                // Reinicializa a fila de agendamentos
                agendamentos = recuperaAgendamentos();
                suspenso = false;
            } else  {
                return;
            }
        }

        // Se o sistema está bloquead ou inativo, nenhum processo pode ser agendado
        if (sistemaController.isSistemaBloqueado(AcessoSistema.getAcessoUsuarioSistema())) {
            LOG.info("Sistema bloqueado ou inativo, processos não podem ser agendados.");
            return;
        }

        // Se existe um processo ativo, não podem ser agendados outros processos em paralelo
        if (ControladorProcessos.getInstance().existeProcessoAtivo()) {
            LOG.info("Existe um processo ativo, não podem ser agendados processos em paralelo.");
            return;
        }

        // Se não existe nenhum agendamento na fila, tenta recuperar mais agendamentos para serem executados
        // Se estiver acima da hora limite, o agendamento de processos é suspenso
        if (agendamentos == null || agendamentos.isEmpty()) {
            agendamentos = recuperaAgendamentos();
            if (agendamentos.isEmpty()) {
                LOG.info("Não existe processo para ser agendado.");
                if (hora >= HORA_LIMITE) {
                    LOG.info("Agendamento de processos suspenso.");
                    suspenso = true;
                }
                return;
            }
        }

        // Recupera o primeiro agendamento da fila
        TransferObject agendamento = agendamentos.remove(0);
        String agdCodigo = agendamento.getAttribute(Columns.AGD_CODIGO).toString();
        String agdJavaClassName = agendamento.getAttribute(Columns.AGD_JAVA_CLASS_NAME).toString();
        String usuCodigo = agendamento.getAttribute(Columns.USU_CODIGO).toString();
        String status = agendamento.getAttribute(Columns.SAG_CODIGO).toString();
        StatusAgendamentoEnum statusAgendamento = StatusAgendamentoEnum.recuperaStatusAgendamento(status);

        // Se está fora da hora limite para execução de agendamentos e todos agendamentos diários, semanais e/ou mensais já foram executados,
        // suspende a execução de agendamentos
        if (hora >= HORA_LIMITE && !statusDeveExecutar(statusAgendamento)) {
            LOG.info("Agendamento de processos suspenso.");
            suspenso = true;
            return;
        }

        try {
            Class<AbstractJob> clazz = (Class<AbstractJob>) Class.forName(agdJavaClassName);
            String tipo = agendamento.getAttribute(Columns.TAG_CODIGO).toString();
            TipoAgendamentoEnum tipoAgendamento = TipoAgendamentoEnum.recuperaTipoAgendamento(tipo);

            LOG.info("Agenda próximo trabalho para execução");
            Agendador.agendaTrabalho(agdCodigo, usuCodigo, clazz, tipoAgendamento);
        } catch (ClassCastException e) {
            LOG.error("Não foi possível agendar a tarefa: " + agdCodigo + " : " + agdJavaClassName + "\nClasse não pode ser agendada.");
        } catch (ClassNotFoundException e) {
            LOG.error("Não foi possível agendar a tarefa: " + agdCodigo + " : " + agdJavaClassName + "\nClasse não encontrada.");
        }
    }

    /**
     * Recupera os possíveis agendamentos que estão aguardando execução.
     *
     */
    private List<TransferObject> recuperaAgendamentos() {
        List<TransferObject> agendamentos = null;
        try {
            List<TransferObject> agdDesordenados = agendamentoController.lstAgendamentosParaExecucao(AcessoSistema.getAcessoUsuarioSistema());

            if (agdDesordenados != null) {
                agendamentos = new ArrayList<>();
                List<TransferObject> agdSemPrioridade = new ArrayList<>();

                for (TransferObject to : agdDesordenados) {
                    StatusAgendamentoEnum sagCodigo = StatusAgendamentoEnum.recuperaStatusAgendamento(to.getAttribute(Columns.SAG_CODIGO).toString());

                    if (statusDeveExecutar(sagCodigo)) {
                        agendamentos.add(to);
                    } else {
                        agdSemPrioridade.add(to);
                    }

                }

                // Adiciona os agendamentos que podem ser executados em outro dia caso não haja tempo
                agendamentos.addAll(agdSemPrioridade);
            }

            LOG.info("Quantidade de agendamentos encontrados: " + agendamentos.size());

        } catch (AgendamentoControllerException ex) {
            agendamentos = new ArrayList<>();
            LOG.error("Não foi possível recuperar os possíveis agendamentos.");
            LOG.error(ex.getMessage(), ex);
        }

        return agendamentos;
    }

    /**
     * Verifica os status dos agendamentos que devem ser executados independente do horário
     * @param status
     * @return
     */
    private boolean statusDeveExecutar(StatusAgendamentoEnum status) {
        return status.equals(StatusAgendamentoEnum.EXECUCAO_DIARIA)
            || status.equals(StatusAgendamentoEnum.EXECUCAO_SEMANAL)
            || status.equals(StatusAgendamentoEnum.EXECUCAO_MENSAL)
            || status.equals(StatusAgendamentoEnum.EXECUCAO_ANUAL)
            ;
    }
}
