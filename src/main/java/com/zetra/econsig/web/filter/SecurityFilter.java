package com.zetra.econsig.web.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SecurityFilter</p>
 * <p>Description: Filtro que verifica se usuário pode acessar um dado recurso do sistema.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class SecurityFilter extends EConsigFilter {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SecurityFilter.class);

    private static final String REQUEST_METHOD_GET = "GET";
    private static final String REQUEST_METHOD_POST = "POST";

    private static final List<String> semRestricaoDeveSetarSessao;

    static {
        // select distinct acr_recurso from tb_acesso_recurso where fun_codigo is null and itm_codigo is not null;
        semRestricaoDeveSetarSessao = new ArrayList<>();
        semRestricaoDeveSetarSessao.add("/v3/visualizarFaq");
        semRestricaoDeveSetarSessao.add("/v3/cadastrarValidacaoTotp");
        semRestricaoDeveSetarSessao.add("/v3/alterarSenha");
        semRestricaoDeveSetarSessao.add("/v3/sairSistema");
        semRestricaoDeveSetarSessao.add("/v3/expirarSistema");
        semRestricaoDeveSetarSessao.add("/v3/visualizarAjuda");
        semRestricaoDeveSetarSessao.add("/v3/carregarPrincipal");
        semRestricaoDeveSetarSessao.add("/v3/visualizarSobre");
        semRestricaoDeveSetarSessao.add("/v3/visualizarTermoUso");
    }

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private SegurancaController segurancaController;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (prosseguirSemExecutar(request)) {
            chain.doFilter(request, response);
            return;
        }

        boolean temPermissaoNoEnderecoAcesso = false;
        boolean temPermissaoIndependenteDoEndereco = false;

        // O cabeçalho abaixo resolve problema de acesso a cookies com frames no navegador IE
        // O projeto P3P é documentado pela W3C
        ((HttpServletResponse) response).addHeader("P3P","CP=\"NON CUR NOR OUR NAV\"");

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpSession session = httpRequest.getSession();

        final String recurso = getRecurso(httpRequest);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);
        AcessoRecurso recursoAcessado = (responsavel.getRecursoAcessado() != null) ? responsavel.getRecursoAcessado() : (AcessoRecurso) session.getAttribute("_recurso_acessado_");
        if (recursoAcessado == null) {
            recursoAcessado = AcessoRecursoHelper.identificarAcessoRecurso(recurso, httpRequest.getParameterMap(), responsavel);
        }
        final String funCodigo = (recursoAcessado != null ? recursoAcessado.getFunCodigo() : null);
        final String funDescricao = (recursoAcessado != null ? recursoAcessado.getFunDescricao() : null);

        try {
            if (recursoAcessado == null) {
                if (!httpRequest.getMethod().equalsIgnoreCase(REQUEST_METHOD_GET)) {
                    // Não grava log para caso de falha de obter o acesso recurso em uma requisição GET, que provavelmente é um refresh
                    gravaLogErro(ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel), recurso, responsavel);
                }
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
                httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                return;

            } else {
                // confere se o recurso foi requisitado via método HTTP definido para este.
                if (!recurso.equals("/v3/exibirMensagem")) {
                    final String requestMethod = httpRequest.getMethod();
                    if ((recursoAcessado.getAcrMetodoHttp().equals(CodedValues.METODO_POST) && !requestMethod.equalsIgnoreCase(REQUEST_METHOD_POST)) ||
                            (recursoAcessado.getAcrMetodoHttp().equals(CodedValues.METODO_GET) && !requestMethod.equalsIgnoreCase(REQUEST_METHOD_GET)) ||
                            (recursoAcessado.getAcrMetodoHttp().equals(CodedValues.METODO_GET_POST) && (!requestMethod.equalsIgnoreCase(REQUEST_METHOD_GET) && !requestMethod.equalsIgnoreCase(REQUEST_METHOD_POST)))) {
                        gravaLogErro(ApplicationResourcesHelper.getMessage("mesangem.erro.acesso.http.invalido.arg0", responsavel, requestMethod), recurso, responsavel);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                        return;
                    }
                }

                if (funCodigo == null) {
                    temPermissaoNoEnderecoAcesso = true;
                    if (semRestricaoDeveSetarSessao.contains(recurso)) {
                        atualizarAcessoSistema(funCodigo, recursoAcessado, session, responsavel);
                    }

                    // confere se o recurso está ativo. não basta apenas não ter restrição alguma
                    if (!recursoAcessado.isAcrAtivo()) {
                        gravaLogErro(ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel), recurso, responsavel);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
                        httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                        return;
                    }

                    // Verifica se exige autorização de execução se o sistema assim estiver configurado

                    if (recursoAcessado.isAcrFimFluxo() && recursoAcessado.exigeSenhaOpeSensiveis(responsavel)) {
                        final String tipoSenha = recursoAcessado.funcaoExigeSegundaSenha(responsavel) ? CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM : CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA;
                        final boolean opEmFilaAutorizada = ((session.getAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA) != null) && ((Boolean) session.getAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA)));

                        if (verificaExigenciaSegundaSenha(request, response, session, funCodigo, recurso, recursoAcessado, tipoSenha)) {
                            return;
                        } else if (recursoAcessado.adicionarFuncaoFilaAutorizacao(responsavel))  {
                            if (!opEmFilaAutorizada) {
                                try {
                                    gravarPaginaOperacaoSensivel(request, recursoAcessado);
                                } catch (final ZetraException ex) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                    ((HttpServletResponse) response).sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal");
                                    return;
                                }
                                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.inserir.operacao.sensivel.fila.confirmacao", responsavel));
                                ((HttpServletResponse) response).sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal");
                                return;
                            }
                        }
                    }


                } else if (responsavel.temPermissao(funCodigo, true) && recursoAcessado.isAcrAtivo()) {
                    temPermissaoNoEnderecoAcesso = true;
                    atualizarAcessoSistema(funCodigo, recursoAcessado, session, responsavel);

                    if (recursoAcessado.getFunCodigo().equals(CodedValues.FUN_CONFIRMAR_OP_FILA_AUTORIZACAO) &&
                        (recursoAcessado.adicionarFuncaoFilaAutorizacao(responsavel) || recursoAcessado.funcaoExigeSegundaSenha(responsavel))) {
                        LOG.error("A configuração de autorização de operação sensível '" + CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA + "' ou '" + CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM + "' são inválidos para a função '469'");
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        ((HttpServletResponse) response).sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal");
                        return;
                    }
                    // Verifica se deve solicitar segunda senha por motivo de segurança para funções que podem liberar margem
                    boolean solicitarSegundaSenhaSeguranca = false;
                    if (recursoAcessado.isAcrFimFluxo() && !recursoAcessado.adicionarFuncaoFilaAutorizacao(responsavel)) {
                        solicitarSegundaSenhaSeguranca = segurancaController.exigirSegundaSenhaOperacoesLiberacaoMargem(responsavel);
                    }
                    // Verifica se o acesso recurso é fim de um fluxo, e caso exija segunda senha ou a própria senha, redireciona para página de autenticação
                    if (responsavel.isCseSupOrg() || responsavel.isCsaCor()) {
                        if (recursoAcessado.isAcrFimFluxo() && (solicitarSegundaSenhaSeguranca || recursoAcessado.exigeSenhaOpeSensiveis(responsavel))) {
                            String tipoSenha = "";
                            if (solicitarSegundaSenhaSeguranca) {
                                tipoSenha = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM;
                            } else {
                                tipoSenha = recursoAcessado.funcaoExigeSegundaSenha(responsavel) ? CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM : CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA;
                            }
                            if (verificaExigenciaSegundaSenha(request, response, session, funCodigo, recurso, recursoAcessado, tipoSenha)) {
                                return;
                            } else if (recursoAcessado.adicionarFuncaoFilaAutorizacao(responsavel))  {
                                Boolean opEmFilaAutorizada = (session.getAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA) != null && ((Boolean) session.getAttribute(AcessoSistema.OPERACAO_FILA_AUTORIZADA)));

                                if (!opEmFilaAutorizada) {
                                    try {
                                        gravarPaginaOperacaoSensivel(request, recursoAcessado);
                                    } catch (final ZetraException ex) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                        ((HttpServletResponse) response).sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal");
                                        return;
                                    }
                                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.inserir.operacao.sensivel.fila.confirmacao", responsavel));
                                    ((HttpServletResponse) response).sendRedirect("../v3/exibirMensagem?acao=exibirMsgSessao&tipo=principal");
                                    return;
                                }
                            }
                        }
                    }

                    // Valida se a natureza de consignatária tem acesso liberado à função restrita
                    if (responsavel.isCsaCor()) {
                        if (recursoAcessado.isFunRestritaNca()) {
                            if (!recursoAcessado.permiteAcessoNca(responsavel.getNcaCodigo())) {
                                temPermissaoNoEnderecoAcesso = false;
                            }
                        }
                    }
                } else {
                    temPermissaoNoEnderecoAcesso = false;
                    if (responsavel.temPermissao(funCodigo, false) && recursoAcessado.isAcrAtivo()) {
                        temPermissaoIndependenteDoEndereco = true;
                    }
                }


                if (!temPermissaoNoEnderecoAcesso) {
                    final StringBuilder mensagemErro = new StringBuilder();

                    if (temPermissaoIndependenteDoEndereco) {
                        mensagemErro.append(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.possui.restricao.endereco.funcao", responsavel, responsavel.getIpUsuario()));
                    } else {
                        mensagemErro.append(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.possui.permissao", responsavel));
                    }

                    mensagemErro.append(" ").append(funDescricao).append(".");

                    gravaLogErro(mensagemErro.toString(), recurso, responsavel);
                    session.setAttribute(CodedValues.MSG_ERRO, mensagemErro.toString());
                    httpRequest.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        }
    }

    private void atualizarAcessoSistema(String funCodigo, AcessoRecurso recursoAcessado, HttpSession session, AcessoSistema responsavel) throws ParametroControllerException {
        responsavel.setFunCodigo(funCodigo);
        if (recursoAcessado.getItmCodigo() != null || funCodigo == null) {
            responsavel.setItmCodigo(recursoAcessado.getItmCodigo());
        }
        responsavel.setSessionId(session.getId());
        responsavel.setDataUltimaRequisicao(DateHelper.getSystemDatetime());
        session.setAttribute("acesso_recurso", recursoAcessado);
        parametroController.saveAcessoUsuario(recursoAcessado.getAcrCodigo(), responsavel);
    }

    /**
     * Grava um snapshot de página da operação que exige autorização de operação sesnível para aprovação posterior
     * @param request
     * @param acessoRecurso
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ServletException
     * @throws ZetraException
     */
    private void gravarPaginaOperacaoSensivel(ServletRequest request, AcessoRecurso acessoRecurso) throws IOException, FileNotFoundException,
    InstantiationException, IllegalAccessException, ServletException, ZetraException {

        int tamMaxTotal = 0;
        for (final String paramSistTam: CodedValues.TPC_CODIGOS_TAM_MAX_ARQUIVOS) {
            final Object paramSistVal = ParamSist.getInstance().getParam(paramSistTam, JspHelper.getAcessoSistema((HttpServletRequest) request));
            int tamMax = (!TextHelper.isNull(paramSistVal)) ? Integer.parseInt(paramSistVal.toString()) : 0;
            tamMax *= (!TextHelper.isNull(paramSistVal) && (paramSistVal.equals(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG) ||
                       paramSistVal.equals(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR) ||
                       paramSistVal.equals(CodedValues.TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO) ||
                       paramSistVal.equals(CodedValues.TPC_TAMANHO_ANEXO_BENEFICIARIO))) ? 1024 * 1024 : 1024;
            tamMaxTotal = (tamMax > tamMaxTotal) ? tamMax : tamMaxTotal;
        }

        final UploadHelper uploadHelper = new UploadHelper();

        uploadHelper.processarRequisicao(request.getServletContext(), ((HttpServletRequest) request), tamMaxTotal);

        final String snap = !TextHelper.isNull(request.getParameter("htmlSnapshot")) ? request.getParameter("htmlSnapshot") : uploadHelper.getValorCampoFormulario("htmlSnapshot");

        final Map<String, Map<String, String[]>> paramsRequest = new HashMap<>();
        final Map<String, String[]> paramMap = request.getParameterMap();
        final Map<String, String[]> mutableMap = new HashMap<>();
        mutableMap.putAll(paramMap);
        mutableMap.remove("htmlSnapshot");
        mutableMap.remove(SynchronizerToken.TRANSACTION_TOKEN_KEY);

        if (mutableMap.containsKey(JspHelper.CAPTCHA_FIELD)) {
            final String[] captchaValue = mutableMap.get(JspHelper.CAPTCHA_FIELD);
            final String captchaKey = (String) ((HttpServletRequest) request).getSession().getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
            if (captchaValue[0].equals(captchaKey)) {
                final Map<String, String[]> captchaParamMap = new HashMap<>();
                final String [] arrayCaptcha = {captchaValue[0], captchaKey};
                captchaParamMap.put(JspHelper.CAPTCHA_FIELD, arrayCaptcha);
                paramsRequest.put(JspHelper.CAPTCHA_FIELD, captchaParamMap);
            } else {
                throw new ZetraException("mensagem.erro.captcha.invalido", JspHelper.getAcessoSistema((HttpServletRequest) request));
            }
        }

        final Map<String, List<String>> paramsComunsUploadHelper = uploadHelper.getValoresCampos();
        if (paramsComunsUploadHelper != null) {
            paramsComunsUploadHelper.forEach((innerKey, value) -> {if (!innerKey.equals("htmlSnapshot")) { mutableMap.put(innerKey, value.toArray(new String [0])); }});
        }

        paramsRequest.put(HttpHelper.REQUEST_QUERY_STRING_JSON, mutableMap);

        if (uploadHelper.hasArquivosCarregados()) {
            final Map<String, List<String>> camposMultiPart = uploadHelper.getValoresCampos();

            camposMultiPart.remove("htmlSnapshot");
            camposMultiPart.remove(SynchronizerToken.TRANSACTION_TOKEN_KEY);

            if (camposMultiPart != null) {
                final Map<String, String[]> multiPartParamArray = new HashMap<>();

                for (final String key: camposMultiPart.keySet()) {
                    final List<String> campoMultiValor = camposMultiPart.get(key);

                    if (key.toLowerCase().equals(JspHelper.CAPTCHA_FIELD)) {
                        final String captchaKey = (String) ((HttpServletRequest) request).getSession().getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                        if (campoMultiValor.get(0).equals(captchaKey)) {
                            final String [] converted = {captchaKey, captchaKey};
                            multiPartParamArray.put(key, converted);
                        } else {
                            throw new ZetraException("mensagem.erro.captcha.invalido", JspHelper.getAcessoSistema((HttpServletRequest) request));
                        }

                    } else {
                        final String [] converted = new String[campoMultiValor.size()];
                        multiPartParamArray.put(key, campoMultiValor.toArray(converted));
                    }
                }
                paramsRequest.put(HttpHelper.MULTI_PARTPARAM_REQUEST_PARAMS, multiPartParamArray);
            }
            final List<String> fileNames = uploadHelper.getNomeCamposArquivos();
            final String [] fileNameArray = new String[fileNames.size()];
            final Map<String, String[]> fileNameParamMap = new HashMap<>();
            fileNameParamMap.put(HttpHelper.ARQUIVO_REQUEST_PARAM_NAME, uploadHelper.getNomeCamposArquivos().toArray(fileNameArray));
            paramsRequest.put(HttpHelper.ARQUIVO_REQUEST_PARAM_NAME, fileNameParamMap);
        }

        sistemaController.gravarPaginaOperacaoSensivel(snap, JspHelper.getRemoteAddr((HttpServletRequest) request), paramsRequest, uploadHelper, ((HttpServletRequest) request).getSession().getId(), acessoRecurso, JspHelper.getAcessoSistema((HttpServletRequest) request));

    }

    private boolean verificaExigenciaSegundaSenha(ServletRequest request, ServletResponse response, HttpSession session, String funcao, String recurso, AcessoRecurso acessoRecurso, String tipoSenha) throws ServletException, IOException {
        final Boolean autenticado2aSenha = (Boolean) session.getAttribute(AcessoSistema.SEGUNDA_SENHA_AUTENTICADA);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);

        if (!acessoRecurso.adicionarFuncaoFilaAutorizacao(responsavel) && ((autenticado2aSenha == null) || !autenticado2aSenha.booleanValue())) {
            redirecionaPgSegundaSenha(request, response, session, funcao, acessoRecurso, tipoSenha);
            return true;
        }  else {
            session.removeAttribute(AcessoSistema.SEGUNDA_SENHA_AUTENTICADA);
            session.removeAttribute(AcessoSistema.EXIGE_SENHA);
            session.removeAttribute(AcessoSistema.ACESSO_RECURSO);
            session.setAttribute(AcessoSistema.ULTIMA_FUNCAO_AUTENTICADA, funcao);
            session.setAttribute(AcessoSistema.ULTIMO_RECURSO_AUTENTICADO, recurso);
        }

        return false;
    }

    private void redirecionaPgSegundaSenha(ServletRequest request, ServletResponse response, HttpSession session, String funcao, AcessoRecurso acessoRecurso, String tipoSenha) throws ServletException, IOException {
        // Não existe mais página de v2, então redireciona para página de erro
        final AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        LOG.warn("Recurso '" + acessoRecurso.getAcrCodigo() + "' sendo redirecionado para segunda senha v2. Tipo senha: '" + tipoSenha + "'");
        LOG.warn("Historico de navegacao: \n\n" + paramSession.toString());

        if (!TextHelper.isNull(tipoSenha) && tipoSenha.equals(CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.operacao.sensivel.requer.autorizacao.outra", responsavel));
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.operacao.sensivel.requer.autorizacao.propria", responsavel));
        }

        request.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
    }

    private void gravaLogErro(String mensagem, String recurso, AcessoSistema responsavel) {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.GERAL, null, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", responsavel, mensagem));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.recurso.arg0", responsavel, recurso));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void destroy() {
    }
}