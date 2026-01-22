package com.zetra.econsig.web.controller.consignacao;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ListaConsignacaoSerViewHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: DeferirConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso DeferirConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/deferirConsignacao" })
public class DeferirConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DeferirConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("deferirConsignacaoController")
    private DeferirConsignacaoController deferirConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    protected boolean ocultarColunaCheckBox(AcessoSistema responsavel) {
        // Desabilita a ocultacao do checkbox em ListarConsignacao_V4
        return false;
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        final boolean deferimentoPelaCsa = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() &&
                ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                 (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()))));

        if (responsavel.isCseSup() || deferimentoPelaCsa) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        if (!deferimentoPelaCsa) {
            carregarListaServico(request, session, model, responsavel);
        }

        // Habilita exibição de campo para filtro por data
        model.addAttribute("exibirFiltroDataInclusao", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String urlDestino = "../v3/deferirConsignacao?acao=deferirConsignacao";
        final String funCodigo = CodedValues.FUN_DEF_CONSIGNACAO;
        final String[] adeDefCodigos = request.getParameterValues("chkDeferir");
        final String[] adeIndefCodigos = request.getParameterValues("chkIndeferir");
        final String[] adeCodigos = (String[])ArrayUtils.addAll(adeDefCodigos, adeIndefCodigos);

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, adeCodigos, request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=deferirConsignacao" })
    public String deferirConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParametroControllerException, ServidorControllerException, ServletException, IOException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final boolean bloqueiaDeferimentoContratoSemPrioridade = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_DEFERIMENTO_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel);

        boolean podeIndeferir = false;
        if (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
            podeIndeferir = true;
        }
        String msg = "";
        String[] adeCodigosDef = null;
        String[] adeCodigosIndef = null;

        final boolean deferirTodos = CodedValues.TPC_SIM.equals(JspHelper.verificaVarQryStr(request, "APLICAR_TODOS_DEF"));
        final boolean indeferirTodos = CodedValues.TPC_SIM.equals(JspHelper.verificaVarQryStr(request, "APLICAR_TODOS_INDEF"));

        try {
            if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
                if ((request.getParameterValues("chkDeferir") == null) && (!podeIndeferir || (request.getParameterValues("chkIndeferir") == null)) && (request.getParameterValues("adesDeferir") == null)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                // Se for para deferir todos, utiliza a lista de contratos encontrados na busca realizada anteriormente
                if (deferirTodos) {
                    adeCodigosDef = request.getParameterValues("adesDeferir");
                } else {
                    adeCodigosDef = request.getParameterValues("chkDeferir");
                }
                // ordena a lista de contratos para deferir por data crescente
                if (bloqueiaDeferimentoContratoSemPrioridade && (adeCodigosDef != null) && (adeCodigosDef.length > 0)) {
                    adeCodigosDef = pesquisarConsignacaoController.ordenarContratosPorDataCrescente(Arrays.asList(adeCodigosDef), responsavel).toArray(new String[]{});
                }

                if (podeIndeferir) {
                    // Se for para indeferir todos, utiliza a lista de contratos encontrados na busca realizada anteriormente
                    if (indeferirTodos) {
                        adeCodigosIndef = request.getParameterValues("adesDeferir");
                    } else {
                        adeCodigosIndef = request.getParameterValues("chkIndeferir");
                    }
                    // ordena a lista de contratos para indeferir por data crescente
                    if (bloqueiaDeferimentoContratoSemPrioridade && (adeCodigosIndef != null) && (adeCodigosIndef.length > 0)) {
                        adeCodigosIndef = pesquisarConsignacaoController.ordenarContratosPorDataCrescente(Arrays.asList(adeCodigosIndef), responsavel).toArray(new String[]{});
                    }
                }
            } else {
                adeCodigosDef = new String[1];
                adeCodigosDef[0] = request.getParameter("ADE_CODIGO").toString();
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.deferir.consignacao.concluido.sucesso", responsavel));

            // Deferir consignações
            if ((adeCodigosDef != null) && (adeCodigosDef.length > 0)) {
                final TreeSet<String> adeCodigosDefSet = new TreeSet<>(Arrays.asList(adeCodigosDef));
                for (final String element : adeCodigosDefSet) {
                    try {
                        CustomTransferObject tmo = null;
                        if ((request.getParameter("TMO_CODIGO") != null) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_DEF_CONSIGNACAO, responsavel)) {
                            tmo = new CustomTransferObject();
                            tmo.setAttribute(Columns.ADE_CODIGO, element);
                            tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                            tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                        }
                        deferirConsignacaoController.deferir(element, tmo, responsavel);
                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            autorizacaoController.criaOcorrenciaADE(element, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                                    (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
                                    (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                        }
                    } catch (final AutorizacaoControllerException ex) {
                        msg += ex.getMessage() + "<BR>";
                        session.removeAttribute(CodedValues.MSG_INFO);
                    }
                }
            }
            // Indeferir consignações
            if ((adeCodigosIndef != null) && (adeCodigosIndef.length > 0)) {
                final TreeSet<String> adeCodigosIndefSet = new TreeSet<>(Arrays.asList(adeCodigosIndef));
                for (final String element : adeCodigosIndefSet) {
                    try {
                        CustomTransferObject tmo = null;
                        if ((request.getParameter("TMO_CODIGO") != null) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_INDF_CONSIGNACAO, responsavel)) {
                            tmo = new CustomTransferObject();
                            tmo.setAttribute(Columns.ADE_CODIGO, element);
                            tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                            tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                        }
                        deferirConsignacaoController.indeferir(element, tmo, responsavel);
                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            autorizacaoController.criaOcorrenciaADE(element, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                                    (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
                                    (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                        }
                    } catch (final AutorizacaoControllerException ex) {
                        msg += ex.getMessage() + "<BR>";
                        session.removeAttribute(CodedValues.MSG_INFO);
                    }
                }
            }
            session.setAttribute(CodedValues.MSG_ERRO, msg);

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            final ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (final AutorizacaoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarConsignacaoSer" })
    @ResponseBody
    public ResponseEntity<String> listarConsignacaoSer(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws JspException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return ResponseEntity.internalServerError().build();
        }
        try {
            final List<TransferObject> lstConsignacaoSer = pesquisarConsignacaoController.findConsignacaoSerByAdeCodigo(adeCodigo, responsavel);

            final List<ColunaListaConsignacao> lstColunas = definirColunasListaConsignacaoSer(request, responsavel);

            final List<TransferObject> lstConsignacaoSerFormatada = formatarValoresListaConsignacaoSer(lstConsignacaoSer, lstColunas, request, session, responsavel);

            return ResponseEntity.ok(ListaConsignacaoSerViewHelper.constroiView(lstConsignacaoSerFormatada, lstColunas, responsavel));

        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
        return ResponseEntity.internalServerError().build();
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        if (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.deferir.indeferir.consignacao.titulo", responsavel));
        } else {
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.deferir.consignacao.titulo", responsavel));
        }

        model.addAttribute("acaoFormulario", "../v3/deferirConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        final boolean deferimentoPelaCsa = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() &&
                ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                 (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()))));

        final boolean isCse = responsavel.isCse() && ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo())));

        final String rseCodigo = (String) request.getAttribute("RSE_CODIGO");
        final String adeNumero = (String) request.getAttribute("ADE_NUMERO");
        if (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {
            // Adiciona opção para deferir consignação
            String link = "../v3/deferirConsignacao?acao=" + (deferimentoPelaCsa ? "deferirConsignacao" : "efetivarAcao") + (!TextHelper.isNull(rseCodigo) ? "&RSE_CODIGO=" + rseCodigo : "") + (!TextHelper.isNull(adeNumero) ? "&ADE_NUMERO=" + adeNumero : "");
            String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.deferir.abreviado", responsavel);
            String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.deferir", responsavel);
            String msgAlternativa = "";
            String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.deferir.indeferir", responsavel);
            String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.deferimento.liquidacao.pendente", responsavel);

            acoes.add(new AcaoConsignacao("DEF_CONSIGNACAO", CodedValues.FUN_DEF_CONSIGNACAO, descricao, descricaoCompleta, "deferir_consignacao.gif", "btnDeferirConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkDeferir"));

            // Adiciona opção para indeferir consignação
            link = ""; // Não coloca ação para a segunda operação
            descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.indeferir.abreviado", responsavel);
            descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.indeferir", responsavel);
            msgAlternativa = "";
            msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.deferir.indeferir", responsavel);
            msgAdicionalConfirmacao = "";

            acoes.add(new AcaoConsignacao("INDF_CONSIGNACAO", CodedValues.FUN_INDF_CONSIGNACAO, descricao, descricaoCompleta, "indeferir_margem.gif", "btnIndeferirConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkIndeferir"));

        } else {
            // Adiciona opção para deferir consignação
            final String link = "../v3/deferirConsignacao?acao=" + (deferimentoPelaCsa ? "deferirConsignacao" : "efetivarAcao") + (!TextHelper.isNull(rseCodigo) ? "&RSE_CODIGO=" + rseCodigo : "") + (!TextHelper.isNull(adeNumero) ? "&ADE_NUMERO=" + adeNumero : "");
            final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.deferir.abreviado", responsavel);
            final String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.deferir", responsavel);
            final String msgAlternativa = "";
            final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.deferir", responsavel);
            final String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.deferimento.liquidacao.pendente", responsavel);

            acoes.add(new AcaoConsignacao("DEF_CONSIGNACAO", CodedValues.FUN_DEF_CONSIGNACAO, descricao, descricaoCompleta, "deferir_consignacao.gif", "btnDeferirConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkDeferir"));
        }

        if (!deferimentoPelaCsa) {
            // Adiciona o editar consignação
            final String link = "../v3/deferirConsignacao?acao=detalharConsignacao";
            final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
            final String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
            final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
            final String msgConfirmacao = "";

            acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));
        }

        if (isCse) {
            // Adiciona o listar consignações de um dado servidor
            final String link = "../v3/deferirConsignacao?acao=listarConsignacaoSer";
            final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.listar.consignacao.ser.abreviado", responsavel);
            final String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.listar.consignacao.ser", responsavel);
            final String msgAlternativa = "";
            final String msgConfirmacao = "";

            acoes.add(new AcaoConsignacao("LISTAR_CONSIGNACAO_SER", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnListarConsignacaoSer", msgAlternativa, msgConfirmacao, null, link, null,null));
        }

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "deferir");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        final boolean deferimentoPelaCsa = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() &&
                ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                        (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()))));

        if (!deferimentoPelaCsa) {
            final String csaCodigo = (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        }

        try {
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!"".equals(periodoIni) ) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!"".equals(periodoFim)) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final boolean resultadoMultiplosServidores = (request.getAttribute("resultadoMultiplosServidores") != null);

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        final boolean deferimentoPelaCsa = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() &&
                ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) ||
                        (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()))));

        final List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
            if (!deferimentoPelaCsa) {

                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, responsavel) && !responsavel.isCsaCor()) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, responsavel) && (responsavel.isCseSup() || responsavel.isCsaCor())) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_RENEGOCIADO, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_RENEGOCIADO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.renegociado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
                }

            } else {

                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CPF, responsavel) && responsavel.isCsaCor()) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CPF, ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                    colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }

    @Override
    protected String getQueryString(List<String> requestParams, HttpServletRequest request) {
        final StringBuilder linkListBuild = new StringBuilder();

        // Concatena os parâmetros de request
        if ((requestParams != null) && !requestParams.isEmpty()) {
            for (final String param: requestParams) {
                // Estes dois parâmetros não devem ser incluídos na query de pesquisa da listagem de in/deferimento
                if ("chkDeferir".equals(param) || "chkIndeferir".equals(param) || "senhaRSA".equals(param)) {
                    continue;
                }
                final String[] paramValues = request.getParameterValues(param);
                if ((paramValues != null) && (paramValues.length > 0)) {
                    for (final String paramValue : paramValues) {
                        if (!TextHelper.isNull(paramValue)) {
                            linkListBuild.append("&").append(param).append("=").append(TextHelper.forUriComponent(paramValue));
                        }
                    }
                }
            }
        }
        // Remove o primeiro "&"
        if (linkListBuild.length() > 0) {
            linkListBuild.deleteCharAt(0);
        }

        return linkListBuild.toString();
    }
}
