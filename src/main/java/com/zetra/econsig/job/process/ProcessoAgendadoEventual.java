package com.zetra.econsig.job.process;

import java.util.Date;

import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessoAgendadoEventual</p>
 * <p>Description: Processo eventual que pode ser agendada a sua execução. Por ser um processo eventual, após o término da execução o mesmo deve ser concluído.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ProcessoAgendadoEventual extends ProcessoAgendado {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessoAgendadoEventual.class);

    public ProcessoAgendadoEventual(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executar() {
        Date dataInicio = new Date();
        try {
            LOG.debug("Executa Processo Agendado Eventual: " + dataInicio);

            // Executa o agendamento
            executa();
            Date dataFim = new Date();

            if (getCodigoRetorno() == ERRO) {
                // Caso seja resultado de erro, grava ocorrência com o motivo
                insereOcorrenciaErroProcessamento(dataInicio, new Date(), getMensagem());
            }

            // Conclui o agendamento, mesmo em caso de erro evitando que seja novamente reexecutado
            AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
            agendamentoController.concluiAgendamento(getAgdCodigo(), dataInicio, dataFim, getResponsavel());

        } catch (AgendamentoControllerException ex) {
            LOG.error("Não foi possível concluir o agendamento \"" + getAgdCodigo() + "\": " + ex.getMessage(), ex);
            insereOcorrenciaErroProcessamento(dataInicio, new Date(), ex.getMessage());
        } catch (ZetraException ex) {
            LOG.error("Não foi possível executar o agendamento \"" + getAgdCodigo() + "\": " + ex.getMessage(), ex);
            insereOcorrenciaErroProcessamento(dataInicio, new Date(), ex.getMessage());
        } catch (Exception ex) {
            LOG.error("Não foi possível executar o agendamento \"" + getAgdCodigo() + "\": " + ex.getMessage(), ex);
            insereOcorrenciaErroProcessamento(dataInicio, new Date(), ex.getMessage());
        }
    }

    private void insereOcorrenciaErroProcessamento(Date dataInicio, Date dataFim, String observacao) {
        try {
            AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
            agendamentoController.insereOcorrencia(getAgdCodigo(), CodedValues.TOC_ERRO_PROCESSAMENTO_AGENDAMENTO, dataInicio,
                    dataFim, ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.agendamento", getResponsavel()) + ": " + observacao, getResponsavel());
        } catch (AgendamentoControllerException e) {
            LOG.error("Não foi possível inserir ocorrencia de erro de processamento. " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected abstract void executa() throws ZetraException;
}
