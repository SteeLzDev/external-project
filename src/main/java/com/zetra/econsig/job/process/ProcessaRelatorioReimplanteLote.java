package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.RelatorioReimplanteLote;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

/**
 * <p>Title: ProcessaRelatorioReimplanteLote</p>
 * <p>Description: Classe que processa relatorio de contratos reimplantados.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ProcessaRelatorioReimplanteLote extends ProcessaRelatorio {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioReimplanteLote.class);


    public ProcessaRelatorioReimplanteLote(Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(new Relatorio("reimplante_lote", ApplicationResourcesHelper.getMessage("rotulo.relatorio.reimplante.contratos", responsavel), CodedValues.FUN_REIMPLANTE_CONSIGNACAO_LOTE, RelatorioReimplanteLote.class.getName(), null, "ReimplanteLote.jasper", "ReimplanteLote.jasper", "", "", "", "", true, false, "N", null), parameterMap, session, agendado, responsavel);

        owner = responsavel.getUsuCodigo();

        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        HashMap<String, Object> parameters = new HashMap<>();
        List<String> adeCodigos = new ArrayList<>();
        String titulo = relatorio.getTitulo();
        String path = ParamSist.getDiretorioRaizArquivos();
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.reimplante.contratos", responsavel), responsavel, parameterMap, null);
        String reportName = null;

        try {

            if (parameterMap.get("adeCodigos") != null) {
                List<String> adeCodigoList = Arrays.asList(parameterMap.get("adeCodigos"));
                if (!adeCodigoList.isEmpty()) {
                    for (String cod : adeCodigoList) {
                        if (!TextHelper.isNull(cod)) {
                            adeCodigos.add(cod);
                        }
                    }
                }
            }

            String[] adeNumNovo = parameterMap.get("adeNumAltera");

            criterio.setAttribute("adeCodigos", adeCodigos);

            String strFormato = "PDF";
            parameterMap.put("formato", new String[]{strFormato});

            String dirFile = path + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "reimplante";

            File dir = new File(dirFile);
            if(!dir.exists() && !dir.exists()){
                dir.mkdirs();
            }

            parameters.put(ReportManager.COLUMN_ADE_NUM_NOVO, adeNumNovo[0]);
            parameters.put(ReportManager.REPORT_DIR_EXPORT, dirFile);
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome, reportName);

            setMensagem(reportNameZip, relatorio.getTipo(), relatorio.getTitulo(), session);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss"));
            LOG.error(mensagem, ex);
        }
    }
}
