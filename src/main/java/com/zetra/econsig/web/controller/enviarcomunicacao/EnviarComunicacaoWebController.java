package com.zetra.econsig.web.controller.enviarcomunicacao;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.comunicacao.EditarAnexoComunicacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EnviarComunicacaoWebController</p>
 * <p>Description: Controlador Web para listar o caso de Enviar Comunicação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: isaac.abreu$
 * $Revision: 24924 $
 * $Date: 2018-07-10 14:28:30 -0300 (Ter, 10 Jul 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/enviarComunicacao" })
public class EnviarComunicacaoWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarComunicacaoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private EditarAnexoComunicacaoController editarAnexoComunicacaoController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.servidor.titulo", responsavel));

        final String listServidor = JspHelper.verificaVarQryStr(request, "listServidor");
        if (!TextHelper.isNull(listServidor)) {
            session.setAttribute("listServidor", listServidor);
        }

        return super.iniciar(request, response, session, model);
    }

    @Override
    @RequestMapping(params = { "acao=pesquisarServidor" })
    public String pesquisarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParseException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.servidor.titulo", responsavel));
        return super.pesquisarServidor(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!TextHelper.isNull(request.getParameter("pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.consultar.comunicacao.titulo", responsavel);
            final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
            final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
            final Object paramCsaPermitidosCmn = ParamSist.getInstance().getParam(CodedValues.TPC_SER_ENVIA_CMN_APENAS_CSA_COM_ADE, responsavel);
            final boolean cmnApenasPraCsaComAde = (paramCsaPermitidosCmn == null) ? true : ("S".equals(paramCsaPermitidosCmn));
            final boolean podeCriarComunicacao = responsavel.temPermissao(CodedValues.FUN_CRIAR_COMUNICACAO);

            final String csaCodigo = (!TextHelper.isNull(responsavel.getCsaCodigo()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
            String corCodigo = (!TextHelper.isNull(responsavel.getCorCodigo()) ? responsavel.getCorCodigo() : JspHelper.verificaVarQryStr(request, "COR_CODIGO"));
            String orgCodigo = (!TextHelper.isNull(responsavel.getOrgCodigo()) ? responsavel.getOrgCodigo() : JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            final String estCodigo = (!TextHelper.isNull(responsavel.getEstCodigo()) ? responsavel.getEstCodigo() : JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
            final String serCodigo = (!TextHelper.isNull(responsavel.getSerCodigo()) ? responsavel.getSerCodigo() : JspHelper.verificaVarQryStr(request, "SER_CODIGO"));

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
            }
            if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
            }

            final CustomTransferObject criteriosPesquisa = new CustomTransferObject();
            criteriosPesquisa.setAttribute("APENAS_CMN_PAI", Boolean.TRUE);
            criteriosPesquisa.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criteriosPesquisa.setAttribute(Columns.COR_CODIGO, corCodigo);
            criteriosPesquisa.setAttribute(Columns.EST_CODIGO, estCodigo);
            criteriosPesquisa.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criteriosPesquisa.setAttribute(Columns.SER_CODIGO, serCodigo);

            // Define valores padrão para filtro de data inicial e final, caso seja obrigatório o filtro
            final String filtroDataIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            final String filtroDataFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            final String ascCodigo = JspHelper.verificaVarQryStr(request, "ASC_CODIGO");
            final String cmnNumero = JspHelper.verificaVarQryStr(request, "CMN_NUMERO");
            final String pendencia = JspHelper.verificaVarQryStr(request, "pendencia");
            final String exibeSomenteCse = JspHelper.verificaVarQryStr(request, "exibeSomenteCse");
            final String filtroLeitura = JspHelper.verificaVarQryStr(request, "lida");
            final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            final String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
            final String filtroRelacaoAde = JspHelper.verificaVarQryStr(request, "existeAdeRelacionada");

            if (!TextHelper.isNull(filtroDataIni)) {
                criteriosPesquisa.setAttribute("periodoIni", filtroDataIni);
            }
            if (!TextHelper.isNull(filtroDataFim)) {
                criteriosPesquisa.setAttribute("periodoFim", filtroDataFim);
            }
            if (!TextHelper.isNull(rseMatricula)) {
                criteriosPesquisa.setAttribute(Columns.RSE_MATRICULA, rseMatricula);
            }
            if (!TextHelper.isNull(serCpf)) {
                criteriosPesquisa.setAttribute(Columns.SER_CPF, serCpf);
            }
            if (!TextHelper.isNull(cmnNumero)) {
                criteriosPesquisa.setAttribute(Columns.CMN_NUMERO, cmnNumero);
            }
            if (!TextHelper.isNull(pendencia)) {
                criteriosPesquisa.setAttribute(Columns.CMN_PENDENCIA, pendencia);
            }
            if (!TextHelper.isNull(exibeSomenteCse)) {
                criteriosPesquisa.setAttribute("exibeSomenteCse", exibeSomenteCse);
            }
            if (!TextHelper.isNull(filtroLeitura)) {
                criteriosPesquisa.setAttribute("CMN_LIDA", filtroLeitura);
            }
            if (!TextHelper.isNull(ascCodigo)) {
                criteriosPesquisa.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
            }
            if (!TextHelper.isNull(filtroRelacaoAde)) {
                criteriosPesquisa.setAttribute("CMN_RELACAO_ADE", filtroRelacaoAde);
            }

            final List<TransferObject> listConsignatarias = getListaConsignatarias(cmnApenasPraCsaComAde, null, session, responsavel);
            final List<TransferObject> listOrgaos = getListaOrgaos(session, responsavel);
            final List<TransferObject> assuntos = comunicacaoController.listaAssuntoComunicacao(responsavel);

            final int total = comunicacaoController.countComunicacoes(criteriosPesquisa, responsavel);
            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            final List<TransferObject> comunicacoes = comunicacaoController.listComunicacoes(criteriosPesquisa, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            final List<String> requestParams = new ArrayList<>(params);

            model.addAttribute("queryString", getQueryString(requestParams, request));

            // Monta link de paginação
            final String linkListagem = "../v3/enviarComunicacao?acao=listar";
            configurarPaginador(linkListagem, "rotulo.navegar.listagem.comunicacao", total, size, requestParams, false, request, model);

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("tituloPagina", tituloPagina);
            model.addAttribute("rotuloSim", rotuloSim);
            model.addAttribute("rotuloNao", rotuloNao);
            model.addAttribute("podeCriarComunicacao", podeCriarComunicacao);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("corCodigo", corCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("pendencia", pendencia);
            model.addAttribute("exibeSomenteCse", exibeSomenteCse);
            model.addAttribute("filtroLeitura", filtroLeitura);
            model.addAttribute("ascCodigo", ascCodigo);
            model.addAttribute("filtroDataFim", filtroDataFim);
            model.addAttribute("filtroDataIni", filtroDataIni);
            model.addAttribute("listConsignatarias", listConsignatarias);
            model.addAttribute("orgaosTO", listOrgaos);
            model.addAttribute("assuntos", assuntos);
            model.addAttribute("comunicacoes", comunicacoes);
            model.addAttribute("filtroRelacaoAde", filtroRelacaoAde);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/enviarComunicacao/listarComunicacao", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);
            //Valida o token de sessão para evitar a chamada direta à operação
            if ((!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "operacao")) || TextHelper.isNull(request.getParameter("cmn_codigo"))) && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.comunicacao.titulo", responsavel);
            String rseMatricula = "";
            String serCpf = "";
            CustomTransferObject servInfo = null;
            String msgErro = null;
            boolean sucesso = true;

            final boolean podeEditarCmn = responsavel.temPermissao(CodedValues.FUN_EDITAR_COMUNICACAO);
            final boolean podeConsultarAde = responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNACAO);
            final boolean podeIncluirAnexo = responsavel.temPermissao(CodedValues.FUN_INCLUIR_ANEXO_COMUNICACAO);

            final CustomTransferObject fitroEdicao = new CustomTransferObject();
            fitroEdicao.setAttribute(Columns.CMN_CODIGO, request.getParameter("cmn_codigo"));
            fitroEdicao.setAttribute("APENAS_CMN_PAI", Boolean.TRUE);
            final CustomTransferObject comunicacao = (CustomTransferObject) comunicacaoController.listComunicacoes(fitroEdicao, responsavel).get(0);
            final String cmnCodigo = (String) comunicacao.getAttribute(Columns.CMN_CODIGO);
            final String rseCodigo = (String) comunicacao.getAttribute(Columns.RSE_CODIGO);
            final String serCodigo = (String) comunicacao.getAttribute(Columns.SER_CODIGO);

            final Usuario usuResponsavel = comunicacaoController.findResponsavelCmn(cmnCodigo, responsavel);

            // busca informações do servidor para exibir na tela
            if (!TextHelper.isNull(rseCodigo)) {
                servInfo = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                rseMatricula = (String) servInfo.getAttribute(Columns.RSE_MATRICULA);
                serCpf = (String) servInfo.getAttribute(Columns.SER_CPF);

                try {
                    final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
                    // Quantidade mínima de dígitos da matrícula a ser informado
                    final int tamanhoMatricula = ((param != null) && !"".equals(param)) ? Integer.parseInt(param.toString()) : 0;

                    if (rseMatricula.length() < tamanhoMatricula) {
                        final int diff = tamanhoMatricula - rseMatricula.length();
                        final StringBuilder prefix = new StringBuilder();
                        for (int i = 0; i < diff; i++) {
                            prefix.append("0");
                        }

                        rseMatricula = prefix.append(rseMatricula).toString();
                    }
                } catch (final Exception ex) {
                }
            }

            final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_COMUNICACAO, responsavel);
            final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 500);

            request.setAttribute("servidor", servInfo);

            final UploadHelper uploadHelper = new UploadHelper();

            try {
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
            } catch (final Throwable ex) {
                LOG.error(ex.getMessage(), ex);
                final String msg = ex.getMessage();
                if (!TextHelper.isNull(msg)) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                }
            }

            final String mensagem = uploadHelper.getValorCampoFormulario("mensagem");
            final String operacao = JspHelper.verificaVarQryStr(request, "operacao");
            final String nomeArq = JspHelper.verificaVarQryStr(request, uploadHelper, "FILE1");

            //Salva o anexo
            if (!TextHelper.isNull(mensagem)) {

                //Inclusão de anexo
                if ("upload".equals(operacao) && podeIncluirAnexo) {

                    final String codigo = (!TextHelper.isNull(comunicacao.getAttribute(Columns.CMN_CODIGO_PAI)) ? comunicacao.getAttribute(Columns.CMN_CODIGO_PAI) : comunicacao.getAttribute(Columns.CMN_CODIGO)).toString();

                    final String path = "comunicacao" + File.separatorChar + DateHelper.format((Date) comunicacao.getAttribute(Columns.CMN_DATA), "yyyyMMdd") + File.separatorChar + codigo;

                    File anexo = null;
                    try {
                        anexo = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_COMUNICACAO, null, session);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        msgErro = ex.getMessage();
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        sucesso = false;

                    }
                    if ((anexo != null) && anexo.exists()) {
                        try {
                            editarAnexoComunicacaoController.createAnexoComunicacao(cmnCodigo, anexo.getName(), anexo.getName(), null, codigo, responsavel);
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.anexo.copiado.sucesso", responsavel));
                        } catch (final Exception ex) {
                            anexo.delete();
                            msgErro = ex.getMessage();
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            sucesso = false;
                        }
                    }
                } else if (!TextHelper.isNull(nomeArq)) {
                    final CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.ACM_NOME, nomeArq);
                    cto.setAttribute(Columns.ACM_CMN_CODIGO, cmnCodigo);

                    //operacao Excluir
                    if ("excluir".equals(operacao)) {
                        try {
                            editarAnexoComunicacaoController.removeAnexoComunicacao(cto, responsavel);
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.anexo.excluido.sucesso", responsavel));
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            sucesso = false;
                        }
                    }
                }
            }

            //Salva a mensagem
            if (!TextHelper.isNull(mensagem) && TextHelper.isNull(msgErro)) {
                final CustomTransferObject novaCmn = new CustomTransferObject();
                novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                novaCmn.setAttribute(Columns.CMN_CODIGO_PAI, cmnCodigo);
                novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);

                try {
                    comunicacaoController.geraComunicacaoResposta(novaCmn, cmnCodigo, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.criada.sucesso", responsavel));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            //busca lista de comunicações de resposta
            final CustomTransferObject cmnTO = new CustomTransferObject();
            cmnTO.setAttribute(Columns.CMN_CODIGO_PAI, cmnCodigo);
            cmnTO.setAttribute("APENAS_CMN_PAI", Boolean.FALSE);
            final List<TransferObject> respostas = comunicacaoController.listComunicacoes(cmnTO, false, -1, -1, responsavel);

            //Monta lista de parâmetros através dos parâmetros de request
            final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            //Ignora os parâmetros abaixo
            parameterMap.remove("offset");
            parameterMap.remove("back");
            parameterMap.remove("operacao");

            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.ACM_CMN_CODIGO, cmnCodigo);

            final int total = editarAnexoComunicacaoController.countAnexoComunicacao(cto, responsavel);
            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            final List<String> requestParams = new ArrayList<>(params);

            List<TransferObject> anexos = null;
            try {
                anexos = editarAnexoComunicacaoController.lstAnexoComunicacao(cto, offset, JspHelper.LIMITE, responsavel);

                final String linkListagem = "../v3/enviarComunicacao?acao=iniciar";
                configurarPaginador(linkListagem, "rotulo.navegar.listagem.comunicacao", total, size, requestParams, false, request, model);
            } catch (final ZetraException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            try {
                //gera um log de leitura da comunicação.
                comunicacaoController.logLeituraComunicacao(cmnCodigo, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("tituloPagina", tituloPagina);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("podeEditarCmn", podeEditarCmn);
            model.addAttribute("servInfo", servInfo);
            model.addAttribute("cmnCodigo", cmnCodigo);
            model.addAttribute("podeConsultarAde", podeConsultarAde);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serCpf", serCpf);
            model.addAttribute("usuResponsavel", usuResponsavel);
            model.addAttribute("comunicacao", comunicacao);
            model.addAttribute("respostas", respostas);
            model.addAttribute("anexos", anexos);
            model.addAttribute("podeIncluirAnexo", podeIncluirAnexo);
            model.addAttribute("uploadHelper", uploadHelper);
            model.addAttribute("sucesso", sucesso);
            model.addAttribute("paramSession", paramSession);
            model.addAttribute("offset", offset);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/enviarComunicacao/editarComunicacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=enviar" })
    public String enviar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
        List<TransferObject> naturezas;
        final String nseCodigo = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
        try {
            naturezas = servicoController.lstNaturezasServicos(false);
        } catch (final ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);
            final String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.criar.comunicacao.titulo", responsavel);
            int tamMaxMsg = 65000; // Como o texto é concatenado na tb_log (LOG_OBS), não pode usar 65535

            try {
                tamMaxMsg = !"0".equals(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_COMUNICACAO, responsavel).toString()) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_COMUNICACAO, responsavel).toString()) : tamMaxMsg;
            } catch (final Exception ex) {
            }

            final Object paramCsaPermitidosCmn = ParamSist.getInstance().getParam(CodedValues.TPC_SER_ENVIA_CMN_APENAS_CSA_COM_ADE, responsavel);
            final boolean cmnApenasPraCsaComAde = (paramCsaPermitidosCmn == null) ? true : ("S".equals(paramCsaPermitidosCmn));
            final boolean podeIncluirAnexo = responsavel.temPermissao(CodedValues.FUN_INCLUIR_ANEXO_COMUNICACAO);

            final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_COMUNICACAO, responsavel);
            final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 500);

            final UploadHelper uploadHelper = new UploadHelper();

            try {
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
            } catch (final Throwable ex) {
                LOG.error(ex.getMessage(), ex);
                final String msg = ex.getMessage();
                if (!TextHelper.isNull(msg)) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                }
            }

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String mensagem = JspHelper.verificaVarQryStr(request, uploadHelper, "mensagem");
            final String papCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "PAP_CODIGO");
            final String ascCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "ASC_CODIGO");

            final String cseCodigo = (!TextHelper.isNull(responsavel.getCseCodigo()) ? responsavel.getCseCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "CSE_CODIGO"));
            final String csaCodigo = (!TextHelper.isNull(responsavel.getCsaCodigo()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO"));
            String corCodigo = (!TextHelper.isNull(responsavel.getCorCodigo()) ? responsavel.getCorCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "COR_CODIGO"));
            String orgCodigo = (!TextHelper.isNull(responsavel.getOrgCodigo()) ? responsavel.getOrgCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "ORG_CODIGO"));
            String serCodigo = (!TextHelper.isNull(responsavel.getSerCodigo()) ? responsavel.getSerCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "SER_CODIGO"));
            final String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                orgCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "ORG_CODIGO");
            }
            if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "COR_CODIGO");
            }
            if (!responsavel.isSer() && TextHelper.isNull(rseCodigo)) {
                rseCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "RSE_CODIGO");
            }

            final String _enviarEmail = JspHelper.verificaVarQryStr(request, uploadHelper, "enviarEmail");
            final Short enviarEmail = TextHelper.isNull(_enviarEmail) ? Short.valueOf("0") : Short.valueOf(_enviarEmail);

            // Se usuário marcar que quer receber email de confirmação de resposta, recupera o email digitado para tal.
            String email = null;
            if (enviarEmail > 0) {
                email = JspHelper.verificaVarQryStr(request, uploadHelper, "email");
            }

            session.removeAttribute(CodedValues.MSG_INFO);

            final CustomTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            final List<TransferObject> listConsignatarias = getListaConsignatarias(cmnApenasPraCsaComAde, !TextHelper.isNull(adeCodigo) ? csaCodigo : null, session, responsavel);
            final List<TransferObject> listOrgaos = getListaOrgaos(session, responsavel);
            final List<TransferObject> assuntos = comunicacaoController.listaAssuntoComunicacao(responsavel);

            String serMail = "";
            if (responsavel.isSer()) {
                ServidorTransferObject serTO = null;
                if (TextHelper.isNull(email)) {
                    serTO = new ServidorTransferObject(serCodigo);
                    final ServidorTransferObject servidor = servidorController.findServidor(serTO, responsavel);
                    serMail = servidor.getSerEmail();
                }
            }

            String serNome = "";
            if (!TextHelper.isNull(rseCodigo)) {
                try {
                    final ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                    serCodigo = servidor.getSerCodigo();
                    serNome = servidor.getSerNome();

                    final List<String> servidores = new ArrayList<>();
                    if (!TextHelper.isNull(session.getAttribute("listServidor"))) {
                        final String listServidor = (String) session.getAttribute("listServidor");
                        session.removeAttribute("listServidor");

                        if ((listServidor != null) && !listServidor.trim().isEmpty()) {
                            final String[] servidoresArray = listServidor.split(";");
                            for (final String ser : servidoresArray) {
                                if (!ser.trim().isEmpty()) {
                                    servidores.add(ser);
                                }
                            }
                        }
                    }

                    servidores.add(serCodigo + "-" + rseCodigo + "-" + serNome);
                    model.addAttribute("servidores", servidores);
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            }

            if(responsavel.isCseSup()) {
                model.addAttribute("verbas", convenioController.getCsaCodVerba(csaCodigo, responsavel));
            }
            model.addAttribute("tituloPagina", tituloPagina);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("papCodigo", papCodigo);
            model.addAttribute("serNome", serNome);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("nseCodigo", nseCodigo);
            model.addAttribute("cseCodigo", cseCodigo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("corCodigo", corCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("ascCodigo", ascCodigo);
            model.addAttribute("podeIncluirAnexo", podeIncluirAnexo);
            model.addAttribute("uploadHelper", uploadHelper);
            model.addAttribute("paramSession", paramSession);
            model.addAttribute("cse", cse);
            model.addAttribute("consignatarias", listConsignatarias);
            model.addAttribute("orgaos", listOrgaos);
            model.addAttribute("email", email);
            model.addAttribute("enviarEmailValue", enviarEmail);
            model.addAttribute("assuntos", TextHelper.isNull(adeCodigo) ? assuntos : assuntos.stream().filter(a -> a.getAttribute(Columns.ASC_CONSIGNACAO).equals(Boolean.TRUE)).collect(Collectors.toList()));
            model.addAttribute("serMail", serMail);
            model.addAttribute("tamMaxMsg", tamMaxMsg);
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("naturezas", naturezas);
            model.addAttribute("adeCodigo", adeCodigo);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/enviarComunicacao/enviarComunicacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);
            final String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.criar.comunicacao.titulo", responsavel);

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            int tamMaxMsg = 65000; // Como o texto é concatenado na tb_log (LOG_OBS), não posso usar 65535
            try {
                tamMaxMsg = !"0".equals(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_COMUNICACAO, responsavel).toString()) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_COMUNICACAO, responsavel).toString()) : tamMaxMsg;
            } catch (final Exception ex) {
            }

            final Object paramCsaPermitidosCmn = ParamSist.getInstance().getParam(CodedValues.TPC_SER_ENVIA_CMN_APENAS_CSA_COM_ADE, responsavel);
            final boolean cmnApenasPraCsaComAde = (paramCsaPermitidosCmn == null) ? true : ("S".equals(paramCsaPermitidosCmn));
            final boolean podeIncluirAnexo = responsavel.temPermissao(CodedValues.FUN_INCLUIR_ANEXO_COMUNICACAO);

            final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_COMUNICACAO, responsavel);
            final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 500);

            final UploadHelper uploadHelper = new UploadHelper();

            try {
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
            } catch (final Throwable ex) {
                LOG.error(ex.getMessage(), ex);
                final String msg = ex.getMessage();
                if (!TextHelper.isNull(msg)) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                }
            }

            final String mensagem = (uploadHelper.getValorCampoFormulario("mensagem") != null ? uploadHelper.getValorCampoFormulario("mensagem") : JspHelper.verificaVarQryStr(request, "mensagem"));
            final String papCodigo = (uploadHelper.getValorCampoFormulario("PAP_CODIGO") != null ? uploadHelper.getValorCampoFormulario("PAP_CODIGO") : JspHelper.verificaVarQryStr(request, "PAP_CODIGO"));
            final String ascCodigo = (uploadHelper.getValorCampoFormulario("ASC_CODIGO") != null ? uploadHelper.getValorCampoFormulario("ASC_CODIGO") : JspHelper.verificaVarQryStr(request, "ASC_CODIGO"));
            final String fileName2Senha = (uploadHelper.getValorCampoFormulario("fileName2Senha") != null ? uploadHelper.getValorCampoFormulario("fileName2Senha") : JspHelper.verificaVarQryStr(request, "fileName2Senha"));

            final String cseCodigo = (!TextHelper.isNull(responsavel.getCseCodigo()) ? responsavel.getCseCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "CSE_CODIGO"));
            List<String> csaCodigos = (!TextHelper.isNull(responsavel.getCsaCodigo()) ? Arrays.asList(responsavel.getCsaCodigo()) : uploadHelper.getValoresCampoFormulario("CSA_CODIGO"));
            String nseCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "NSE_CODIGO");
            String corCodigo = (!TextHelper.isNull(responsavel.getCorCodigo()) ? responsavel.getCorCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "COR_CODIGO"));
            List<String> orgCodigos = (!TextHelper.isNull(responsavel.getOrgCodigo()) ? Arrays.asList(responsavel.getOrgCodigo()) : uploadHelper.getValoresCampoFormulario("ORG_CODIGO"));
            String serCodigo = (!TextHelper.isNull(responsavel.getSerCodigo()) ? responsavel.getSerCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "SER_CODIGO"));
            String rseCodigo = (!TextHelper.isNull(responsavel.getRseCodigo()) ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, uploadHelper, "RSE_CODIGO"));
            final String cnvCodVerba = JspHelper.verificaVarQryStr(request, uploadHelper, "cnvCodVerba");
            final String adeCodigo = (uploadHelper.getValorCampoFormulario("ADE_CODIGO") != null ? uploadHelper.getValorCampoFormulario("ADE_CODIGO") : JspHelper.verificaVarQryStr(request, "ADE_CODIGO"));

            final List<String> servidores = uploadHelper.getValoresCampoFormulario("SERVIDOR_LIST");

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                orgCodigos = uploadHelper.getValoresCampoFormulario("ORG_CODIGO");
            }
            if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                corCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "COR_CODIGO");
            }

            if(responsavel.isCseSup() && CodedValues.PAP_SERVIDOR.equals(papCodigo)) {
                csaCodigos = uploadHelper.getValoresCampoFormulario("csaCodigo");
            }

            final String _enviarEmail = JspHelper.verificaVarQryStr(request, uploadHelper, "enviarEmail");
            final Short enviarEmail = TextHelper.isNull(_enviarEmail) ? Short.valueOf("0") : Short.valueOf(_enviarEmail);

            // Se usuário marcar que quer receber email de confirmação de resposta, recupera o email digitado para tal.
            String email = null;
            if (enviarEmail > 0) {
                email = JspHelper.verificaVarQryStr(request, uploadHelper, "email");
            }

            final String _enviarCopiaEmail = JspHelper.verificaVarQryStr(request, uploadHelper, "enviarCopiaEmail");
            final Short enviarCopiaEmail = TextHelper.isNull(_enviarCopiaEmail) ? Short.valueOf("0") : Short.valueOf(_enviarCopiaEmail);

            session.removeAttribute(CodedValues.MSG_INFO);

            String cmnCodigo = null;
            List<String> cmnCodigos = new ArrayList<>();

            // Significa requisição de envio de dados de uma nova comunicação
            if (!TextHelper.isNull(mensagem)) {
                final List<TransferObject> novaCmns = new ArrayList<>();
                if ((csaCodigos != null) && !csaCodigos.isEmpty() && !TextHelper.isNull(csaCodigos.get(0))) {
                    // Se tem lista de CSAs, então não pode ter NSE_CODIGO
                    nseCodigo = null;

                    // Se foi selecionado a opção "todas", recupera as consignatárias aptas a receberem a comunicação
                    if (csaCodigos.contains("todas")) {
                        final List<TransferObject> csaCodigosTo = getListaConsignatarias(cmnApenasPraCsaComAde, null, session, responsavel);
                        if ((csaCodigosTo != null) && !csaCodigosTo.isEmpty()) {
                            csaCodigos = csaCodigosTo.stream().map(to -> (String) to.getAttribute(Columns.CSA_CODIGO)).collect(Collectors.toList());
                        }
                    }

                    for (final String csaCodigo: csaCodigos) {
                        if ((orgCodigos != null) && !orgCodigos.isEmpty() && !TextHelper.isNull(orgCodigos.get(0))) {

                            if (orgCodigos.contains("todos")) {
                                final List<TransferObject> orgCodigosTo = getListaOrgaos(session, responsavel);
                                if ((orgCodigosTo != null) && !orgCodigosTo.isEmpty()) {
                                    orgCodigos = orgCodigosTo.stream().map(to -> (String) to.getAttribute(Columns.ORG_CODIGO)).toList();
                                }
                            }
                            for (final String orgCodigo : orgCodigos) {
                                final CustomTransferObject novaCmn = new CustomTransferObject();
                                novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                                novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                                novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                                novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                                novaCmn.setAttribute(Columns.ORG_CODIGO, orgCodigo);
                                novaCmn.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                                novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                                novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                                novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                                novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                                novaCmn.setAttribute(Columns.SER_EMAIL, email);
                                novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                                novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);

                                if (!TextHelper.isNull(adeCodigo)) {
                                    novaCmn.setAttribute(Columns.CMN_ADE_CODIGO, adeCodigo);
                                }

                                if (responsavel.isCseSup() && !TextHelper.isNull(papCodigo) && CodedValues.PAP_SERVIDOR.equals(papCodigo)) {
                                    novaCmn.setAttribute(CodedValues.ENVIA_CSA_CONTRATOS_SERVIDOR, Boolean.TRUE);
                                    novaCmn.setAttribute(CodedValues.ENVIA_CSA_CNV_CONTRATOS_SERVIDOR, cnvCodVerba);
                                }

                                novaCmns.add(novaCmn);
                            }
                        } else if ((servidores != null) && !servidores.isEmpty()) {
                            for (final String servidor : servidores) {
                                final String[] serSplit = servidor.split("-");
                                serCodigo = serSplit[0];
                                rseCodigo = serSplit[1];

                                final CustomTransferObject novaCmn = new CustomTransferObject();
                                novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                                novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                                novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                                novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                                novaCmn.setAttribute(Columns.ORG_CODIGO, null);
                                novaCmn.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                                novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                                novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                                novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                                novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                                novaCmn.setAttribute(Columns.SER_EMAIL, email);
                                novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                                novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);
                                novaCmns.add(novaCmn);
                            }
                        } else {
                            final CustomTransferObject novaCmn = new CustomTransferObject();
                            novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                            novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                            novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                            novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                            novaCmn.setAttribute(Columns.ORG_CODIGO, (orgCodigos != null) && !orgCodigos.isEmpty() ? orgCodigos.get(0) : null);
                            novaCmn.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                            novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                            novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                            novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                            novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                            novaCmn.setAttribute(Columns.SER_EMAIL, email);
                            novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                            novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);

                            if (responsavel.isCseSup() && !TextHelper.isNull(papCodigo) && CodedValues.PAP_SERVIDOR.equals(papCodigo)) {
                                novaCmn.setAttribute(CodedValues.ENVIA_CSA_CONTRATOS_SERVIDOR, Boolean.TRUE);
                                novaCmn.setAttribute(CodedValues.ENVIA_CSA_CNV_CONTRATOS_SERVIDOR, cnvCodVerba);
                            }

                            novaCmns.add(novaCmn);
                        }
                    }
                } else if ((orgCodigos != null) && !orgCodigos.isEmpty() && !TextHelper.isNull(orgCodigos.get(0))) {

                    if (orgCodigos.contains("todos")) {
                        final List<TransferObject> orgCodigosTo = getListaOrgaos(session, responsavel);
                        if ((orgCodigosTo != null) && !orgCodigosTo.isEmpty()) {
                            orgCodigos = orgCodigosTo.stream().map(to -> (String) to.getAttribute(Columns.ORG_CODIGO)).toList();
                        }
                    }

                    for (final String orgCodigo : orgCodigos) {
                        final CustomTransferObject novaCmn = new CustomTransferObject();
                        novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                        novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                        novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                        novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                        novaCmn.setAttribute(Columns.ORG_CODIGO, orgCodigo);
                        novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                        novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                        novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                        if (!TextHelper.isNull(nseCodigo)) {
                            novaCmn.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                        }
                        novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                        novaCmn.setAttribute(Columns.SER_EMAIL, email);
                        novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                        novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);

                        novaCmns.add(novaCmn);
                    }
                } else if ((servidores != null) && !servidores.isEmpty()) {
                    for (final String servidor : servidores) {
                        final String[] serSplit = servidor.split("-");
                        serCodigo = serSplit[0];
                        rseCodigo = serSplit[1];

                        final CustomTransferObject novaCmn = new CustomTransferObject();
                        novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                        novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                        novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                        novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                        novaCmn.setAttribute(Columns.ORG_CODIGO, null);
                        novaCmn.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                        novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                        novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                        novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                        novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                        novaCmn.setAttribute(Columns.SER_EMAIL, email);
                        novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                        novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);
                        novaCmns.add(novaCmn);
                    }
                } else {
                    final CustomTransferObject novaCmn = new CustomTransferObject();
                    novaCmn.setAttribute(Columns.PAP_CODIGO, papCodigo);
                    novaCmn.setAttribute(Columns.CMN_TEXTO, mensagem);
                    novaCmn.setAttribute(Columns.CMN_ALERTA_EMAIL, enviarEmail);
                    novaCmn.setAttribute(Columns.CSE_CODIGO, cseCodigo);
                    novaCmn.setAttribute(Columns.ORG_CODIGO, (orgCodigos != null) && !orgCodigos.isEmpty() ? orgCodigos.get(0) : null);
                    novaCmn.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                    novaCmn.setAttribute(Columns.COR_CODIGO, corCodigo);
                    novaCmn.setAttribute(Columns.SER_CODIGO, serCodigo);
                    novaCmn.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                    novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                    novaCmn.setAttribute(Columns.SER_EMAIL, email);
                    novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
                    novaCmn.setAttribute(Columns.CMN_COPIA_EMAIL_SMS, enviarCopiaEmail);

                    novaCmns.add(novaCmn);
                }

                final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                final String subDiretorioTemporario = File.separator + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + responsavel.getUsuCodigo();
                final File diretorioTemporario = new File(diretorioRaizArquivos + File.separator + subDiretorioTemporario);

                File anexo = null;
                try {
                    // Cria comunicacao
                    if (podeIncluirAnexo && (((uploadHelper != null) && uploadHelper.hasArquivosCarregados()) || !"".equals(fileName2Senha))) {
                        if (uploadHelper.hasArquivosCarregados()) {
                            anexo = uploadHelper.salvarArquivo(subDiretorioTemporario, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_COMUNICACAO, null, session);
                        } else {
                            anexo = new File(diretorioTemporario.getCanonicalPath() + File.separatorChar + fileName2Senha);
                        }

                        for (final TransferObject cmn: novaCmns) {
                            if (TextHelper.isNull(nseCodigo)) {
                                cmnCodigo = comunicacaoController.createComunicacao(cmn, anexo, responsavel);
                                if(!TextHelper.isNull(cmnCodigo)) {
                                    cmnCodigos.add(cmnCodigo);
                                }
                            } else {
                                cmnCodigos = comunicacaoController.createComunicacaoNseCodigo(cmn, anexo, responsavel);
                            }
                        }

                        if (diretorioTemporario.exists()) {
                            FileHelper.deleteDir(diretorioTemporario.getCanonicalPath());
                        }

                        if (cmnCodigos.isEmpty() && !TextHelper.isNull(nseCodigo)) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.nse.vazio", responsavel));
                        } else if (cmnCodigos.isEmpty()) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.criada.nao.encontrou.servidor", responsavel));
                        } else if (!cmnCodigos.isEmpty()) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.criada.sucesso", responsavel));
                        }

                    } else {
                        for (final TransferObject cmn: novaCmns) {
                            if (TextHelper.isNull(nseCodigo)) {
                                cmnCodigo = comunicacaoController.createComunicacao(cmn, responsavel);
                                if(!TextHelper.isNull(cmnCodigo)) {
                                    cmnCodigos.add(cmnCodigo);
                                }
                            } else {
                                cmnCodigos = comunicacaoController.createComunicacaoNseCodigo(cmn, responsavel);
                            }
                        }
                        if (cmnCodigos.isEmpty() && !TextHelper.isNull(nseCodigo)) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.nse.vazio", responsavel));
                        } else if (cmnCodigos.isEmpty()) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.criada.nao.encontrou.servidor", responsavel));
                        } else if (!cmnCodigos.isEmpty()) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.comunicacao.criada.sucesso", responsavel));
                        }
                    }
                } catch (final Exception ex) {
                    if ((diretorioTemporario != null) && diretorioTemporario.exists()) {
                        FileHelper.deleteDir(diretorioTemporario.getCanonicalPath());
                    }
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }

            final CustomTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            List<TransferObject> consignatarias = null;
            if (cmnApenasPraCsaComAde && responsavel.isSer()) {
                consignatarias = consignatariaController.lstConsignatariaSerTemAde(serCodigo, responsavel);
            } else {
                consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
            }

            final List<TransferObject> assuntos = comunicacaoController.listaAssuntoComunicacao(responsavel);

            String serMail = "";

            if (responsavel.isSer()) {
                ServidorTransferObject serTO = null;
                if (TextHelper.isNull(email)) {
                    serTO = new ServidorTransferObject(serCodigo);
                    final ServidorTransferObject servidor = servidorController.findServidor(serTO, responsavel);
                    serMail = servidor.getSerEmail();
                }
            }

            model.addAttribute("tituloPagina", tituloPagina);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("cseCodigo", cseCodigo);
            model.addAttribute("papCodigo", papCodigo);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("ascCodigo", ascCodigo);
            model.addAttribute("podeIncluirAnexo", podeIncluirAnexo);
            model.addAttribute("uploadHelper", uploadHelper);
            model.addAttribute("paramSession", paramSession);
            model.addAttribute("cse", cse);
            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("email", email);
            model.addAttribute("assuntos", assuntos);
            model.addAttribute("serMail", serMail);
            model.addAttribute("tamMaxMsg", tamMaxMsg);
            model.addAttribute("mensagem", mensagem);

            // Volta um passo para que o redirecionamento seja feito para a listagem e não para a tela de enviar comunicação
            paramSession.halfBack();
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
        final ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();
        return enviar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        final HttpSession session = request.getSession();
        // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação
        final ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.back();
        return "enviar";
    }

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("acaoFormulario", "../v3/enviarComunicacao");
        model.addAttribute("omitirAdeNumero", true);
    }

    private List<TransferObject> getListaConsignatarias(boolean cmnApenasPraCsaComAde, String csaCodigo, HttpSession session, AcessoSistema responsavel) throws ConsignatariaControllerException, ConvenioControllerException, InstantiationException, IllegalAccessException {
        List<TransferObject> listConsignatarias = null;
        if (responsavel.isSer() && cmnApenasPraCsaComAde && TextHelper.isNull(csaCodigo)) {
            listConsignatarias = consignatariaController.lstConsignatariaSerTemAde(responsavel.getSerCodigo(), responsavel);
        } else if (responsavel.isOrg()) {
            listConsignatarias = convenioController.getCsaCnvAtivo(null, responsavel.getOrgCodigo(), responsavel);
        } else {
            TransferObject criterio = null;
            if (responsavel.isCsaCor() || (!TextHelper.isNull(csaCodigo) && responsavel.isSer())) {
                criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_CODIGO, TextHelper.isNull(csaCodigo) && !responsavel.isSer() ? responsavel.getCsaCodigo() : csaCodigo);
            }
            listConsignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
        }
        return listConsignatarias;
    }

    private List<TransferObject> getListaOrgaos(HttpSession session, AcessoSistema responsavel) throws ConsignanteControllerException, ConvenioControllerException, InstantiationException, IllegalAccessException {
        List<TransferObject> orgaosTO = null;
        CustomTransferObject criterio = null;
        if (responsavel.isCsaCor() || responsavel.isCseSup()) {
            if (responsavel.isCsaCor()) {
                final String cor_codigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
                final String csa_codigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade() : ((responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidadePai() : null);
                orgaosTO = convenioController.getOrgCnvAtivo(csa_codigo, cor_codigo, responsavel);
            } else {
                orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
            }
        } else if (responsavel.isOrg() || responsavel.isSer()) {
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
            orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
        }

        return orgaosTO;
    }
}
