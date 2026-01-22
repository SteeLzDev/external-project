package com.zetra.econsig.web.controller.leilao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.pontuacao.PontuacaoServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractEfetivarAcaoConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReverterPontuacaoLeilaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Reverter Pontuação de Leilão.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision:$
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reverterPontuacaoLeilao" })
public class ReverterPontuacaoLeilaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {

    @Autowired
    private AutorizacaoController autorizacaoController;
    @Autowired
    private PontuacaoServidorController pontuacaoServidorController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.reverter.pontuacao.leilao", responsavel));
        model.addAttribute("acaoFormulario", "../v3/reverterPontuacaoLeilao");
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/reverterPontuacaoLeilao?acao=reverterPontuacaoLeilao";
        String funCodigo = CodedValues.FUN_REVER_LEILAO_NAO_CONCRETIZADO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=reverterPontuacaoLeilao" })
    public String reverterPontuacao(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
        	CustomTransferObject tmo = null;
        	if (!TextHelper.isNull(request.getParameter("TMO_CODIGO"))) {
        		tmo = new CustomTransferObject();
        		tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
        		tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
        		tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
        	}

        	TransferObject ade = recuperarAde(responsavel, adeCodigo);
            String rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();
            String tmoCodigo = (String)tmo.getAttribute(Columns.TMO_CODIGO);

            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_REVER_LEILAO_NAO_CONCRETIZADO, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.rever.pontuacao.leilao", responsavel), null, null, null, null, tmoCodigo, responsavel);
            pontuacaoServidorController.calcularPontuacao(rseCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reverter.pontuacao.leilao.concluido.sucesso", responsavel));

            ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private TransferObject recuperarAde(AcessoSistema responsavel, String adeCodigo) throws AutorizacaoControllerException {
        TransferObject ade = null;
        List<String> adeCodigos = new ArrayList<>();
        adeCodigos.add(adeCodigo);
        // validaPermissao = false pois o servidor solicitou à terceiros a informação de propostas
        List<TransferObject> autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos, false, responsavel);
        if (autdes != null && !autdes.isEmpty()) {
            ade = autdes.get(0);
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);
        }
        return ade;
    }
}
