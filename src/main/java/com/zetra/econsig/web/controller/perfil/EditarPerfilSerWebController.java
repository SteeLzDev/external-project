package com.zetra.econsig.web.controller.perfil;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarPerfilSerWebController</p>
 * <p>Description: Web Controller para edição de perfil de SER</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class EditarPerfilSerWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarPerfilSer" }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        final String tipo = AcessoSistema.ENTIDADE_SER;
        final String perCodigo = CodedValues.PER_CODIGO_SERVIDOR;

        List<TransferObject> funcoesPermitidasPerfil = null;
        List<String> funcoesPerfil = null;
        String perDescricao = "";
        String perVisivel = "";
        Date perDataExpiracao = null;
        final Boolean entPodeAltPer = false;
        String perIpAcesso = "";
        String perDdnsAcesso = "";

        try {
            funcoesPermitidasPerfil = usuarioController.lstFuncoesPermitidasPerfil(tipo, null, responsavel);
            funcoesPerfil = usuarioController.getFuncaoPerfil(tipo, null, perCodigo, responsavel);

            final Perfil perfil = usuarioController.findPerfil(perCodigo, responsavel);
            perDescricao = perfil.getPerDescricao();
            perVisivel = (perfil.getPerVisivel() != null ? perfil.getPerVisivel() : "");
            perDataExpiracao = perfil.getPerDataExpiracao();
            perIpAcesso = perfil.getPerIpAcesso();
            perDdnsAcesso = perfil.getPerDdnsAcesso();
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            funcoesPermitidasPerfil = new ArrayList<>();
            funcoesPerfil = new ArrayList<>();
        }

        final boolean podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_SER);
        final String linkAction = "../v3/manterPerfilSer?acao=editar";
        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.perfil.edicao.perfil.de", responsavel) + " " + ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);

        // Exibe Botao que leva ao rodapé
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("podeEditarPerfil", podeEditarPerfil);
        model.addAttribute("titulo", titulo);
        model.addAttribute("tituloPagina", titulo);
        model.addAttribute("linkAction", linkAction);

        model.addAttribute("tipo", tipo);
        model.addAttribute("perCodigo", perCodigo);
        model.addAttribute("funcoes", funcoesPermitidasPerfil);
        model.addAttribute("perFunCodigos", funcoesPerfil);
        model.addAttribute("perDescricao", perDescricao);
        model.addAttribute("perVisivel", perVisivel);
        model.addAttribute("perDataExpiracao", perDataExpiracao);
        model.addAttribute("entPodeAltPer",entPodeAltPer);
        model.addAttribute("perIpAcesso",perIpAcesso);
        model.addAttribute("perDdnsAcesso",perDdnsAcesso);

        return viewRedirect("jsp/manterPerfil/editarPerfil", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterPerfilSer" }, params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final String tipo = AcessoSistema.ENTIDADE_SER;
        final String perCodigo = CodedValues.PER_CODIGO_SERVIDOR;
        final String perDescricao = JspHelper.verificaVarQryStr(request, "PER_DESCRICAO");

        if ("".equals(perDescricao.trim())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.perfil.informar.descricao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String[] funcao = request.getParameterValues("funcao");
            final List<String> funCodigos = funcao != null ? Arrays.asList(funcao) : new ArrayList<>();
            final Date perDataExpiracao = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO"), LocaleHelper.getDatePattern()) : null;
            final String perIpAcesso = JspHelper.verificaVarQryStr(request, "perfil_ip_acesso");
            final String perDdnsAcesso = JspHelper.verificaVarQryStr(request, "perfil_ddns_acesso");

            usuarioController.updatePerfil(tipo, null, perCodigo, perDescricao, null, perDataExpiracao, null, null , null, perIpAcesso, perDdnsAcesso, funCodigos, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.perfil.alteracoes.salvas.sucesso", responsavel));
        } catch (UsuarioControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }
}
