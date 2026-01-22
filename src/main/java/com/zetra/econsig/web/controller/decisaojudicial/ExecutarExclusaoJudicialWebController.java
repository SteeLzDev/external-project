package com.zetra.econsig.web.controller.decisaojudicial;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: ExecutarExclusaoJudicialWebController</p>
 * <p>Description: Web Controller para exclusão de consignação em Decisão Judicial (liquidação ou suspensão)</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarExclusaoJudicial" })
public class ExecutarExclusaoJudicialWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarExclusaoJudicialWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.opcao.excluir.consignacao", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarExclusaoJudicial");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        return sadCodigos;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
         // Operação de liquidação e suspensão listam os mesmos status:
        // SAD_EMANDAMENTO, SAD_DEFERIDA e SAD_ESTOQUE, SAD_ESTOQUE_MENSAL e SAD_ESTOQUE_NAO_LIBERADO caso existam
        criterio.setAttribute("TIPO_OPERACAO", "liquidar");

        // Somente consignações que integram folha
        criterio.setAttribute(Columns.ADE_INT_FOLHA, CodedValues.INTEGRA_FOLHA_SIM);

        // Ordenadas pela ordem de desconto em folha de forma reversa
        criterio.setAttribute("TIPO_ORDENACAO", "3");
        return criterio;
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            String nseCodigo = (String) autdes.getAttribute(Columns.NSE_CODIGO);

            String urlDestino = null;
            if (CodedValues.NSE_EMPRESTIMO.equals(nseCodigo) || CodedValues.NSE_FINANCIAMENTO.equals(nseCodigo) || CodedValues.NSE_AUXILIO_FINANCEIRO.equals(nseCodigo)
                    || CodedValues.NSE_SEGURO.equals(nseCodigo) || CodedValues.NSE_PREVIDENCIA.equals(nseCodigo)) {
                urlDestino = "../v3/executarSuspensaoJudicial?acao=confirmarSuspensao";
            } else {
                urlDestino = "../v3/executarLiquidacaoJudicial?acao=efetivarAcao";
            }

            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";

        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona operacao para excluir (liquidar/suspender) consignacao
        String link = "../v3/executarExclusaoJudicial?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.excluir.consignacao.decisao.judicial.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.excluir.consignacao.decisao.judicial", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.excluir.consignacao.decisao.judicial.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.exclusao.consignacao.decisao.judicial", responsavel);
        String msgAdicionalConfirmacao = null;

        acoes.add(new AcaoConsignacao("EXECUTAR_DECISAO_JUDICIAL", CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL, descricao, descricaoCompleta, "liquidar_contrato.gif", "btnExcluirConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null ,null));

        return acoes;
    }

    @Override
    protected String tratarConsignacaoNaoEncontrada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return "forward:/v3/executarDecisaoJudicial?acao=iniciar";
    }
}
