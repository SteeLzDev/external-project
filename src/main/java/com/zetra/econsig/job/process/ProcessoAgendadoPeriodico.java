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
 * <p>Title: ProcessoAgendadoPeriodico</p>
 * <p>Description: Processo eventual que pode ser agendada a sua execução. Por ser um processo periodico, o agendamento não é concluído, é inserida uma ocorrencia de execução ou erro do agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ProcessoAgendadoPeriodico extends ProcessoAgendado {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessoAgendadoPeriodico.class);

    public ProcessoAgendadoPeriodico(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected final void executar() {
        Date dataInicio = new Date();
        try {
            LOG.debug("Executa Processo Agendado Periodico: " + dataInicio);

            executa();
            Date dataFim = new Date();

            AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
            agendamentoController.insereOcorrencia(getAgdCodigo(), CodedValues.TOC_PROCESSAMENTO_AGENDAMENTO, dataInicio,
                    dataFim, ApplicationResourcesHelper.getMessage("mensagem.informacao.processamento.agendamento", getResponsavel()), getResponsavel());

        } catch (AgendamentoControllerException e) {
            LOG.error("Não foi possível inserir ocorrencia de processamento \"" + getAgdCodigo() + "\": " + e.getMessage(), e);
            insereOcorrenciaErroProcessamento(dataInicio, new Date());
        } catch (ZetraException e) {
            LOG.error("Não foi possível processar o agendamento \"" + getAgdCodigo() + "\": " + e.getMessage(), e);
            insereOcorrenciaErroProcessamento(dataInicio, new Date());
        } catch (Exception e) {
            LOG.error("Não foi possível processar o agendamento \"" + getAgdCodigo() + "\": " + e.getMessage(), e);
            insereOcorrenciaErroProcessamento(dataInicio, new Date());
        }
    }

    private void insereOcorrenciaErroProcessamento(Date dataInicio, Date dataFim) {
        try {
            AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
            agendamentoController.insereOcorrencia(getAgdCodigo(), CodedValues.TOC_ERRO_PROCESSAMENTO_AGENDAMENTO, dataInicio,
                    dataFim, ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.agendamento", getResponsavel()), getResponsavel());
        } catch (AgendamentoControllerException e) {
            LOG.error("Não foi possível inserir ocorrencia de erro de processamento. " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected abstract void executa() throws ZetraException;
}
