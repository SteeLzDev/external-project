package com.zetra.econsig.job;

import java.lang.reflect.InvocationTargetException;

import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: Agendador</p>
 * <p>Description: Agendador de trabalhos para serem executados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Agendador {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Agendador.class);

    /**
     * Agenda um trabalho para ser executado.
     *
     * @param agdCodigo Código do agendamento que será executado
     * @param usuCodigo Código do usuário responsável pelo agendamento
     * @param nomeJob Nome do trabalho
     * @param nomeTrigger Nome da trigger que o trabalho estará vinculado
     * @param clazz Classe que será executada quando o trabalho for executado
     * @param tipo Tipo do agendamento
     * @param dataInicio Data de início do trabalho
     * @param dataFim Data fim do trabalho
     * @param segundos Segundos para reexecução do trabalho
     * @throws SchedulerException
     */
    public static void agendaTrabalho(String agdCodigo, String usuCodigo, Class<? extends AbstractJob> clazz, TipoAgendamentoEnum tipo) {
        LOG.info("Agendando Tarefa: " + agdCodigo + " Classe: " + clazz + " Usuario: " + usuCodigo);
        ProcessoAgendador agendador = new ProcessoAgendador(agdCodigo, usuCodigo, clazz, tipo);
        agendador.start();
    }

    private static class ProcessoAgendador extends Processo {
        private AbstractJob job;

        public ProcessoAgendador(String agdCodigo, String usuCodigo, Class<? extends AbstractJob> clazz, TipoAgendamentoEnum tipo) {
            try {
                job = clazz.getDeclaredConstructor().newInstance();
                job.setAgdCodigo(agdCodigo);
                job.setUsuCodigo(usuCodigo);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                LOG.error("Não foi possível instanciar classe do agendamento '" + agdCodigo + "'.");
                LOG.error(ex.getMessage(), ex);
            }
        }

        @Override
        protected void executar() {
            if (job != null) {
                try {
                    // DESENV-16284 : Espera a finalização do método que agendou o trabalho para evitar que consultas
                    //  foram do escopo transacional não retornem os dados necessário.
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Executa o job agendado
                job.executar();
            }
        }
    }
}