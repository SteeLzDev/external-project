package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

public class ProcessaRelatorioSolicitacaoSaldoDevedor extends ProcessaRelatorio{

	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSolicitacaoSaldoDevedor.class);

    public ProcessaRelatorioSolicitacaoSaldoDevedor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

		String strIniPeriodo = getParametro("periodoIni", parameterMap);
        String strFimPeriodo = getParametro("periodoFim", parameterMap);
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
        }

        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.solicitacao.saldo.devedor", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();
        String tipoSaldoCombo = getParametro("tipoSaldoCombo", parameterMap);
        String adeNumero = getParametro("ADE_NUMERO", parameterMap);

        if (!TextHelper.isNull(adeNumero)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.solicitacao.saldo.devedor.ade.arg0", responsavel, adeNumero));
        }

        String rseMatricula = getFiltroRseMatricula(parameterMap, subTitulo, nomeArquivo, session, responsavel);
        String serCpf = getFiltroCpf(parameterMap, subTitulo, nomeArquivo, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);
        String estCodigo = getFiltroEstCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);
        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);


        if (TextHelper.isNull(tipoSaldoCombo)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.solicitacao.saldo.devedor.tipo", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
        } else {
            String descTipoSaldo = tipoSaldoCombo.equals(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo()) ? ApplicationResourcesHelper.getMessage("rotulo.sistema.informacao.saldo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.sistema.liquidacao.saldo", responsavel);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.solicitacao.saldo.devedor.tipo", responsavel, descTipoSaldo.toUpperCase()));
        }

        String reportName = null;
        try {
            criterio.setAttribute("periodoIni", paramIniPeriodo);
            criterio.setAttribute("periodoFim", paramFimPeriodo);
            criterio.setAttribute("adeNumero", adeNumero);
            criterio.setAttribute("rseMatricula", rseMatricula);
            criterio.setAttribute("serCpf", serCpf);
            criterio.setAttribute("orgCodigo", orgCodigo);
            criterio.setAttribute("estCodigo", estCodigo);
            criterio.setAttribute("csaCodigo", csaCodigo);
            criterio.setAttribute("tipoSolicitacao", tipoSaldoCombo);

            String strFormato = getStrFormato();
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nomeArquivo.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}