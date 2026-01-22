package com.zetra.econsig.web.controller.perfil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractManterPerfilWebController</p>
 * <p>Description: Classe abstrata para uso Manutenção de Perfil.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
*/

public abstract class AbstractManterPerfilWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractManterPerfilWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String codigo = getCodigo(request);
        final String tipo = getTipo(request);

        // Valida o token
        if ((!responsavel.isSup() || !AcessoSistema.ENTIDADE_SUP.equals(tipo)) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> perfil = null;

        final String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")) ? Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")) : filtro_tipo;
        } catch (final Exception ex1) {
            LOG.error(ex1.getMessage(), ex1);
        }

        try {
            final CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                criterio.setAttribute(getColunaAtivo(request), CodedValues.STS_INATIVO);

                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(getColunaAtivo(request), CodedValues.STS_ATIVO);

                // Outros
            }  else if (!"".equals(filtro) && (filtro_tipo != -1)) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.PER_DESCRICAO;
                        break;

                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }
            perfil = usuarioController.lstPerfil(tipo, codigo, criterio, responsavel);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            perfil = new ArrayList<>();
        }

        model.addAttribute("codigo", codigo);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("filtro", filtro);
        model.addAttribute("perfil", perfil);

        return viewRedirect("jsp/manterPerfil/listarPerfil", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final String tipo = getTipo(request);
        final String codigo = getCodigo(request);

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Short status = null;

        final String msgErro = "";

        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");

            status = Short.valueOf(JspHelper.verificaVarQryStr(request, "STATUS"));

            final String msgRet=(status.equals(CodedValues.STS_ATIVO) ? ApplicationResourcesHelper.getMessage("mensagem.perfil.bloqueado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.perfil.desbloqueado.sucesso", responsavel));
            status = (status.equals(CodedValues.STS_ATIVO) ? CodedValues.STS_INATIVO : CodedValues.STS_ATIVO);

            usuarioController.updatePerfil(tipo, codigo, perCodigo, null, null, null, null, status, null, null, null, null, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, msgRet);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final String tipo = getTipo(request);
        String codigo = getCodigo(request);

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if ("".equals(codigo)) {
            codigo = responsavel.getCodigoEntidade();
        }

        final String msgErro = "";

        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");
            usuarioController.removePerfil(tipo, codigo, perCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.perfil.removido.sucesso", responsavel));

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }


    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final String tipo = getTipo(request);
        String codigo = getCodigo(request);
        final String perOrigem = JspHelper.verificaVarQryStr(request, "copia_perfil");

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (codigo.isEmpty()) {
            codigo = responsavel.getCodigoEntidade();
        }

        String msgErro = "";

        if ("".equals(JspHelper.verificaVarQryStr(request, "PER_DESCRICAO"))) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.perfil.informar.descricao", responsavel);
        }

        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String perDescricao = JspHelper.verificaVarQryStr(request, "PER_DESCRICAO");
            String perVisivel = null;
            Date perDataExpiracao = null;
            String perEntAltera = JspHelper.verificaVarQryStr(request, "PER_ENT_ALTERA");
            String perAutoDesbloqueio = CodedValues.TPA_NAO;
            //Pegando o IP e DDNS da tela para remoção ou edição no perfil.
            final String perIpAcesso = JspHelper.verificaVarQryStr(request, "perfil_ip_acesso");
            final String perDdnsAcesso = JspHelper.verificaVarQryStr(request, "perfil_ddns_acesso");

            //Pegando o valor para acionar a remoção da restrição de ip.
            final String perRemoveRestricaoUsu = JspHelper.verificaVarQryStr(request, "PER_REMOVE_RES_USU");

            if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "PER_VISIVEL"))) {
                perVisivel = JspHelper.verificaVarQryStr(request, "PER_VISIVEL");
            }
            if (!"".equals(JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO"))) {
                final String perDataExpiracaoStr = JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO");
                perDataExpiracao = DateHelper.parse(perDataExpiracaoStr, LocaleHelper.getDatePattern());
            }

            if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                perAutoDesbloqueio = JspHelper.verificaVarQryStr(request, "PER_AUTO_DESBLOQUEIO");
            }

            final String perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");
            final String[] destino = request.getParameterValues("aplica_perfil");

            //Valida se o usuario que esta fazendo a edição poderia realmente estar ediando o perfil
            final Perfil perfilEditado = usuarioController.findPerfil(perCodigo, responsavel);
            boolean entPodeAltPer = CodedValues.TPA_SIM.equals(perfilEditado.getPerEntAltera());

            if (perEntAltera.isEmpty()){
            	perEntAltera = perfilEditado.getPerEntAltera();
            }

            if (!entPodeAltPer) {
                if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    entPodeAltPer = responsavel.isSup();
                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    entPodeAltPer = responsavel.isCseSup();
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    entPodeAltPer = responsavel.isCseSupOrg();
                } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    entPodeAltPer = !responsavel.isCor();
                }

                if (!entPodeAltPer) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.entidade.nao.altera.perfil", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            if (!TextHelper.isNull(perOrigem)) {
                // Copia as funções do perfil selecionado para o perfil atual
                final List<String> perDestino = new ArrayList<>();
                perDestino.add(perCodigo);
                usuarioController.copyPerfil(tipo, codigo, perOrigem, perDestino, responsavel);
            } else if ((destino != null) && (destino.length > 0)) {
                // Copia as funções do perfil atual para os perfis selecionados
                final List<String> perDestino = Arrays.asList(destino);
                usuarioController.copyPerfil(tipo, codigo, perCodigo, perDestino, responsavel);
            } else {
                final String[] funcao = request.getParameterValues("funcao");
                final List<String> funCodigos = funcao != null ? Arrays.asList(funcao) : new ArrayList<>();
                usuarioController.updatePerfil(tipo, codigo, perCodigo, perDescricao, perVisivel, perDataExpiracao, perEntAltera, null, perAutoDesbloqueio, perIpAcesso, perDdnsAcesso, funCodigos, responsavel);
            }

            if ("S".equalsIgnoreCase(perRemoveRestricaoUsu)) {
                usuarioController.removeRestricaoUsuarioPerfil(perCodigo, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.perfil.alteracoes.salvas.sucesso", responsavel));

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //declaração de variáveis
        final String codigo = getCodigo(request);
        final String operacao = getOperacao(request);
        final String tipo = getTipo(request);

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> funcoes = null;
        String perCodigo = "";
        List<TransferObject> perfil = null;
        List<TransferObject> paramSist = null;
        String entAltera = "";
        String perIpAcesso = "";
        String perDdnsAcesso = "";

        Boolean entPodeAltPer = false;
        if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
            entPodeAltPer = responsavel.isSup();
        } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
            entPodeAltPer = responsavel.isCseSup();
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
            entPodeAltPer = responsavel.isCseSupOrg();
        } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
            entPodeAltPer = !responsavel.isCor();
        }

        try {
            if (!operacao.startsWith("inserir")) {
                perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");
                if (TextHelper.isNull(perCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
            final CustomTransferObject criterio = new CustomTransferObject();
            perfil = usuarioController.lstPerfil(tipo, codigo, criterio, responsavel);

            for (final TransferObject perfilSing : perfil) {
                if (perfilSing.getAttribute(Columns.PER_CODIGO).equals(perCodigo)) {
                    entAltera = (String) perfilSing.getAttribute(Columns.PER_ENT_ALTERA);
                    perIpAcesso = (String) perfilSing.getAttribute(Columns.PER_IP_ACESSO);
                    perDdnsAcesso = (String) perfilSing.getAttribute(Columns.PER_DDNS_ACESSO);
                }
            }

            paramSist = parametroController.selectParamSistCseEditavelPerfil(tipo, perCodigo, responsavel);
            funcoes = usuarioController.lstFuncoesPermitidasPerfil(tipo, codigo, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            funcoes = new ArrayList<>();
        }

        if (!entPodeAltPer && !CodedValues.TPA_SIM.equals(entAltera)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.perfil.entidade.nao.altera", responsavel));
        }

        model.addAttribute("tipo",tipo);
        model.addAttribute("codigo",codigo);
        model.addAttribute("operacao",operacao);
        model.addAttribute("perCodigo",perCodigo);
        model.addAttribute("funcoes",funcoes);
        model.addAttribute("perfil",perfil);
        model.addAttribute("entPodeAltPer",entPodeAltPer);
        model.addAttribute("entAltera",entAltera);
        model.addAttribute("paramSist",paramSist);
        model.addAttribute("perIpAcesso",perIpAcesso);
        model.addAttribute("perDdnsAcesso",perDdnsAcesso);

        return viewRedirect("jsp/manterPerfil/editarPerfil", request, session, model, responsavel);
    }


    @RequestMapping(params = { "acao=inserir" })
    public String inserir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final String tipo = getTipo(request);
        String codigo = getCodigo(request);
        final String perOrigem = JspHelper.verificaVarQryStr(request, "copia_perfil");

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
        	validaTipoEntidade(request, responsavel);
        } catch (final ZetraException e) {
        	// Redireciona para página de erro
        	session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
        	return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if ("".equals(codigo)) {
            codigo = responsavel.getCodigoEntidade();
        }

        String msgErro = "";

        if ("".equals(JspHelper.verificaVarQryStr(request, "PER_DESCRICAO"))) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.perfil.informar.descricao", responsavel);
        }

        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String perDescricao = JspHelper.verificaVarQryStr(request, "PER_DESCRICAO");
            String perVisivel = null;

            if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "PER_VISIVEL"))) {
                perVisivel = JspHelper.verificaVarQryStr(request, "PER_VISIVEL");
            }

            final String[] funcao = request.getParameterValues("funcao");
            final List<String> funCodigos = funcao != null ? Arrays.asList(funcao) : new ArrayList<>();

            final Date perDataExpiracao = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "PER_DATA_EXPIRACAO"), LocaleHelper.getDatePattern()) : null;

            final String perEntAltera = JspHelper.verificaVarQryStr(request, "PER_ENT_ALTERA");

            final String perAutoDesbloqueio = JspHelper.verificaVarQryStr(request, "PER_AUTO_DESBLOQUEIO");
            //Pegando o IP e DDNS da tela para remoção ou edição no perfil.
            final String perIpAcesso = JspHelper.verificaVarQryStr(request, "perfil_ip_acesso");
            final String perDdnsAcesso = JspHelper.verificaVarQryStr(request, "perfil_ddns_acesso");

            final String perCodigo = usuarioController.createPerfil(tipo, codigo, perDescricao, perVisivel, perDataExpiracao, perEntAltera, perOrigem, perAutoDesbloqueio, funCodigos, perIpAcesso, perDdnsAcesso, responsavel);
            link = link.replace("inserir", "editar") + "&PER_CODIGO=" + perCodigo;

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.perfil.criado.sucesso", responsavel));


        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }

    protected void validaTipoEntidade(HttpServletRequest request, AcessoSistema responsavel) throws LogControllerException, ZetraException {
    	final String tipo = getTipo(request);
    	final String codEntidade = getCodigo(request);

    	if (isTipoEntidadeInvalido(tipo, codEntidade, responsavel)) {
    		// Registra log de erro
    		final com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
    		log.add(ApplicationResourcesHelper.getMessage("mensagem.perfil.tentativa.manutencao.peril.sem.permissao", responsavel));
    		log.write();
    		throw new ZetraException("mensagem.erro.interno.contate.administrador", responsavel);
    	}
    }

    public String getLinkVoltarListagem(HttpServletRequest request, HttpSession session) throws InstantiationException, IllegalAccessException {
        final ParamSession paramSession = ParamSession.getParamSession(session);
        return SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
    }


    /**
     * recupera a coluna de ativo dependendo da entidade
     * @param request
     * @param responsavel
     * @return
     */
    protected abstract String getColunaAtivo(HttpServletRequest request);

    /**
     * recupera tipo
     * @param request
     * @param responsavel
     * @return
     */
    protected abstract String getTipo(HttpServletRequest request);

    /**
     * recupera operacao
     * @param request
     * @param responsavel
     * @return
     */
    protected abstract String getOperacao(HttpServletRequest request);

    protected String getCodigo(HttpServletRequest request) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        return (!"".equals(JspHelper.verificaVarQryStr(request, "codigo"))) ? JspHelper.verificaVarQryStr(request, "codigo") : (String) responsavel.getCodigoEntidade();
    }
}