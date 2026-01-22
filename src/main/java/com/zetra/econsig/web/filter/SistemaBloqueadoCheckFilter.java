package com.zetra.econsig.web.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: SessionFilter</p>
 * <p>Description: Filtro que confere se usuário ou entidade está bloqueado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class SistemaBloqueadoCheckFilter extends EConsigFilter {

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private SistemaController sistemaController;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (prosseguirSemExecutar(request)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = ((HttpServletRequest) request).getSession();
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String recurso = getRecurso(httpRequest);
        AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);
        AcessoRecurso recursoAcessado = responsavel.getRecursoAcessado();
        if (recursoAcessado == null) {
            recursoAcessado = AcessoRecursoHelper.identificarAcessoRecurso(recurso, httpRequest.getParameterMap(), responsavel);
        }
        String funCodigo = (recursoAcessado != null ? recursoAcessado.getFunCodigo() : null);
        String funDescricao = (recursoAcessado != null ? recursoAcessado.getFunDescricao() : null);
        boolean podeBloquearRecurso = (recursoAcessado != null && recursoAcessado.isAcrBloqueio());

        if (podeBloquearRecurso) {
            try {
                if (funCodigo != null) {
                    // Verifica se é um usuário de uma entidade consignatária que está bloqueada
                    // Se a entidade estiver bloqueada o usuário não pode consultar margem
                    if (responsavel.isCsa() && funCodigo.equals(CodedValues.FUN_CONS_MARGEM)) {
                        ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                        if (csa != null &&
                                (csa.getCsaAtivo().equals(CodedValues.STS_INATIVO) || csa.getCsaAtivo().equals(CodedValues.STS_INDISP))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.csa.bloqueada.arg0.arg1", responsavel, funDescricao, csa.getCsaNome()));
                            httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                            return;
                        }
                    } else if (responsavel.isCor() && funCodigo.equals(CodedValues.FUN_CONS_MARGEM)) {
                        ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                        CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(responsavel.getCorCodigo(), responsavel);
                        if ((csa != null && !csa.getCsaAtivo().equals(CodedValues.STS_ATIVO)) ||
                                (cor != null && !cor.getCorAtivo().equals(CodedValues.STS_ATIVO))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.cor.bloqueada.arg0.arg1", responsavel, funDescricao, cor.getCorNome()));
                            httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                            return;
                        }
                    }

                    // Verifica se o sistema permite o login de usuário correspondente vinculado a uma entidade bloqueada
                    boolean permiteLoginUsuCorEntidadeBloq = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_LOGIN_USU_COR_ENTIDADE_BLOQ, responsavel);
                    if (!permiteLoginUsuCorEntidadeBloq) {
                        // Verifica se é um usuário de uma entidade correspondente que está bloqueada
                        // ou que a entidade consignatária do correspondente está bloqueada
                        // Se a entidade estiver bloqueada o usuário não pode acessar nenhuma opção no sistema
                        if (responsavel.isCor()) {
                            ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                            CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(responsavel.getCorCodigo(), responsavel);
                            if ((csa != null && !csa.getCsaAtivo().equals(CodedValues.STS_ATIVO)) ||
                                    (cor != null && !cor.getCorAtivo().equals(CodedValues.STS_ATIVO))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.cor.bloqueada.arg0.arg1", responsavel, funDescricao, cor.getCorNome()));
                                httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                                return;
                            }
                        }
                    }
                }

                Short status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                boolean bloqueado = status.equals(CodedValues.STS_INATIVO);
                boolean indisponivel = status.equals(CodedValues.STS_INDISP);

                // Se o sistema está bloqueado ou indisponível e o usuário não possui o perfil de administrador,
                // então redireciona o usuário para página de erro
                if ((bloqueado || indisponivel) && !responsavel.temPermissao(CodedValues.FUN_EFETUAR_LOGIN_SISTEMA_BLOQUEADO)) {
                    String rotuloSistema = (ParamSist.getInstance().getParam(CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, responsavel) != null &&
                            !ParamSist.getInstance().getParam(CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, responsavel).equals("")) ?
                            ParamSist.getInstance().getParam(CodedValues.TPC_ALTERA_MSG_BLOQUEIO_SISTEMA, responsavel).toString() :
                            ApplicationResourcesHelper.getMessage("rotulo.sistema.indisponivel", responsavel);

                    session.setAttribute(CodedValues.MSG_ERRO, rotuloSistema);
                    httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao&tipo=indisponivel").forward(request, response);
                    return;
                }

            } catch (Exception ex) {
                throw new ServletException(ex.getMessage());
            }

            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
