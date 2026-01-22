package com.zetra.econsig.job.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ReportControllerException;
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
 * <p> Title: ProcessaRelatorioUsuarios</p>
 * <p> Description: Classe para processamento de relatorio de usuários</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioUsuarios extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioUsuarios.class);

    public ProcessaRelatorioUsuarios(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        // seta o nome do arquivo
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.usuarios", responsavel), responsavel, parameterMap, null);

        String csaCodigo, cseCodigo, corCodigo, orgCodigo;
        String filtroCsa, filtroCse, filtroCor, filtroOrg;
        Boolean includeSuporte = null;
        String csaCodigoTodosCor = null;

        String[] stu = (parameterMap.get("STU_CODIGO"));
        List<String> stuCodigos = stu != null ? Arrays.asList(stu) : null;

        if ((responsavel.isCseSup() || responsavel.isCsa()) && parameterMap.containsKey("csaCodigo")) {
            String[] csa = TextHelper.split(parameterMap.get("csaCodigo")[0], ";");
            csaCodigo = csa[0];
            if (TextHelper.isNull(csaCodigo) && !responsavel.isCsa()) {
                filtroCsa = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();

            } else {
                filtroCsa = null;
            }

            if (!TextHelper.isNull(csaCodigo) && !csaCodigo.equalsIgnoreCase("NENHUM")) {
                filtroCsa = csa[2];
            }

        } else {
            csaCodigo = null;
            filtroCsa = null;
        }

        if ((responsavel.isCseSup() || responsavel.isCsaCor()) && parameterMap.containsKey("corCodigo")) {
            String[] cor = TextHelper.split(parameterMap.get("corCodigo")[0], ";");
            corCodigo = cor[0];

            if (corCodigo.equals("TODOS_DA_CSA")) {
                // csa selecionada para exibir usuários de todos os correspondentes
                csaCodigoTodosCor = cor[1];
            }

            if (TextHelper.isNull(corCodigo) && !responsavel.isCor()) {
                filtroCor = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
                if (responsavel.isCsa()) {
                    // marca a csa do responsável para exibir apenas usuários de seus correspondentes
                    csaCodigoTodosCor = responsavel.getCsaCodigo();
                }
            } else if (corCodigo.equals("TODOS_DA_CSA") && !responsavel.isCor()) {
                filtroCor = ApplicationResourcesHelper.getMessage("rotulo.campo.csa.todos.simples", responsavel);
            } else {
                filtroCor = null;
            }

            if (!TextHelper.isNull(corCodigo) && !corCodigo.equalsIgnoreCase("NENHUM") && !corCodigo.equalsIgnoreCase("TODOS_DA_CSA")) {
                filtroCor = cor[2];
            }
        } else {
            corCodigo = null;
            filtroCor = null;
        }

        if (responsavel.isCseSupOrg() && parameterMap.containsKey("orgCodigo")) {
            String[] org = TextHelper.split(parameterMap.get("orgCodigo")[0], ";");
            orgCodigo = org[0];
            if (TextHelper.isNull(orgCodigo) && (!responsavel.isOrg() || responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO))) {
                filtroOrg = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
            } else {
                filtroOrg = null;
            }

            if (!TextHelper.isNull(orgCodigo) && !orgCodigo.equalsIgnoreCase("NENHUM")) {
                filtroOrg = org[2];
            }
        } else {
            orgCodigo = null;
            filtroOrg = null;
        }

        if (responsavel.isCseSup() && parameterMap.containsKey("cseCodigo")) {
            String[] cse = TextHelper.split(parameterMap.get("cseCodigo")[0], ";");
            cseCodigo = cse[0];

            if (cseCodigo != null && !cseCodigo.equalsIgnoreCase("NENHUM")) {
                cseCodigo = responsavel.getCodigoEntidade();

                try {
                    filtroCse = new ConsignanteDelegate().findConsignante(cseCodigo, responsavel).getCseNome();
                } catch (ConsignanteControllerException e) {
                    filtroCse = null;
                    LOG.error(e.getMessage(), e);
                }
            } else {
                filtroCse = null;
            }
        } else {
            cseCodigo = null;
            filtroCse = null;
        }

        // Se é usuário de consignatária, e não foi escolhida a opção NENHUM,
        // define o valor de csaCodigo pois o mesmo virá vazio
        if (responsavel.isCsa() && csaCodigo != null && !csaCodigo.equalsIgnoreCase("NENHUM")) {
            csaCodigo = responsavel.getCodigoEntidade();
            try {
                filtroCsa = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel).getCsaNomeIdentificador();
            } catch (ConsignatariaControllerException e) {
                filtroCsa = null;
                LOG.error(e.getMessage(), e);
            }
        }

        // Se é usuário de órgão, e não foi escolhida a opção NENHUM,
        // define o valor de orgCodigo pois o mesmo virá vazio
        if (responsavel.isOrg() && orgCodigo != null && !orgCodigo.equalsIgnoreCase("NENHUM")) {
            orgCodigo = responsavel.getCodigoEntidade();
            try {
                OrgaoTransferObject oto = new ConsignanteDelegate().findOrgao(orgCodigo, responsavel);
                filtroOrg = oto.getOrgNome() + " - " + oto.getOrgIdentificador();
            } catch (ConsignanteControllerException e) {
                filtroCsa = null;
                LOG.error(e.getMessage(), e);
            }
        }

        // Se é usuário de correspondente, e não foi escolhida a opção NENHUM,
        // define o valor de corCodigo pois o mesmo virá vazio
        if (responsavel.isCor() && corCodigo != null && !corCodigo.equalsIgnoreCase("NENHUM")) {
            corCodigo = responsavel.getCodigoEntidade();
            try {
                filtroCor = new ConsignatariaDelegate().findCorrespondente(corCodigo, responsavel).getCorNomeIdentificador();
            } catch (ConsignatariaControllerException e) {
                filtroCor = null;
                LOG.error(e.getMessage(), e);
            }
        }

        // Se é usuário de consignante ou de suporte, adciona o filtro de seleção de inclusão de usuários de suporte
        if (responsavel.isCseSup()) {
            String[] chkIncludeSuporte = parameterMap.get("includeSuporte");
            includeSuporte = Boolean.valueOf(chkIncludeSuporte != null ? chkIncludeSuporte[0] : "false");
        }

        if ((cseCodigo == null || cseCodigo.equalsIgnoreCase("NENHUM")) &&
                (orgCodigo == null || orgCodigo.equalsIgnoreCase("NENHUM")) &&
                (csaCodigo == null || csaCodigo.equalsIgnoreCase("NENHUM")) &&
                (corCodigo == null || corCodigo.equalsIgnoreCase("NENHUM")) &&
                (includeSuporte == null || !includeSuporte)) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.um.campo.deve.ser.diferente.nenhum", responsavel);
            return;
        }

        List<String> funCodigos = getFiltroFunCodigo(parameterMap, null, null, session, responsavel);

        criterio.setAttribute(Columns.UCA_CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.UCE_CSE_CODIGO, cseCodigo);
        criterio.setAttribute(Columns.UCO_COR_CODIGO, corCodigo);
        criterio.setAttribute(Columns.UOR_ORG_CODIGO, orgCodigo);
        criterio.setAttribute(ReportManager.CRITERIO_INCLUDE_SUPORTE, includeSuporte);
        criterio.setAttribute(Columns.FUN_CODIGO, funCodigos);
        criterio.setAttribute(Columns.STU_CODIGO, stuCodigos);
        criterio.setAttribute("CSA_CODIGO_TODOS_COR", csaCodigoTodosCor);

        String titulo = relatorio.getTitulo();
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_COR, filtroCor);
        parameters.put(ReportManager.PARAM_NAME_CSA, filtroCsa);
        parameters.put(ReportManager.PARAM_NAME_CSE, filtroCse);
        parameters.put(ReportManager.PARAM_NAME_ORGAO, filtroOrg);
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);

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
