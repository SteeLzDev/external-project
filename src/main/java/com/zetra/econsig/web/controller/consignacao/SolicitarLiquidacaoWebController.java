package com.zetra.econsig.web.controller.consignacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SolicitarLiquidacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Solicitar Liquidação.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:$
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/solicitarLiquidacao" })
public class SolicitarLiquidacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitarLiquidacaoWebController.class);

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        carregarListaServico(request, session, model, responsavel);

        // Habilita exibição de campo para filtro por data
        model.addAttribute("exibirFiltroDataInclusao", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.solicitar.liquidacao", responsavel));
        model.addAttribute("acaoFormulario", "../v3/solicitarLiquidacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/solicitarLiquidacao?acao=solicitar";
        String funCodigo = CodedValues.FUN_SOLICITAR_LIQUIDAR_CONSIGNACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=solicitar" })
    public String solicitar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String tmoCodigo = request.getParameter("TMO_CODIGO");

            List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);

            liquidarConsignacaoController.solicitarLiquidacao(adeCodigos, ApplicationResourcesHelper.getMessage("mensagem.info.solicitar.liquidacao.observacao", responsavel), tmoCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.solicitar.liquidacao.sucesso", responsavel));

            ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para Solicitar liquidar consignação
        String link = "../v3/solicitarLiquidacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.solicitar.liquidacao", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.solicitar.liquidacao", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.liquidacao.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitar.liquidacao", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("SOLICITAR_LIQUIDAR_CONSIGNACAO", CodedValues.FUN_SOLICITAR_LIQUIDAR_CONSIGNACAO, descricao, descricaoCompleta, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/solicitarLiquidacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitar_liquidacao");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        try {
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("") ) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }

    @RequestMapping(params = { "acao=efetivarCancelamentoSolicitacao" })
    public String efetivarCancelamentoSolicitacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/solicitarLiquidacao?acao=cancelarSolicitacao";
        String funCodigo = CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=cancelarSolicitacao" })
    public String cancelarSolicitacao(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String tmoCodigo = request.getParameter("TMO_CODIGO");

            List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);

            liquidarConsignacaoController.cancelarSolicitacaoLiquidacao(adeCodigos, ApplicationResourcesHelper.getMessage("mensagem.info.cancelar.solicitar.liquidacao.observacao", responsavel), tmoCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.cancelar.solicitar.liquidacao.sucesso", responsavel));

            //Verifica se a consignatária pode ser desbloqueada caso tenha sido bloqueada por este motivo ou outro.
            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
            consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);

            ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
