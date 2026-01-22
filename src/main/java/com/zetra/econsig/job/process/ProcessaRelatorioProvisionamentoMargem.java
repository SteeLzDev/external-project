package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
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
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioProvisionamentoMargem</p>
 * <p>Description: Classe para processamento do relatório de provisionamento de margem</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 */
public class ProcessaRelatorioProvisionamentoMargem extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadCsa.class);

    public ProcessaRelatorioProvisionamentoMargem(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String reportName = null;

        final StringBuilder filtrosAplicados = new StringBuilder();

        final String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.provisionamento.margem", responsavel), responsavel, parameterMap, null);

        String csaCodigo = null, csaNome = null;
        String corCodigo = null, corNome = null;
        List<String> orgCodigos = null;

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim") &&
                !TextHelper.isNull(getParametro("periodoIni", parameterMap)) && !TextHelper.isNull(getParametro("periodoFim", parameterMap))) {
            final String strIniPeriodo = getParametro("periodoIni", parameterMap);
            final String strFimPeriodo = getParametro("periodoFim", parameterMap);

            filtrosAplicados.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo));

            final String periodoIni = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            final String periodoFim = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");

            criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoIni);
            criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFim);
        }

        if (parameterMap.containsKey("orgCodigo")) {
            List<String> orgNames = null;
            final String values[] = (parameterMap.get("orgCodigo"));
            filtrosAplicados.append((filtrosAplicados.length() > 0 ? System.getProperty("line.separator") : "") + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ""));
            if ("".equals(values[0])) {
                filtrosAplicados.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase());
            } else {
                orgCodigos = new ArrayList<>();
                orgNames = new ArrayList<>();
                try {
                    for (final String value : values) {
                        final String[] separ = value.split(";");
                        orgCodigos.add(separ[0]);
                        orgNames.add(separ[2] + " ");
                    }
                    filtrosAplicados.append(orgNames.toString().replace("[", "").replace("]", ""));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        if (parameterMap.containsKey("csaCodigo") || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
            String values[] = (parameterMap.get("csaCodigo"));
            filtrosAplicados.append((filtrosAplicados.length() > 0 ? System.getProperty("line.separator") : "") + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ""));

            if (responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
                if (responsavel.isCsa()) {
                    csaCodigo = responsavel.getCodigoEntidade();
                } else if (responsavel.isCor()) {
                    csaCodigo = responsavel.getCodigoEntidadePai();
                }

                try {
                    final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                    final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(csaCodigo, responsavel);
                    csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
                } catch (final ConsignatariaControllerException e) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.falha.recuperar.consignataria.arg0", responsavel, "(" + e.getMessage() + ")"));
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.falha.recuperar.consignataria.arg0", responsavel, ""), e);
                }
            } else if ((values.length != 0) && !"".equals(values[0])) {
                values = values[0].split(";");
                csaCodigo = values[0];
                csaNome = values[2];
            }

            if (!TextHelper.isNull(csaCodigo)) {
                criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                filtrosAplicados.append(csaNome);
            } else {
                filtrosAplicados.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase());
            }
        }

        if (parameterMap.containsKey("corCodigo")) {
            String values[] = (parameterMap.get("corCodigo"));
            filtrosAplicados.append((filtrosAplicados.length() > 0 ? System.getProperty("line.separator") : "") + ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ""));

            if (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corCodigo = responsavel.getCodigoEntidade();
                try {
                    final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                    final CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(corCodigo, responsavel);
                    corNome = (String) cor.getAttribute(Columns.COR_NOME);
                } catch (final ConsignatariaControllerException e) {
                    codigoRetorno = ERRO;
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.falha.recuperar.correspondente.arg0", responsavel, "(" + e.getMessage() + ")"));
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.falha.recuperar.correspondente.arg0", responsavel, ""), e);
                }
            } else if ((values.length != 0) && !"".equals(values[0])) {
                values = values[0].split(";");
                corCodigo = values[0];
                corNome = values[2];
            }

            if (!TextHelper.isNull(corCodigo)) {
                criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
                filtrosAplicados.append(corNome);
            } else {
                filtrosAplicados.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase());
            }
        }

        if (parameterMap.containsKey("svcCodigo")) {
            final String svcs[] = parameterMap.get("svcCodigo");
            filtrosAplicados.append((filtrosAplicados.length() > 0 ? System.getProperty("line.separator") : "") + ApplicationResourcesHelper.getMessage("rotulo.servico.plural.arg0", responsavel, ""));

            if ((svcs == null) || ((svcs.length == 1) && TextHelper.isNull(svcs[0]))) {
                filtrosAplicados.append(ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase());
            } else {
                final List<String> svcCodigos = new ArrayList<>();
                String values[];
                for (int i = 0; i < svcs.length; i++) {
                    values = svcs[i].split(";");
                    svcCodigos.add(values[0]);
                    if (i > 0) {
                        filtrosAplicados.append(", ");
                    }
                    filtrosAplicados.append(values[2]);
                }
                criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
            }
        }

        try {
            final String strFormato = getStrFormato();

            String tituloGrupo = null;
            if (responsavel.isCseSupOrg()) {
                tituloGrupo = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase();
            } else if (responsavel.isCsaCor()) {
                tituloGrupo = ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel).toUpperCase();
            }
            final boolean adeAptasPortabilidade = getParametro("adePortabilidadeCartao", parameterMap) != null ? "true".equals(getParametro("adePortabilidadeCartao", parameterMap)) : false;
            final boolean adeNuncaExistiuLancamento = getParametro("adeSemLancamento", parameterMap) != null ? "true".equals(getParametro("adeSemLancamento", parameterMap)) : false;

            criterio.setAttribute("adeNuncaExistiuLancamento", adeNuncaExistiuLancamento);
            criterio.setAttribute("adeAptasPortabilidade", adeAptasPortabilidade);

            final String titulo = relatorio.getTitulo();
            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("TITULO_GRUPO", tituloGrupo);
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toUpperCase());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, filtrosAplicados.toString());
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("RESPONSAVEL", responsavel);

            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(ex.getMessage(), ex);
        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
