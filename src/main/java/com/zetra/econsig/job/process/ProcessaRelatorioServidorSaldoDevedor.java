package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

public class ProcessaRelatorioServidorSaldoDevedor extends ProcessaRelatorio {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioServidorSaldoDevedor.class);

    public ProcessaRelatorioServidorSaldoDevedor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        owner = responsavel.getUsuCodigo();

        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        ServidorTransferObject servidor = null;
        final HashMap<String, Object> parameters = new HashMap<>();
        final StringBuilder subTitulo = new StringBuilder("");
        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.saldo.devedor.servidor", responsavel), responsavel, parameterMap, null));
        final String tipoSaldoCombo = getParametro("tipoSaldoCombo", parameterMap);
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = !TextHelper.isNull(strIniPeriodo) ? reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00") : null;
            paramFimPeriodo = !TextHelper.isNull(strFimPeriodo) ? reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59") : null;
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        try {
          final ServidorController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
          servidor = usuarioController.findServidor(responsavel.getSerCodigo(), responsavel);
        } catch (ServidorControllerException e) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, e);
        }

        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append(": ").append(responsavel.getRseMatricula()).append(" - ").append(servidor.getSerCpf()).append(" - ").append(servidor.getSerNome());

        String[] ade = (parameterMap.get("ADE_NUMERO_LIST"));
        String adeNumero    = getParametro("ADE_NUMERO", parameterMap);
        List<Long> adeNumeros = new ArrayList<>();

        if (ade != null) {
            for(String contrato : ade) {
                adeNumeros.add(Long.parseLong(contrato));
            }
        }

        if (!TextHelper.isNull(adeNumero)) {
            adeNumeros.add(Long.parseLong(adeNumero));
        }

        criterio.setAttribute("tipoSolicitacao", tipoSaldoCombo);
        criterio.setAttribute("periodoIni", paramIniPeriodo);
        criterio.setAttribute("periodoFim", paramFimPeriodo);
        criterio.setAttribute("usuCodigo", responsavel.getUsuCodigo());
        criterio.setAttribute("ADE_NUMERO_LIST", adeNumeros);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("informativo", "Informativo");

        try {
            String reportName = null;
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
