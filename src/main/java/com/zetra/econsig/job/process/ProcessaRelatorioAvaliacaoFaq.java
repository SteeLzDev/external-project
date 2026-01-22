package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioAvaliacaoFaq</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioAvaliacaoFaq extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioAvaliacaoFaq.class);

    public ProcessaRelatorioAvaliacaoFaq(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.avaliacao.faq", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();

        String strFormato = getStrFormato();
        String[] papel = parameterMap.get("papel");
        boolean cse = false;
        boolean org = false;
        boolean csa = false;
        boolean cor = false;
        boolean ser = false;

        for (String i : papel) {
            cse = cse || i.equals("cse") ? true : false;
            org = org || i.equals("org") ? true : false;
            csa = csa || i.equals("csa") ? true : false;
            cor = cor || i.equals("cor") ? true : false;
            ser = ser || i.equals("ser") ? true : false;
        }

        criterio.setAttribute("cse", cse);
        criterio.setAttribute("org", org);
        criterio.setAttribute("csa", csa);
        criterio.setAttribute("cor", cor);
        criterio.setAttribute("ser", ser);
        criterio.setAttribute("responsavel", responsavel);

        String strIniPeriodo = getParametro("periodoIni", parameterMap);
        String strFimPeriodo = getParametro("periodoFim", parameterMap);

        if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
        }

        criterio.setAttribute("periodoIni", paramIniPeriodo);
        criterio.setAttribute("periodoFim", paramFimPeriodo);

        String itemAvaliacaoFaq = getParametro("faqCodigo", parameterMap);
        String faqCodigo ="";
        String avaliacaoFaq = getParametro("avaliacaoCombo", parameterMap);

        if(!TextHelper.isNull(itemAvaliacaoFaq)) {
            String [] itemFaq = itemAvaliacaoFaq.split(";");
            String faqDescricao = itemFaq[1];
            faqCodigo = itemFaq[0];
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.faq.item", responsavel, faqDescricao.toUpperCase()));
        }

        if(TextHelper.isNull(avaliacaoFaq)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.util.inutil.param", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel)));
        } else {

            String avaliacaoFaqDescricao = avaliacaoFaq.equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.util", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.inutil", responsavel);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.avaliacao.util.inutil.param", responsavel, avaliacaoFaqDescricao));
        }

        criterio.setAttribute("faqCodigo", faqCodigo);
        criterio.setAttribute("avaliacaoFaq", avaliacaoFaq);

        String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
        parameters.put("RESPONSAVEL", responsavel);

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            parameters.put("FAQS", relatorioController.listaAvaliacaoFaqAnalitico(criterio, responsavel));
            parameters.put("FAQSinteticos", relatorioController.listaAvaliacaoFaqSintetico(criterio, responsavel));

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            // Gera Zip
            geraZip(nomeArquivo.toString(), reportName);
        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
