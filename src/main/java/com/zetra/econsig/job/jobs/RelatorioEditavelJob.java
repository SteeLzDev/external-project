package com.zetra.econsig.job.jobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioEditavel;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RelatorioEditavelJob</p>
 * <p>Description: Trabalho padrão para execução do Relatório Editável.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioEditavelJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioEditavel(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioGerencialGeralJob).
     */
    class ProcessoRelatorioEditavel extends ProcessoAgendadoEventual {

        public ProcessoRelatorioEditavel(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Map<String, String[]> mapeamento = getParametrosRelatorio();
            String relCodigo = mapeamento.get(Columns.getColumnName(Columns.REL_CODIGO))[0].toString();

            if (TextHelper.isNull(relCodigo)) {
                throw new ZetraException("mensagem.erro.interno.relatorio.codigo.ausente", getResponsavel());
            }

            Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio(relCodigo);
            Processo processo = new ProcessaRelatorioEditavel(relatorio, mapeamento, null, true, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

        private Map<String, String[]> getParametrosRelatorio() throws AgendamentoControllerException {
            AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
            Map<String, List<String>> retorno = agendamentoController.lstParametrosAgendamento(getAgdCodigo(), getResponsavel());

            Map<String, String[]> mapeamento = new HashMap<>();
            Iterator<Map.Entry<String, List<String>>> iteKey = retorno.entrySet().iterator();
            while (iteKey.hasNext()) {
                Map.Entry<String, List<String>> entry = iteKey.next();
                String chave = entry.getKey();
                List<String> valores = entry.getValue();
                String[] strValores = valores.toArray(new String[0]);
                mapeamento.put(chave, strValores);
            }

            return mapeamento;
        }
    }

}
