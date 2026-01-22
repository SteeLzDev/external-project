package com.zetra.econsig.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.entidade.TermoAdesaoTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.sistema.TermoAdesaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.cert.CERTClient;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SessionFilter</p>
 * <p>Description: Verifica se a sessão é válida ao acessar o recurso requisitado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class SessionFilter extends EConsigFilter {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SessionFilter.class);

    private static final List<String> pgPermitidasAoExpirar = new ArrayList<>();
    private static final List<String> pgPermitidasAoConfirmarLeitura = new ArrayList<>();
    private static final List<String> pgPermitidasAoValidarCertificadoDigital = new ArrayList<>();
    private static final List<String> pgPermitidasAoAceitarTermoDeUso = new ArrayList<>();
    private static final List<String> pgPermitidasAoAceitarPoliticaPrivacidade = new ArrayList<>();
    private static final List<String> pgPermitidasAlterarEmailOuTelefone = new ArrayList<> ();
    private static final List<String> pgPermitidasLeilaoFinalizadoSemContato = new ArrayList<> ();
    private static final List<String> pgPermitidasAtualizarCadasatro = new ArrayList<> ();
    private static final List<String> pgPermitidasAutorizarDescontoParcialSer = new ArrayList<> ();
    private static final List<String> pgPermitidasReconhecimentoFacialPrimeiroAcesso = new ArrayList<> ();
    private static final List<String> pgPermitidasAoValidarTotp = new ArrayList<> ();

    private static final List<String> pgPermitidasTermoAdesao = new ArrayList<> ();
    private static final List<String> pgPermitidasAoFormularioPesquisa = new ArrayList<>();


    static {
        final String[] pgPermitidas = {
                "/v3/iniciarFsConsignante",
                "/v3/iniciarFsConsignataria",
                "/v3/iniciarFsServidor",
                "/v3/visualizarTopoServidor",
                "/v3/expirarSistema",
                "/v3/visualizarRodape",
        };

        pgPermitidasAoValidarCertificadoDigital.add("/v3/autenticarUsuarioCertificadoDigital");
        pgPermitidasAoValidarCertificadoDigital.add("/v3/solicitarCertificadoDigital");
        pgPermitidasAoValidarCertificadoDigital.add("/v3/solicitarCertificado");
        pgPermitidasAoValidarCertificadoDigital.add("/v3/validarCertificado");
        pgPermitidasAoValidarCertificadoDigital.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoExpirar.add("/v3/alterarSenha");
        pgPermitidasAoExpirar.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoConfirmarLeitura.add("/v3/confirmarMensagem");
        pgPermitidasAoConfirmarLeitura.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoAceitarTermoDeUso.add("/arquivos/upload_anexo.jsp");
        pgPermitidasAoAceitarTermoDeUso.add("/v3/visualizarTermoUso");
        pgPermitidasAoAceitarTermoDeUso.add("/v3/aceitarTermoUso");
        pgPermitidasAoAceitarTermoDeUso.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoAceitarPoliticaPrivacidade.add("/v3/visualizarPoliticaPrivacidade");
        pgPermitidasAoAceitarPoliticaPrivacidade.add("/v3/aceitarPoliticaPrivacidade");
        pgPermitidasAoAceitarPoliticaPrivacidade.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAlterarEmailOuTelefone.add("/v3/atualizarEmailTelefone");
        pgPermitidasAlterarEmailOuTelefone.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasLeilaoFinalizadoSemContato.add("/v3/informarContatoLeilaoFinalizado");
        pgPermitidasLeilaoFinalizadoSemContato.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAtualizarCadasatro.add("/v3/atualizarCadastro");
        pgPermitidasAtualizarCadasatro.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAutorizarDescontoParcialSer.add("/v3/autorizarDescontoParcialSer");
        pgPermitidasAutorizarDescontoParcialSer.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasReconhecimentoFacialPrimeiroAcesso.add("/v3/reconhecimentoFacial");
        pgPermitidasReconhecimentoFacialPrimeiroAcesso.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoValidarTotp.add("/v3/autenticarUsuario");
        pgPermitidasAoValidarTotp.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasTermoAdesao.add("/v3/informarTermoAdesao");
        pgPermitidasTermoAdesao.addAll(Arrays.asList(pgPermitidas));

        pgPermitidasAoFormularioPesquisa.add("/v3/formularioResposta");
        pgPermitidasAoFormularioPesquisa.add("/v3/formularioResposta/salvarResposta");
        pgPermitidasAoFormularioPesquisa.addAll(Arrays.asList(pgPermitidas));
    }

    @Autowired
    private CERTClient certClient;

    @Autowired
    private TermoAdesaoController termoAdesaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (prosseguirSemExecutar(request)) {
            chain.doFilter(request, response);
            return;
        }

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();
        keepSessionCentralizador(httpRequest);

        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String recurso = getRecurso(httpRequest);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(httpRequest);
        final AcessoRecurso recursoAcessado = AcessoRecursoHelper.identificarAcessoRecurso(recurso, httpRequest.getParameterMap(), responsavel);
        if ((recursoAcessado != null) && recursoAcessado.isAcrSessao()) {
            boolean sessaoInvalida = ((session.getAttribute(CodedValues.SESSAO_INVALIDA) != null) && Boolean.parseBoolean(session.getAttribute(CodedValues.SESSAO_INVALIDA).toString())) || responsavel.isSessaoInvalidaErroSeg();

            UsuarioTransferObject usu = null;

            if (!TextHelper.isNull(responsavel.getUsuCodigo())) {
                try {
                    usu = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
                } catch (final UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    sessaoInvalida = true;
                }
            }

            // Invalida a sessão se o usuário estiver bloqueado. Um usuário com status
            // diferente de ativo não pode se autenticar, porém ele pode ser bloqueado
            // enquanto está acessando o sistema, e com isso devemos invalidar a sessão
            if (!sessaoInvalida && ((usu == null) || (usu.getStuCodigo() == null) || !CodedValues.STU_ATIVO.equals(usu.getStuCodigo()))) {
                sessaoInvalida = true;
            }

            final String userAgentAtual = httpRequest.getHeader("user-agent");
            final String userAgentSession = (String) session.getAttribute("userAgentLogin");
            final boolean userAgentChanged = !TextHelper.isNull(userAgentSession) && !TextHelper.isNull(userAgentAtual) && !userAgentAtual.equals(userAgentSession);

            // Invalida a sessão se houver alteração do user-agent do mesmo usuário
            if (!sessaoInvalida && userAgentChanged) {
                sessaoInvalida = true;
                LOG.warn("userAgentAtual [" + userAgentAtual + "] <> userAgentSession [" + userAgentSession + "]");
                session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.acesso.simultaneo.invalido", responsavel));
            }

            // Invalida a sessão se o IP de acesso usado no login for diferente do IP
            // de acesso da requisição atual. Caso o usuário ou sua entidade tenha
            // restrição de IP/DNS, e o IP for um dos permitidos (Load-balance) não
            // invalida a sessão do usuário.
            if (!sessaoInvalida) {
                final String ipAcessoLogin = responsavel.getIpUsuario();
                final String ipAcessoAtual = JspHelper.getRemoteAddr(httpRequest);
                final Integer portaLogicaAcessoAtual = JspHelper.getRemotePort(httpRequest);
                if (!ipAcessoAtual.equals(ipAcessoLogin)) {
                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_TROCAR_IP_ACESSO_MESMA_SESSAO, CodedValues.TPC_SIM, responsavel)) {
                        // Atualiza o IP do responsável
                        responsavel.setIpUsuario(ipAcessoAtual);
                        responsavel.setPortaLogicaUsuario(portaLogicaAcessoAtual);
                        sessaoInvalida = false;

                    } else if (JspHelper.isThisIpAddressLocal(ipAcessoAtual) && (session.getAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA) != null)) {
                        // Se é uma requisição vinda do próprio servidor, verifica se é a operação de confirmação
                        // de operações sensíveis que foi requisitada, e caso seja, a sessão não deve ser invalidada
                        // mas também não deve trocar o IP do usuário
                        sessaoInvalida = false;

                    } else {
                        // Se não permite a troca de IP, verifica se o IP atual é válido de acordo com as
                        // restrições do usuário ou de sua entidade
                        sessaoInvalida = true;

                        if (!TextHelper.isNull(responsavel.getUsuCodigo())) {
                            try {
                                final String[] restricaoEntidade = UsuarioHelper.obtemRestricaoIpDDNSEntidadeUsu(responsavel);
                                if ((!TextHelper.isNull(usu.getUsuIpAcesso()) && JspHelper.validaIp(ipAcessoAtual, usu.getUsuIpAcesso())) ||
                                        (!TextHelper.isNull(usu.getUsuDDNSAcesso()) && JspHelper.validaDDNS(ipAcessoAtual, usu.getUsuDDNSAcesso())) ||
                                        (!TextHelper.isNull(restricaoEntidade[0]) && JspHelper.validaIp(ipAcessoAtual, restricaoEntidade[0])) ||
                                        (!TextHelper.isNull(restricaoEntidade[1]) && JspHelper.validaDDNS(ipAcessoAtual, restricaoEntidade[1]))) {
                                    // Se o usuário ou sua entidade possui restrição de acesso, e o ip de acesso atual
                                    // é válido para as restrições, então não invalida a sessão
                                    sessaoInvalida = false;
                                    // Atualiza o IP do responsável
                                    responsavel.setIpUsuario(ipAcessoAtual);
                                    responsavel.setPortaLogicaUsuario(portaLogicaAcessoAtual);
                                }
                            } catch (final ViewHelperException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }

                        if (sessaoInvalida) {
                            session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.endereco.acesso.diferente", responsavel));
                        }
                    }
                }
            }

            if (responsavel.getUsuCodigo() == null) {
                httpRequest.getRequestDispatcher("/v3/expirarSistema?acao=iniciar").forward(request, response);
                return;
            } else if (sessaoInvalida) {
                final String mensagem = session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA) != null? session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA).toString(): "";
                // Invalida a sessão atual
                session.invalidate();
                session = httpRequest.getSession();
                request.setAttribute("mensagemSessaoExpirada", mensagem);
                httpRequest.getRequestDispatcher("/v3/expirarSistema?acao=iniciar").forward(request, response);
                return;
            } else if (session.getAttribute("valida_certificado_digital") != null) {
                boolean direcionar = true;

                if (pgPermitidasAoValidarCertificadoDigital.contains(recurso)) {
                    direcionar = false;
                }
                if (direcionar) {
                    if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_CERT, responsavel))) {
                        httpRequest.getRequestDispatcher("/v3/solicitarCertificado?acao=iniciar").forward(request, response);
                        return;
                    } else {
                        httpRequest.getRequestDispatcher("/v3/solicitarCertificadoDigital?acao=iniciar").forward(request, response);
                        return;
                    }
                }
            } else if ((session.getAttribute("exigeValidacaoTotp") != null)
                    && "1".equals(session.getAttribute("exigeValidacaoTotp"))) {
                boolean direcionar = true;

                if (pgPermitidasAoValidarTotp.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/autenticarUsuario?acao=validarTotp").forward(request, response);
                    return;
                }
            } else if ((session.getAttribute("AceitarTermoDeUso") != null)
                    && "1".equals(session.getAttribute("AceitarTermoDeUso"))) {
                boolean direcionar = true;

                if (pgPermitidasAoAceitarTermoDeUso.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/visualizarTermoUso?acao=iniciar").forward(request, response);
                    return;
                }
            } else if ((session.getAttribute("AceitarPoliticaPrivacidade") != null)
                    && "1".equals(session.getAttribute("AceitarPoliticaPrivacidade"))) {
                boolean direcionar = true;

                if (pgPermitidasAoAceitarPoliticaPrivacidade.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/visualizarPoliticaPrivacidade?acao=iniciar").forward(request, response);
                    return;
                }
            } else if ((session.getAttribute("AlterarSenha") != null) && "1".equals(session.getAttribute("AlterarSenha"))) {
                if (responsavel.isSer()) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("rotulo.ajuda.senhaExpirada.servidor", responsavel));
                } else {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("rotulo.ajuda.senhaExpirada.geral", responsavel));
                }
                boolean direcionar = true;

                if (pgPermitidasAoExpirar.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/alterarSenha").forward(request, response);
                    return;
                }

            } else if ((session.getAttribute("ExigeEmailOuTelefone") != null) && "1".equals(session.getAttribute("ExigeEmailOuTelefone"))) {
                boolean direcionar = true;

                if (pgPermitidasAlterarEmailOuTelefone.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/atualizarEmailTelefone").forward(request, response);
                    return;
                }

            } else if ((session.getAttribute("AutorizaDescontoParcialSer") != null) && "1".equals(session.getAttribute("AutorizaDescontoParcialSer"))) {
                boolean direcionar = true;

                if (pgPermitidasAutorizarDescontoParcialSer.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/autorizarDescontoParcialSer").forward(request, response);
                    return;
                }

            } else if ((session.getAttribute("ExigeReconhecimentoFacialPrimeiroAcesso") != null) && "1".equals(session.getAttribute("ExigeReconhecimentoFacialPrimeiroAcesso"))) {
                boolean direcionar = true;

                if (pgPermitidasReconhecimentoFacialPrimeiroAcesso.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/reconhecimentoFacial?acao=iniciar").forward(request, response);
                    return;
                }
            } else if (session.getAttribute("mensagem_sem_leitura") != null) {
                boolean direcionar = true;

                if (pgPermitidasAoConfirmarLeitura.contains(recurso)) {
                    direcionar = false;
                }
                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/confirmarMensagem?acao=iniciar").forward(request, response);
                    return;
                }

            } else if ((session.getAttribute("LeilaoFinalizadoSemContato") != null) && "1".equals(session.getAttribute("LeilaoFinalizadoSemContato"))) {
                boolean direcionar = true;

                if (pgPermitidasLeilaoFinalizadoSemContato.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/informarContatoLeilaoFinalizado?acao=iniciar").forward(request, response);
                    return;
                }
            } else if ((session.getAttribute("exigeAtualizacaoCadastral") != null) && "1".equals(session.getAttribute("exigeAtualizacaoCadastral"))) {
                boolean direcionar = true;

                if (pgPermitidasAtualizarCadasatro.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/atualizarCadastro?acao=iniciar").forward(request, response);
                    return;
                }
            } else if ((session.getAttribute("formularioObrigatorio") != null) && "1".equals(session.getAttribute("formularioObrigatorio"))){
                boolean direcionar = true;

                if (pgPermitidasAoFormularioPesquisa.contains(recurso)) {
                    direcionar = false;
                }

                if (direcionar) {
                    httpRequest.getRequestDispatcher("/v3/formularioResposta?acao=responder&" + SynchronizerToken.generateToken4URL((HttpServletRequest) request)).forward(request, response);
                    return;
                }
            } else {
            	try {
            		final List<String> termoAdesaoLerDepois = (List<String>) session.getAttribute("termoAdesaoLerDepois");
					final List<TermoAdesaoTO> listaTermoAdesao = termoAdesaoController.listTermoAdesaoSemLeitura(recursoAcessado.getFunCodigo(), termoAdesaoLerDepois, responsavel);
                    final List<TermoAdesaoTO> listaTermoAdesaoSemFunCodigoExibeServidor = termoAdesaoController.listTermoAdesaoSemFunCodigoExibeServidor();

                    if (!listaTermoAdesaoSemFunCodigoExibeServidor.isEmpty() && responsavel.isSer()) {
                        session.setAttribute("listaTermoAdesaoSemFunCodigoExibeServidor", listaTermoAdesaoSemFunCodigoExibeServidor);
                    }

					if (!listaTermoAdesao.isEmpty()) {
	                    boolean direcionar = true;
						if (pgPermitidasTermoAdesao.contains(recurso)) {
							direcionar = false;
						}

						if (direcionar) {
							httpRequest.getRequestDispatcher("/v3/informarTermoAdesao?acao=iniciar&funCodigo=" + (!TextHelper.isNull(recursoAcessado.getFunCodigo()) ? recursoAcessado.getFunCodigo() : "")).forward(request, response);
							return;
						}

					}
				} catch (final TermoAdesaoControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                addURIToHistory(request, paramSession);
            }
        }

        // Força a atualização dos atributos na sessão persistida
        session.setAttribute(ParamSession.SESSION_ATTR_NAME, paramSession);
        session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);

        chain.doFilter(request, response);
    }

    private void addURIToHistory(ServletRequest request, ParamSession paramSession) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String link = httpRequest.getRequestURI();

        final boolean skipHistory = (request.getParameter("_skip_history_") != null) || (request.getAttribute("_skip_history_") != null);

        // Se sessão é válida, adiciona link corrente à pilha de navegação
        final Map<String, String[]> parametros = new HashMap<>(request.getParameterMap());
        if (parametros.containsKey("back") && !skipHistory) {
            paramSession.back();
        }

        // Ignora o histórico no caso de composição e variação de margem (POP-UP)
        final boolean tipoPopUp = (request.getParameter("tipo") != null)
                && ("comp_margem".equals(request.getParameter("tipo"))
                ||  "variacao_margem".equals(request.getParameter("tipo"))
                );
        final boolean excecao = (link != null) && (link.endsWith("/img/qrcode.jsp")
                || link.endsWith("/v3/exibirMensagem")
                || link.endsWith("/v3/downloadArquivo")
                || link.endsWith("/arquivos/upload_anexo.jsp")
                || link.endsWith("/js/mensagens.jsp")
                || link.endsWith("/v3/favoritarMenu")
                || link.endsWith("/v3/visualizarRodape")
                || link.endsWith("/v3/listarCidades")
                );

        if (!parametros.containsKey("MM_update") && !skipHistory && !tipoPopUp && !excecao) {
            // Remove os parametros que são específicos do vrf_login
            parametros.remove("back");

            // Adiciona o histórico
            paramSession.addHistory(link, parametros);
        }
    }

    @Override
    public void destroy() {
    }

    private void keepSessionCentralizador(HttpServletRequest request) {
        if (request.getRequestURI().endsWith("verificarOperacao")) {
            return;
        }

        final String urlCentralizador = (String) request.getSession().getAttribute("urlCentralizador");
        if (!TextHelper.isNull(urlCentralizador)) {
            Date sessionDate = (Date) request.getSession().getAttribute("sessionDateCentralizdor");

            if (sessionDate == null) {
                sessionDate = new Date();
                request.getSession().setAttribute("sessionDateCentralizdor", sessionDate);
            } else {
                final Date dataAtual = new Date();
                final long millis = dataAtual.getTime() - sessionDate.getTime();
                final Date dataSubtraida = new Date(millis);

                final Calendar c = Calendar.getInstance();
                c.setTime(dataSubtraida);
                final int minutos = c.get(Calendar.MINUTE);
                if (minutos >= 5) {
                    request.setAttribute("executePingCentralizador", true);
                    request.getSession().setAttribute("sessionDateCentralizdor", new Date());
                }
            }
        }
    }
}
