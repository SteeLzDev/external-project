package com.zetra.econsig.job.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioDescontosMovFin</p>
 * <p>Description: Classe para processamento de relatorio de descontos (movimento financeiro)
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public final class ProcessaRelatorioDescontosMovFin extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioDescontosMovFin.class);

    public ProcessaRelatorioDescontosMovFin(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String reportName = null;

        String strIniPeriodo = "";
        String paramIniPeriodo = "";

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) &&
                parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }

        if (parameterMap.containsKey("periodo")) {
            strIniPeriodo = getParametro("periodo", parameterMap);
            paramIniPeriodo = formatarPeriodo(strIniPeriodo);
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strIniPeriodo);

        StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.descontos.mov.fin", responsavel), responsavel, parameterMap, null)));

        String[] sad = (parameterMap.get("SAD_CODIGO"));
        List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        String[] spd = (parameterMap.get("SPD_CODIGO"));
        List<String> spdCodigos = spd != null ? Arrays.asList(spd) : null;

        StringBuilder subTitulo = new StringBuilder();

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);

        String estCodigo = (tipoEntidade.equals("EST")) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);

        String echCodigo = getFiltroEnderecoCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String plaCodigo = getFiltroPlanoCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String cnvCodVerba = getFiltroCnvCodVerba(parameterMap, subTitulo, nome, session, responsavel);

        String nomeEntidade = "";

        if (!responsavel.isCseSup()) {
            nomeEntidade = (tipoEntidade == null || !tipoEntidade.equals("EST")) ? responsavel.getNomeEntidade() : responsavel.getNomeEntidadePai();
        }

        String order = (responsavel.isCseSupOrg() && csaCodigo == null) ? "CONSIGNATARIA" : "ORGAO";
        String tituloGrupo = (responsavel.isCseSupOrg() && csaCodigo == null) ? ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase() : ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toUpperCase();
        String formatoArquivo = getStrFormato();

        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
        criterio.setAttribute("ORG_CODIGO", orgCodigo);
        criterio.setAttribute("EST_CODIGO", estCodigo);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("COR_CODIGO", corCodigo);
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("PERIODO", paramIniPeriodo);
        criterio.setAttribute("SAD_CODIGO", sadCodigos);
        criterio.setAttribute("SPD_CODIGO", spdCodigos);
        criterio.setAttribute("ORDER", order);
        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
        criterio.setAttribute("PLA_CODIGO", plaCodigo);
        criterio.setAttribute("ECH_CODIGO", echCodigo);
        criterio.setAttribute("CNV_COD_VERBA", cnvCodVerba);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("NOME_ENTIDADE", nomeEntidade);
        parameters.put("TIPO_ENTIDADE", tipoEntidade);
        parameters.put("ORDER", order);
        parameters.put("TITULO_GRUPO", tituloGrupo);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, formatoArquivo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());

        try{
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(formatoArquivo, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

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
