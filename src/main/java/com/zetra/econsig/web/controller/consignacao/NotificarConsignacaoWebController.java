package com.zetra.econsig.web.controller.consignacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: NotificarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso NotificarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/notificarConsignacao" })
public class NotificarConsignacaoWebController extends AbstractListarTodasConsignacoesWebController {

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.notificar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/notificarConsignacao");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);

        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/notificarConsignacao?acao=confirmarNotificacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.notificar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.notifica.cse", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.nao.selecionado.notifica.cse", responsavel);

        acoes.add(new AcaoConsignacao("NOTIFICA_CSA", CodedValues.FUN_NOTIFICA_CONSIGNACAO_A_CSE, descricao, descricaoCompleta, "desbloqueado.gif", "btnNotificarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkNotificar"));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "notificar");

        if (responsavel.isCor()) {
            criterio.setAttribute(Columns.COR_CODIGO, responsavel.getCodigoEntidade());
        }

        if (responsavel.isCsa()) {
            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
        }

        if (responsavel.isOrg()) {
            criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
        }

        return criterio;
    }

    @RequestMapping(params = { "acao=confirmarNotificacao" })
    public String confirmarSuspensao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        try {
            SynchronizerToken.saveToken(request);

            List<String> adesCodigosIncluir = Arrays.asList(JspHelper.verificaVarQryStr(request, "adesCodigosIncluir").split(","));
            List<String> adesCodigosRemover = Arrays.asList(JspHelper.verificaVarQryStr(request, "adesCodigosRemover").split(","));

            autorizacaoController.registraNotificacoesCse(adesCodigosIncluir, adesCodigosRemover, responsavel);

            String msg = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.sucesso", responsavel);
            session.setAttribute(CodedValues.MSG_INFO, msg);

            ParamSession paramSession = ParamSession.getParamSession(session);

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (AutorizacaoControllerException  e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
