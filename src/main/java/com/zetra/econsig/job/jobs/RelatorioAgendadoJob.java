package com.zetra.econsig.job.jobs;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RelatorioAgendadoJob</p>
 * <p>Description: Trabalho genérico para execução de um relatório agendado.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAgendadoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioAgendadoJob.class);

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioAgendado(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioAgendadoJob).
     */
    class ProcessoRelatorioAgendado extends ProcessoAgendadoEventual {

        // Processo interno de execução
        private Processo processo;

        public ProcessoRelatorioAgendado(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            String nomeClasseProcesso = "";
            try {
                AcessoSistema responsavel = getResponsavel();
                AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
                String agdCodigo = getAgdCodigo();
                Map<String, String[]> parameterMap = getParametrosAgendamento(agdCodigo);

                TransferObject agendamento = agendamentoController.findAgendamento(agdCodigo, responsavel);
                String relCodigo = agendamento.getAttribute(Columns.AGD_REL_CODIGO).toString();
                Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(relCodigo);

                Class<?>[] paramTypes = { Relatorio.class, Map.class, HttpSession.class, Boolean.class, AcessoSistema.class };
                Object[] paramValues = {  relatorio, parameterMap, null, true, responsavel };
                nomeClasseProcesso = relatorio.getClasseProcesso();
                Class<Processo> classeProcesso = (Class<Processo>) Class.forName(nomeClasseProcesso);
                processo = classeProcesso.getConstructor(paramTypes).newInstance(paramValues);
                //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
                //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
                //O Processo Pai que é gerenciado pelo ControladorProcessos.
                processo.run();

            } catch (ClassNotFoundException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (IllegalArgumentException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (SecurityException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (InstantiationException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (IllegalAccessException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (InvocationTargetException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            } catch (NoSuchMethodException e) {
                LOG.error("Não foi possível executar o relatório agendado: " + nomeClasseProcesso + "\nClasse não pode ser agendada.");
                throw new ZetraException("mensagem.erro.relatorio.agendado.classe.nao.pode.ser.agendada", getResponsavel(), nomeClasseProcesso);
            }
        }

        @Override
        public int getCodigoRetorno() {
            if (processo != null) {
                return processo.getCodigoRetorno();
            }
            return super.getCodigoRetorno();
        }

        @Override
        public String getMensagem() {
            if (processo != null) {
                return processo.getMensagem();
            }
            return super.getMensagem();
        }
    }
}
