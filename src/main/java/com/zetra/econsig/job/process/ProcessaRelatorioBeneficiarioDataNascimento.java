package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioBeneficiarioDataNascimento</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioBeneficiarioDataNascimento extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioBeneficiarioDataNascimento.class);

    public ProcessaRelatorioBeneficiarioDataNascimento(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.beneficiario.data.nascimento", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();

        String strFormato = getStrFormato();

        criterio.setAttribute("responsavel", responsavel);

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.nascimento.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        criterio.setAttribute("periodoIni", paramIniPeriodo);
        criterio.setAttribute("periodoFim", paramFimPeriodo);
        criterio.setAttribute("rseMatricula", getFiltroRseMatricula(parameterMap, subTitulo, nomeArquivo, session, responsavel));
        criterio.setAttribute("serCpf", getFiltroCpf(parameterMap, subTitulo, nomeArquivo, session, responsavel));

        String nome = getParametro("nome", parameterMap);
        criterio.setAttribute("serNome", nome);
        if(!TextHelper.isNull(nome)){
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.nome.singular.arg0", responsavel, nome));
        }

        criterio.setAttribute("nseCodigo", getFiltroNseCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel));

        // Código do benefício
        String benCodigo = "";
        if (parameterMap.containsKey("BEN_CODIGO")) {
            String aux = getParametro("BEN_CODIGO", parameterMap);
            if (!TextHelper.isNull(aux)) {
                String helper[] = aux.split(";");
                benCodigo = helper[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, helper[1].toUpperCase()));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        criterio.setAttribute("benCodigo", benCodigo);

        // Pegar dos campos os valores setados pelo usuario
        // Código da operadora
        String csaCodigo = "";
        if (parameterMap.containsKey("csaCodigoOperadora")) {
            String aux = getParametro("csaCodigoOperadora", parameterMap);
            if (!TextHelper.isNull(aux)) {
                String helper[] = aux.split(";");
                csaCodigo = helper[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.operadora.arg0", responsavel, helper[2].toUpperCase()));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.operadora.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
            }
        }

        criterio.setAttribute("csaCodigo", csaCodigo);

        List<String> statusCodigo = new ArrayList<>();
        List<String> statusDescricao = new ArrayList<>();

        if  (parameterMap.containsKey("scbCodigo")) {
            String scbs[] = (parameterMap.get("scbCodigo"));
            if (!scbs[0].equals("")) {
                List<String> aux = Arrays.asList(scbs);

                aux.forEach(item-> {
                    statusCodigo.add(item.split(";")[0]);
                    statusDescricao.add(item.split(";")[1]);
                });

                String statusDescricaoStr = statusDescricao.toString().substring(1);
                statusDescricaoStr = statusDescricaoStr.substring(0, statusDescricaoStr.length() - 1);

                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.contrato.beneficio.situacao.contrato.filtro", responsavel, statusDescricaoStr.toUpperCase()));
            }
        }

        criterio.setAttribute("scbCodigo", statusCodigo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        try {
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
