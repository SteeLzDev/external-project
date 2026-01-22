package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioDescontoExpirar</p>
 * <p>Description: processo assíncrono para geração de relatório de descontos a expirar</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioDescontoExpirar extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioDescontoExpirar.class);

    private static final String PARAM_NAME_TITULO_TAXA_JUROS = "TITULO_TAXA_JUROS";

    public ProcessaRelatorioDescontoExpirar(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta proprietário do processo.
        owner = responsavel.getUsuCodigo();

        // Seta descrição do processo.
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

        String strPeriodo = getParametro("periodo", parameterMap);
        String paramPeriodo = "";

        if (!TextHelper.isNull(strPeriodo)) {
            paramPeriodo = formatarPeriodo(strPeriodo);
        } else {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
            return;
        }

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo);
        StringBuilder subTitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.descontos.expirar", responsavel), responsavel, parameterMap, null));
        String fileZip = "";

        String[] sad = (parameterMap.get("SAD_CODIGO"));
        List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        String[] origemAde = parameterMap.get("chkOrigem");
        List<String> origensAdes = origemAde != null ? Arrays.asList(origemAde) : null;
        String[] motivoTerminoAde = parameterMap.get("chkTermino");
        List<String> motivoTerminoAdes = motivoTerminoAde != null ? Arrays.asList(motivoTerminoAde) : null;

        if (motivoTerminoAdes != null && origensAdes != null) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente", responsavel);
            return;
        }

        String order = null;

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);

        String estCodigo = (tipoEntidade.equals("EST")) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);

        String path = getPath(responsavel);
        String strFormato = getStrFormato();

        if (path == null) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel);
            return;

        } else {
            String entidade = getEntidade(responsavel);

            path += File.separatorChar + "relatorio" + File.separatorChar + entidade + File.separatorChar + relatorio.getTipo();

            if (!responsavel.isCseSup()) {
                path += File.separatorChar + responsavel.getCodigoEntidade();
            }

            // Cria a pasta de relatório caso não exista.
            new File(path).mkdirs();

            String fileName = path + File.separatorChar + nome.toString();

            try {
                if (responsavel.isCsa() && corCodigo == null) {
                    if (orgCodigo == null) {
                        order = "ORGAO";
                    }
                } else if (responsavel.isCor() || (responsavel.isCsa() && corCodigo != null)) {
                    if (orgCodigo == null) {
                        order = "ORGAO";
                    }
                } else if (responsavel.isCseSupOrg()) {
                    if (csaCodigo == null) {
                        order = "CONSIGNATARIA";
                    }
                }

                List<String> listArquiv = new ArrayList<>();

                String nomeRelConsig = "";
                boolean geraRelatorio = false;
                String nomeRelatorio = nome.toString();
                nomeRelConsig = path + File.separatorChar + nomeRelatorio;

                if (strFormato.equals("TEXT")) {
                    nomeRelConsig += ".txt";
                } else {
                    nomeRelConsig += "." + strFormato.toLowerCase();
                }

                criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                criterio.setAttribute("ADE_ANO_MES_FIM", paramPeriodo);
                criterio.setAttribute("EST_CODIGO", estCodigo);
                criterio.setAttribute("ORG_CODIGO", orgCodigo);
                criterio.setAttribute("CSA_CODIGO", csaCodigo);
                criterio.setAttribute("COR_CODIGO", corCodigo);
                criterio.setAttribute("SVC_CODIGO", svcCodigos);
                criterio.setAttribute("SAD_CODIGO", sadCodigos);
                criterio.setAttribute("ORDER", order);
                criterio.setAttribute("ORIGEM_ADE", origensAdes);
                criterio.setAttribute("TERMINO_ADE", null);
                criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
                geraRelatorio = true;

                try {
                    if (geraRelatorio) {
                        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                        parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
                        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
                        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
                        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
                        parameters.put(PARAM_NAME_TITULO_TAXA_JUROS, (ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel) ? ApplicationResourcesHelper.getMessage("rotulo.cet.abreviado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel)));
                        parameters.put("RESPONSAVEL", responsavel);

                        ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                        reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

                        listArquiv.add(nomeRelConsig);
                    }
                } catch (ReportControllerException ex) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                    LOG.error(mensagem, ex);
                }

                fileZip = fileName + ".zip";
                FileHelper.zip(listArquiv, fileZip);

                for (String arqDelete : listArquiv) {
                    FileHelper.delete(arqDelete);
                }

                // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                enviaEmail(fileZip);

                setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

            } catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }
}
