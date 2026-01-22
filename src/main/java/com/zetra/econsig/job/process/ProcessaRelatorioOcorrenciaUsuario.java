package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
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

/**
 * <p>Title: ProcessaRelatorioOcorrenciaUsuario</p>
 * <p>Description: Classe para processamento de relatorio de Ocorrência de Usuário
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioOcorrenciaUsuario extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioOcorrenciaServidor.class);

    public ProcessaRelatorioOcorrenciaUsuario(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        Boolean includeSuporte = null;
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        String opLogin = "";
        String tipoEntidade = "";
        List<String> tocCodigos = new ArrayList<>();
        List<String> tmoCodigos = new ArrayList<>();

        HashMap<String, Object> parameters = new HashMap<>();

        if (parameterMap.containsKey("periodoIni")&& parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String orgCodigo = responsavel.getOrgCodigo();
        String csaCodigo = responsavel.getCsaCodigo();
        String corCodigo = responsavel.getCorCodigo();
        String cseCodigo = responsavel.getCseCodigo();
        String corCsa = responsavel.getCsaCodigo();

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);
        StringBuilder subTitulo = new StringBuilder("");

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.usu", responsavel), responsavel, parameterMap, null);

        if (parameterMap.containsKey("tipoEntidade")) {
            tipoEntidade = getParametro("tipoEntidade", parameterMap);
        }

        //de acordo com os códigos que estão na sessão, define que tipo de entidade aos quais irão pertencer os usuários
        if (TextHelper.isNull(tipoEntidade)) {
            if (!TextHelper.isNull(orgCodigo)) {
                tipoEntidade = AcessoSistema.ENTIDADE_ORG;
            } else if (!TextHelper.isNull(csaCodigo)) {
                tipoEntidade = (!TextHelper.isNull(corCodigo) && !corCodigo.equals("NENHUM")) ? AcessoSistema.ENTIDADE_COR : AcessoSistema.ENTIDADE_CSA;
            } else if (!TextHelper.isNull(corCodigo)) {
                tipoEntidade = AcessoSistema.ENTIDADE_COR;
            }
        }

        if (parameterMap.containsKey("cseCodigo")) {
            String values[] = (parameterMap.get("cseCodigo"));
            if (values.length == 0 || values[0].equals("")) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            } else if (values[0].equalsIgnoreCase("NENHUM")) {
                cseCodigo = values[0];
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                cseCodigo = values[0];
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, values[2]));

            }
        }

        if (parameterMap.containsKey("orgCodigo")) {
            String values[] = (parameterMap.get("orgCodigo"));
            if (values.length == 0 || values[0].equals("") || (orgCodigo != null && values[0].equals(orgCodigo))) {
                if (!TextHelper.isNull(orgCodigo)) {
                    ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                    OrgaoTransferObject orgTransferObject;
                    try {
                        orgTransferObject = cseDelegate.findOrgao(orgCodigo, responsavel);
                        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, orgTransferObject.getOrgNome()));
                    } catch (ConsignanteControllerException e) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        return;
                    }
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                }
            } else if (values[0].equalsIgnoreCase("NENHUM")) {
                orgCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                orgCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, values[2]));
            }
        }

        if (parameterMap.containsKey("csaCodigo")) {
            String values[] = (parameterMap.get("csaCodigo"));
            if (values.length == 0 || values[0].equals("") || (csaCodigo != null && values[0].equals(csaCodigo))) {
                if (!TextHelper.isNull(csaCodigo)) {
                    try {
                        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                        ConsignatariaTransferObject csaTransferObject = csaDelegate.findConsignataria(csaCodigo, responsavel);
                        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, csaTransferObject.getCsaNome()));

                    } catch (ConsignatariaControllerException e) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        return;
                    }
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                }
            } else if (values[0].equalsIgnoreCase("NENHUM")) {
                csaCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                csaCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));

            }
        }

        if (parameterMap.containsKey("corCodigo")) {
            String values[] = (parameterMap.get("corCodigo"));
            if (values.length == 0 || values[0].equals("") || (corCodigo != null && values[0].equals(corCodigo))) {
                if (!TextHelper.isNull(corCodigo)) {
                    try {
                        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                        CorrespondenteTransferObject corTransferObject = csaDelegate.findCorrespondente(corCodigo, responsavel);
                        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, corTransferObject.getCorNome()));

                    } catch (ConsignatariaControllerException e) {
                        codigoRetorno = ERRO;
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel) + ".<br>";
                        return;
                    }
                } else {
                    subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                }
            } else if (values[0].equalsIgnoreCase("NENHUM")) {
                corCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum.simples", responsavel).toUpperCase()));
            } else if (values[0].equalsIgnoreCase("TODOS_DA_CSA")) {
                corCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.csa.todos.simples", responsavel).toUpperCase()));
            } else {
                values = values[0].split(";");
                corCodigo = values[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, values[2]));
            }
        }

        // login responsável
        opLogin = getParametro("OP_LOGIN", parameterMap);
        if (!TextHelper.isNull(opLogin)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.login.responsavel.arg0", responsavel, opLogin));
        }

        // tipo ocorrencia
        if (parameterMap.containsKey("tocCodigo")) {
            String tocs[] = (parameterMap.get("tocCodigo"));
            if (!tocs[0].equals("")) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ""));
                for (int i = 0; i < tocs.length; i++) {
                    values = tocs[i].split(";");
                    tocCodigos.add(values[0]);
                    if (i == (tocs.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                tocCodigos = CodedValues.TOC_CODIGOS_USUARIO;
            }
        }

        // tipo motivo operacao
        if (parameterMap.containsKey("tmoCodigo")) {
            String tmos[] = (parameterMap.get("tmoCodigo"));
            if (!tmos[0].equals("")) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ""));
                for (int i = 0; i < tmos.length; i++) {
                    values = tmos[i].split(";");
                    tmoCodigos.add(values[0]);
                    if (i == (tmos.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        // Se é usuário de consignante ou de suporte, adciona o filtro de seleção de inclusão de usuários de suporte
        if (responsavel.isCseSup()) {
            String[] chkIncludeSuporte = parameterMap.get("includeSuporte");
            includeSuporte = Boolean.valueOf(chkIncludeSuporte != null ? chkIncludeSuporte[0] : "false");
        }

        if ((TextHelper.isNull(tipoEntidade) &&
                (!TextHelper.isNull(corCodigo) && corCodigo.equals("NENHUM")) &&
                (!TextHelper.isNull(csaCodigo) && csaCodigo.equals("NENHUM")) &&
                (!TextHelper.isNull(orgCodigo) && orgCodigo.equals("NENHUM")) &&
                (!TextHelper.isNull(cseCodigo) && cseCodigo.equals("NENHUM"))&&
                (includeSuporte == null || !includeSuporte)) ||
                ((!TextHelper.isNull(tipoEntidade) && tipoEntidade.equals("CSA")) &&
                        (!TextHelper.isNull(corCodigo) && corCodigo.equals("NENHUM")) &&
                        (!TextHelper.isNull(csaCodigo) && csaCodigo.equals("NENHUM")))) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.pelo.menos.uma.entidade.deve.estar.com.opcao.diferente.nenhuma", responsavel) + "<br>";
            return;
        }

        if ((TextHelper.isNull(csaCodigo) || csaCodigo.equals("NENHUM")) && corCodigo != null && corCodigo.equalsIgnoreCase("TODOS_DA_CSA")) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.uma.consignataria.deve.estar.selecionada.para.uso.filtro.todos.da.consignataria.no.correspondente", responsavel) + "<br>";
            return;
        }

        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
        criterio.setAttribute("ORG_CODIGO", orgCodigo);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("COR_CODIGO", corCodigo);
        criterio.setAttribute("CSE_CODIGO", cseCodigo);
        criterio.setAttribute("OP_LOGIN", opLogin);
        criterio.setAttribute("tipoEntidade", tipoEntidade);
        criterio.setAttribute("COR_CSA", corCsa);
        criterio.setAttribute(Columns.TOC_CODIGO, tocCodigos);
        criterio.setAttribute(Columns.TMO_CODIGO, tmoCodigos);
        criterio.setAttribute(ReportManager.CRITERIO_INCLUDE_SUPORTE, includeSuporte);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        String reportName = null;
        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

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
