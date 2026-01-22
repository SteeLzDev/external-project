package com.zetra.econsig.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleAcessoSeguranca;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RestricaoAcessoFilter</p>
 * <p>Description: Filtro que verifica cache de restrições de acesso .</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class RestricaoAcessoFilter extends EConsigFilter {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RestricaoAcessoFilter.class);

    private static final List<String> semRestricaoAcesso;

    static {
        semRestricaoAcesso = new ArrayList<>();

        semRestricaoAcesso.add("/");
        semRestricaoAcesso.add("/index.jsp");
        semRestricaoAcesso.add("/v3/autenticarUsuario");
        semRestricaoAcesso.add("/v3/autenticar");
        semRestricaoAcesso.add("/v3/expirarSistema");
        semRestricaoAcesso.add("/v3/exibirMensagem");
        semRestricaoAcesso.add("/v3/redirecionarTermoUsu");
        semRestricaoAcesso.add("/v3/redirecionarTermoSer");
        semRestricaoAcesso.add("/v3/validaPdf");
        semRestricaoAcesso.add("/WEB-INF/jsp/visualizarPaginaErro/visualizarMensagem.jsp");

        semRestricaoAcesso.add("/img/view.jsp");
        semRestricaoAcesso.add("/js/mensagens.jsp");

        semRestricaoAcesso.add("/admin/limpar_cache.jsp");
        semRestricaoAcesso.add("/admin/versao.jsp");
        semRestricaoAcesso.add("/admin/status.jsp");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (prosseguirSemExecutar(request)) {
            chain.doFilter(request, response);
            return;
        }

        AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String recurso = getRecurso(httpRequest);

        try {
            // verifica se usuário não foi bloqueado por tentativas erradas de acesso de segurança
            if (ControleAcessoSeguranca.CONTROLESEGURANCA.usuarioBloqueadoPorAcessoIlegal(responsavel)) {
                String telaLogin = responsavel.isSer() ? LoginHelper.getPaginaLoginServidor() : LoginHelper.getPaginaLogin();
                telaLogin = LoginHelper.getPaginaLogin();

                telaLogin += "?t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");

                // Invalida a sessão do usuário e redireciona para página de login
                HttpSession session = httpRequest.getSession();
                new LogDelegate (responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_LOGOUT).write();
                session.invalidate();
                session = httpRequest.getSession();
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.seguranca", responsavel));
                ControleAcessoSeguranca.CONTROLESEGURANCA.removeMapUsuario(responsavel.getUsuCodigo());
                ((HttpServletResponse) response).sendRedirect(telaLogin);
                return;
            }

            if (!semRestricaoAcesso.contains(recurso)) {
                ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);

                if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
                    if (restricao.getGrauRestricao() == ControleRestricaoAcesso.GrauRestricao.RestricaoOperacao) {
                        HttpSession session = httpRequest.getSession();
                        gravaLogErro(ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.operacao", responsavel), recurso, responsavel);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.operacao", responsavel) + " : " + restricao.getDescricao());
                        httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal").forward(request, response);
                        return;
                    } else if (restricao.getGrauRestricao() == ControleRestricaoAcesso.GrauRestricao.RestricaoGeral) {
                        String telaLogin = null;
                        if (responsavel.getUsuCodigo() == null) {
                            telaLogin = LoginHelper.getPaginaLogin();
                            telaLogin += "?t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
                        } else {
                            telaLogin = "../v3/expirarSistema?acao=iniciar";
                        }

                        // Invalida a sessão do usuário e redireciona para página de login
                        HttpSession session = httpRequest.getSession();
                        new LogDelegate (responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_LOGOUT).write();
                        session.invalidate();
                        session = httpRequest.getSession();
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.restricao.acesso.geral", responsavel) + " : " + restricao.getDescricao());
                        ((HttpServletResponse) response).sendRedirect(telaLogin);
                        return;
                    }
                }
            }
        } catch (ZetraException e) {
            httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession();
            LOG.error(e.getMessage(), e);
            gravaLogErro(e.getMessage(), recurso, responsavel);
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private void gravaLogErro(String mensagem, String recurso, AcessoSistema responsavel) {
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.GERAL, null, Log.LOG_ERRO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", responsavel, mensagem));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.recurso.arg0", responsavel, recurso));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
