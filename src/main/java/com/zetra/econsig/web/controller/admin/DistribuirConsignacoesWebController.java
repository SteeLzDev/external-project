package com.zetra.econsig.web.controller.admin;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.job.process.ProcessaRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.RelatorioDistribuirConsignacoesPorServicos;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.AbstractWebController;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * <p>Title: DistribuirConsignacoesWebController</p>
 * <p>Description: Controlador Web para caso de uso Distribuir Consignações por Serviços.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/distribuirConsignacoesPorServicos" })
public class DistribuirConsignacoesWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DistribuirConsignacoesWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private TransferirConsignacaoController transferirController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        // Carrega informações necessárias
        carregarListaConsignataria(request, session, model, responsavel);
        carregarListaServico(request, session, model, responsavel);

        return viewRedirect("jsp/distribuirConsignacoes/distribuirConsignacoes", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return processarRequisicao(request, response, session, model, true);
    }

    @RequestMapping(params = { "acao=executar" })
    public String executar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return processarRequisicao(request, response, session, model, false);
    }

    private String processarRequisicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, boolean validar) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
        String svcCodigoOrigem = JspHelper.verificaVarQryStr(request, "svcCodigoOrigem");
        List<String> svcCodigosDestino = (!TextHelper.isNull(request.getParameter("svcCodigoDestino")) ? Arrays.asList(request.getParameterValues("svcCodigoDestino")) : null);
        List<String> csaCodigos = (!TextHelper.isNull(request.getParameter("csaCodigo")) ? Arrays.asList(request.getParameterValues("csaCodigo")) : null);

        String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
        String ocaObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");

        try {
            List<TransferObject> relatorioFinal = transferirController.distribuirConsignacoesPorServicos(svcCodigoOrigem, svcCodigosDestino, csaCodigos, rseMatricula, serCpf, tmoCodigo, ocaObs, validar, responsavel);

            // Gera relatório contendo o resultado do processamento
            ProcessaRelatorioDistribuicaoConsignacao processaRelatorio = new ProcessaRelatorioDistribuicaoConsignacao(request.getParameterMap(), relatorioFinal, session, responsavel);
            processaRelatorio.run();

            String nomeArquivoDownload = URLEncoder.encode(TextHelper.forJavaScriptAttribute(processaRelatorio.nomeRelatorio), "UTF-8");
            String msgDownload = "<a href=\"#no-back\" onclick=\"postData('../v3/downloadArquivo?arquivo_nome=" + nomeArquivoDownload + "&tipo=relatorio&subtipo=consignacoes&skip_history=true','download');\">"
                               + ApplicationResourcesHelper.getMessage("mensagem.informacao.para.fazer.download.arquivo.clique.aqui", responsavel) + "</a>";

            // Define mensagem de sucesso para o usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(validar ? "mensagem.sucesso.distribuir.consignacoes.validar" : "mensagem.sucesso.distribuir.consignacoes.executar", responsavel));
            model.addAttribute("msgDownload", msgDownload);

        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }

    private static class ProcessaRelatorioDistribuicaoConsignacao extends ProcessaRelatorio {

        private final List<TransferObject> relatorioFinal;
        private String nomeRelatorio;

        public ProcessaRelatorioDistribuicaoConsignacao(Map<String, String[]> parameterMap, List<TransferObject> relatorioFinal, HttpSession session, AcessoSistema responsavel) {
            super(new Relatorio("distribuir_consignacao", ApplicationResourcesHelper.getMessage("rotulo.relatorio.distribuir.consignacoes", responsavel), "", RelatorioDistribuirConsignacoesPorServicos.class.getName(), null, "DistribuirConsignacaoPorServico.jasper", "", "", "", "", "", true, false, "N", null), parameterMap, session, false, responsavel);

            // Seta o proprietário do processo
            owner = responsavel.getUsuCodigo();

            // Seta a descrição do processo
            setDescricao(relatorio.getTitulo());

            this.relatorioFinal = relatorioFinal;
        }

        @Override
        protected void executar() {
            try {
                String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.distribuir.consignacoes", responsavel), responsavel, parameterMap, null);

                // Grava o relatório na pasta de relatório de consignações
                String rootPath = ParamSist.getDiretorioRaizArquivos();
                String fileName = rootPath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "consignacoes";

                Map<String, Object> parameters = new HashMap<>();
                parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
                parameters.put(ReportManager.REPORT_DIR_EXPORT, fileName);
                parameters.put(ReportManager.REPORT_FILE_NAME, nome);

                // Converte a lista de CustomTransferObject em uma lista de Beans para criar um Datasource
                List<RegistroRelatorio> listaRelatorio = relatorioFinal.stream().map(RegistroRelatorio::new).collect(Collectors.toList());
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaRelatorio);

                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                String reportName = reportController.makeReport("PDF", parameters, relatorio, dataSource, responsavel);

                geraZip(nome.toString(), reportName);
                nomeRelatorio = nome.toString() + ".zip";

            } catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }

    public static class RegistroRelatorio {
        private final String adeNumero;
        private final String serNome;
        private final String serCpf;
        private final String rseMatricula;
        private final String observacao;

        public RegistroRelatorio(TransferObject dados) {
            adeNumero = dados.getAttribute(Columns.ADE_NUMERO).toString();
            serNome = dados.getAttribute(Columns.SER_NOME).toString();
            serCpf = dados.getAttribute(Columns.SER_CPF).toString();
            rseMatricula = dados.getAttribute(Columns.RSE_MATRICULA).toString();
            observacao = dados.getAttribute("OBS").toString();
        }

        public String getAdeNumero() {
            return adeNumero;
        }

        public String getSerNome() {
            return serNome;
        }

        public String getSerCpf() {
            return serCpf;
        }

        public String getRseMatricula() {
            return rseMatricula;
        }

        public String getObservacao() {
            return observacao;
        }

        public String getOrdem() {
            return "";
        }
    }
}
