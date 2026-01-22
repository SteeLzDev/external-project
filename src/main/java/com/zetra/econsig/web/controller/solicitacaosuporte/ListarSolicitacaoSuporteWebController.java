package com.zetra.econsig.web.controller.solicitacaosuporte;

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

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.solicitacaosuporte.SolicitacaoSuporteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ListarSolicitacaoSuporteWebController</p>
 * <p>Description: Controlador Web responsável por listar e consultar as solicitações de suporte.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarSolicitacaoSuporte" })
public class ListarSolicitacaoSuporteWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarSolicitacaoSuporteWebController.class);

    @Autowired
    private SolicitacaoSuporteController solicitacaoSuporteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String usuCodigo = responsavel.getUsuCodigo();

        boolean podeEditarSos = responsavel.temPermissao(CodedValues.FUN_EDITAR_SOLICITACAO_SUPORTE);
        List<TransferObject> lstSolicitacao = new ArrayList<>();
        try {
            lstSolicitacao = solicitacaoSuporteController.lstSolicitacaoSuporte(usuCodigo, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
        model.addAttribute("podeEditarSos", podeEditarSos);
        model.addAttribute("lstSolicitacao", lstSolicitacao);

        return viewRedirect("jsp/manterSolicitacaoSuporte/listarSolicitacaoSuporte", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String sosCodigo = JspHelper.verificaVarQryStr(request, "sosCodigo");
        TransferObject solicitacaoTO = null;
        try {
            solicitacaoTO = solicitacaoSuporteController.findSolicitacaoSuporte(sosCodigo, responsavel);
            solicitacaoSuporteController.atualizaSolicitacaoSuporte(solicitacaoTO, sosCodigo, responsavel);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitacao.suporte.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute("solicitacao", solicitacaoTO);

        return viewRedirect("jsp/manterSolicitacaoSuporte/consultarSolicitacaoSuporte", request, session, model, responsavel);
    }
}