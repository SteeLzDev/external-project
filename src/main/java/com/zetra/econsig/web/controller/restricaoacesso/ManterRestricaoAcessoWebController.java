package com.zetra.econsig.web.controller.restricaoacesso;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.restricaoacesso.RegraRestricaoAcessoViewHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterRestricaoAcessoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manter Restrição Acesso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/restricaoAcesso" })
public class ManterRestricaoAcessoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterRestricaoAcessoWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (request.getParameter("codigo") != null && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            return buscarLista(request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            //Valida o token de sessão para evitar a chamada direta à operação
            if (request.getParameter("codigo") != null && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rraCodigoSelected = request.getParameter("codigo");
            if (!TextHelper.isNull(rraCodigoSelected)) {
                RegraRestricaoAcessoViewHelper.excluirRegraRestricaoAcesso(rraCodigoSelected, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.excluida.sucesso", responsavel));
            }

            return buscarLista(request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    private String buscarLista(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws InstantiationException, IllegalAccessException, ParametroControllerException {

        boolean podeEditarRra = responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO);

        String csaCodigo = null;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCor()) {
            csaCodigo = responsavel.getCodigoEntidadePai();
        }

        List<TransferObject> restricoes = null;

        int total = parametroController.countRestricoesAcesso(csaCodigo, responsavel);
        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        restricoes = parametroController.lstRestricoesAcesso(csaCodigo, offset, size, responsavel);

        configurarPaginador("../v3/restricaoAcesso?acao=iniciar", "rotulo.paginacao.titulo.restricao.aceso", total, size, null, false, request, model);

        model.addAttribute("podeEditarRra", podeEditarRra);
        model.addAttribute("restricoes", restricoes);

        return viewRedirect("jsp/manterRestricaoAcesso/listarRestricaoAcesso", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tipo = responsavel.getTipoEntidade();
            String usuCodigo = responsavel.getUsuCodigo();
            String entCodigo = responsavel.getCodigoEntidade();

            Map<String, String> funcoes = null;
            if (!responsavel.isCseSup()) {
                funcoes = usuarioController.selectFuncoesRestricaoAcesso(usuCodigo, entCodigo, tipo, responsavel);
            } else {
                funcoes = usuarioController.selectFuncoesRestricaoAcesso(null, null, null, responsavel);
            }

            List<Map.Entry<String, String>> listTO = new LinkedList<>(funcoes.entrySet());

            Collections.sort(listTO, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

            model.addAttribute("listTO", listTO);

            return viewRedirect("jsp/manterRestricaoAcesso/editarRestricaoAcesso", request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            RegraRestricaoAcessoViewHelper.createRegraRestricaoAcesso(request, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.criado.sucesso", responsavel));
            request.setAttribute("url64", TextHelper.encode64("../v3/restricaoAcesso?acao=iniciar&" + SynchronizerToken.generateToken4URL(request)));
            return "jsp/redirecionador/redirecionar";

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64("../v3/restricaoAcesso?acao=iniciar&" + SynchronizerToken.generateToken4URL(request)));
            return "jsp/redirecionador/redirecionar";
        }

    }

}
