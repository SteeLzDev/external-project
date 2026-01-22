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

import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.consignacao.AbstractEfetivarAcaoConsignacaoWebController;

/**
 * <p>Title: ExecutarAutorizarDescontoDecisaoJudicialWebController</p>
 * <p>Description: Web Controller para autorizar desconto de consignação em Decisão Judicial</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarAutorizarDescontoDecisaoJudicial" })
public class ExecutarAutorizarDescontoJudicialWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarAutorizarDescontoJudicialWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.autorizar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarAutorizarDescontoDecisaoJudicial");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);
        return sadCodigos;
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean autorizaDesconto = JspHelper.verificaVarQryStr(request, "AUTORIZA_DESCONTO").equals("true");

        String urlDestino = "../v3/executarAutorizarDescontoDecisaoJudicial?acao=salvar&AUTORIZA_DESCONTO="+autorizaDesconto;
        String funCodigo = CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona operacao para desautorizar desconto em folha
        String link = "../v3/executarAutorizarDescontoDecisaoJudicial?acao=efetivarAcao&AUTORIZA_DESCONTO=false";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.desautorizar.decisao.judicial.abrev", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.desautorizar.decisao.judicial", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.desautorizar.decisao.judicial.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desautorizar.decisao.judicial", responsavel);

        acoes.add(new AcaoConsignacao("DESAUTORIZAR_DECISAO_JUDICIAL", CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL, descricao, descricaoCompleta, "desautorizar_contrato.gif", "btnDesautorizarConsignacao", msgAlternativa, msgConfirmacao, null, link, null ,null));

        // Adiciona operacao para autorizar desconto em folha
        link = "../v3/executarAutorizarDescontoDecisaoJudicial?acao=efetivarAcao&AUTORIZA_DESCONTO=true";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.autorizar.decisao.judicial.abrev", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.autorizar.decisao.judicial", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.autorizar.decisao.judicial.clique.aqui", responsavel);
        msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizar.decisao.judicial", responsavel);

        acoes.add(new AcaoConsignacao("AUTORIZAR_DECISAO_JUDICIAL", CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL, descricao, descricaoCompleta, "autorizar_contrato.gif", "btnAutorizarConsignacao", msgAlternativa, msgConfirmacao, null, link, null ,null));

        //Adiciona o detalhar consignação
        link = "../v3/retirarConsignacaoCompra?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSession paramSession = ParamSession.getParamSession(session);
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && responsavel.temPermissao(CodedValues.FUN_ALTERAR_CADASTRO_BENEFICIARIOS)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean autorizaDesconto = JspHelper.verificaVarQryStr(request, "AUTORIZA_DESCONTO").equals("true");

        String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");

        try {
            if(autorizaDesconto) {
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TDA_SIM, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.autorizar.decisao.judicial.sucesso", responsavel));
            } else {
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TDA_NAO, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.desautorizar.decisao.judicial.sucesso", responsavel));
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @Override
    protected String tratarConsignacaoNaoEncontrada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return "forward:/v3/executarDecisaoJudicial?acao=iniciar";
    }
}
