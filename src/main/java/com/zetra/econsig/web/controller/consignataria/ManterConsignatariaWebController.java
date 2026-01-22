package com.zetra.econsig.web.controller.consignataria;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OcorrenciaConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.dto.web.DadosConsignataria;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.persistence.entity.LimiteMargemCsaOrg;
import com.zetra.econsig.persistence.entity.ParamPostoCsaSvc;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.PenalidadeController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusConsignatariaEnum;
import com.zetra.econsig.values.StatusCredenciamentoEnum;
import com.zetra.econsig.values.TipoMotivoBloqueioEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutenção de Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/manterConsignataria"})
public class ManterConsignatariaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterConsignatariaWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PenalidadeController penalidadeController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = {"acao=iniciar"})
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, UnsupportedEncodingException {

        SynchronizerToken.saveToken(request);

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        /* consignataria */
        final boolean podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS);
        final boolean podeExcluirCsa = responsavel.temPermissao(CodedValues.FUN_EXCL_CONSIGNATARIA);
        final boolean podeConsultarCsa = responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNATARIAS);
        final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSA);
        final boolean podeEditarEnderecosCsa = responsavel.temPermissao(CodedValues.FUN_EDITAR_ENDERECOS_CONSIGNATARIA);
        /* usuario */
        final boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSA);
        final boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA);
        final boolean podeEditarParamCsa = responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_CONSIGNATARIA);
        /* perfil usuario */
        final boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_CSA);
        /* correspondente */
        final boolean podeConsultarCor = responsavel.temPermissao(CodedValues.FUN_CONS_CORRESPONDENTES);
        final boolean podeEditarCor = responsavel.temPermissao(CodedValues.FUN_EDT_CORRESPONDENTES);
        /* servico*/
        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);
        final boolean podeConsultarSvc = responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS);

        boolean temPenalidade = responsavel.temPermissao(CodedValues.FUN_INC_PENALIDADE);
        if (temPenalidade) {
            final List<TransferObject> tiposPenalidade = penalidadeController.lstTiposPenalidade(responsavel);
            temPenalidade = tiposPenalidade.iterator().hasNext();
        }

        List<TransferObject> consignatarias = null;

        int filtroTipo = -1;
        try {
            filtroTipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (final Exception ex1) {
        }

        String filtro = filtroTipo == 4 ? request.getParameter("NCA_CODIGO") : JspHelper.verificaVarQryStr(request, "FILTRO");

        if (!TextHelper.isNull(filtro)) {
            try {
                filtro = java.net.URLDecoder.decode(filtro, "UTF-8");
            } catch (final UnsupportedEncodingException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        final String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");

        try {
            final CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtroTipo == 0) {
                final List<Short> statusCsa = new ArrayList<>();
                statusCsa.add(CodedValues.STS_INATIVO);
                statusCsa.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.CSA_ATIVO, statusCsa);
                // Desbloqueado
            } else if (filtroTipo == 1) {
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!"".equals(filtro) && (filtroTipo != -1)) {
                String campo = null;
                String filtro_formatado = null;

                switch (filtroTipo) {
                    case 2:
                        campo = Columns.CSA_IDENTIFICADOR;
                        filtro_formatado = CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO;
                        break;
                    case 3:
                        campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                        filtro_formatado = CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO;
                        break;
                    case 4:
                        // DESENV-13750 - Filtro Natureza Consignatária
                        campo = Columns.CSA_NCA_NATUREZA;
                        filtro_formatado = filtro;
                        model.addAttribute("ncaCodigoSelecionado", filtro);
                        break;
                    case 5:
                        //DESENV-15889 - Inclusão de filtro para identificar verba ao acesso de CSE, ORG e SUP
                        campo = Columns.CNV_COD_VERBA;
                        filtro_formatado = CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, filtro_formatado);
            }

            if (!"".equals(filtro2)) {
                criterio.setAttribute(Columns.CSA_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            final int total = consignatariaController.countConsignatarias(criterio, responsavel);
            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            consignatarias = consignatariaController.lstConsignatarias(criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");
            params.remove("FILTRO");

            final List<String> requestParams = new ArrayList<>(params);

            final StringBuilder linkListarConsignatarias = new StringBuilder().append(request.getRequestURI()).append("?acao=iniciar");

            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO"))) {
                final String filtroDecode = java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "FILTRO"), "UTF-8");
                linkListarConsignatarias.append("&FILTRO=").append(java.net.URLEncoder.encode(filtroDecode, "UTF-8"));
            }

            model.addAttribute("queryString", getQueryString(requestParams, request));

            configurarPaginador(linkListarConsignatarias.toString(), "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            consignatarias = new ArrayList<>();
        }

        final List<DadosConsignataria> consignatariaDTOs = converterListaDtoConsignataria(consignatarias, podeEditarCsa, true, responsavel);
        final List<TransferObject> lstNatureza = consignatariaController.lstNatureza();

        final boolean cadastraEmpCor = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, CodedValues.TPC_SIM, responsavel);
        final StringBuilder linkImpressao = new StringBuilder("../v3/manterConsignataria?acao=listarTodasConsignatarias");

        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO"))) {
            final String filtroDecode = java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "FILTRO"), "UTF-8");
            linkImpressao.append("&FILTRO=").append(java.net.URLEncoder.encode(filtroDecode, "UTF-8"));
        }

        linkImpressao.append("&FILTRO_TIPO=").append(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")).append("&FILTRO2=").append(JspHelper.verificaVarQryStr(request, "FILTRO2")).append("&NCA_CODIGO=").append(filtro).append("&").append(SynchronizerToken.generateToken4URL(request));

        // Exibe Botao que leva ao rodapé
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("cadastraEmpCor", cadastraEmpCor);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro2", filtro2);
        model.addAttribute("podeEditarCsa", podeEditarCsa);
        model.addAttribute("filtro_tipo", filtroTipo);
        model.addAttribute("podeExcluirCsa", podeExcluirCsa);
        model.addAttribute("podeConsultarCsa", podeConsultarCsa);
        model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
        model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
        model.addAttribute("podeConsultarUsu", podeConsultarUsu);
        model.addAttribute("podeCriarUsu", podeCriarUsu);
        model.addAttribute("podeConsultarCor", podeConsultarCor);
        model.addAttribute("podeEditarCor", podeEditarCor);
        model.addAttribute("podeEditarSvc", podeEditarSvc);
        model.addAttribute("podeConsultarSvc", podeConsultarSvc);
        model.addAttribute("temPenalidade", temPenalidade);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("linkImpressao", linkImpressao.toString());
        model.addAttribute("podeEditarParamCsa", podeEditarParamCsa);
        model.addAttribute("podeEditarEnderecosCsa", podeEditarEnderecosCsa);

        model.addAttribute("consignatariaDTOs", consignatariaDTOs);
        model.addAttribute("lstNatureza", lstNatureza);

        return viewRedirect("jsp/manterConsignataria/listarConsignatarias", request, session, model, responsavel);
    }

    public static List<DadosConsignataria> converterListaDtoConsignataria(List<TransferObject> consignatarias, boolean podeEditarCsa, boolean incluirMotivoBloq, AcessoSistema responsavel) {
        final List<DadosConsignataria> consignatariaDTOs = new ArrayList<>();

        if (consignatarias != null && !consignatarias.isEmpty()) {
            final ConsignatariaController controller = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);

            for (TransferObject consignataria : consignatarias) {
                final String csaCodigo = (String) consignataria.getAttribute(Columns.CSA_CODIGO);
                final String csaNome = (String) consignataria.getAttribute(Columns.CSA_NOME);
                final String csaNomeAbrev = consignataria.getAttribute(Columns.CSA_NOME_ABREV) != null ? consignataria.getAttribute(Columns.CSA_NOME_ABREV).toString() : "";
                final String csaIdentificador = (String) consignataria.getAttribute(Columns.CSA_IDENTIFICADOR);
                final String csaCnpj = consignataria.getAttribute(Columns.CSA_CNPJ) != null ? consignataria.getAttribute(Columns.CSA_CNPJ).toString() : "";
                final String csaAtivo = consignataria.getAttribute(Columns.CSA_ATIVO) != null ? consignataria.getAttribute(Columns.CSA_ATIVO).toString() : StatusConsignatariaEnum.ATIVO.getCodigo();
                final boolean isBloqueada = StatusConsignatariaEnum.recuperaStatusConsignataria(csaAtivo).isBloqueado();
                String descStatus = csaAtivo.equals(StatusConsignatariaEnum.ATIVO.getCodigo()) ?
                        ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.desbloqueado", responsavel) :
                        ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.bloqueado", responsavel);

                if (isBloqueada && incluirMotivoBloq) {
                    final Date csaDataDesbloqAutomatico = (Date) consignataria.getAttribute(Columns.CSA_DATA_DESBLOQ_AUTOMATICO);
                    if (csaDataDesbloqAutomatico != null) {
                        descStatus = ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.bloqueado.ate.data.arg0", responsavel, DateHelper.format(csaDataDesbloqAutomatico, LocaleHelper.getDatePattern()));
                    }

                    // Se está bloqueada, veja se tem motivo de bloqueio gravado
                    final String tmbDescricao = (String) consignataria.getAttribute(Columns.TMB_DESCRICAO);
                    if (!TextHelper.isNull(tmbDescricao)) {
                        descStatus += " (" + tmbDescricao + ")";
                    }
                }

                String csaNomeAbrevScript = TextHelper.isNull(csaNomeAbrev) ? csaNome : csaNomeAbrev;
                csaNomeAbrevScript = csaNomeAbrevScript.replace("\'", "\\'").replace("\"", "");

                String mensagemDesbloqueio = null;
                if (podeEditarCsa && isBloqueada) {
                    mensagemDesbloqueio = controller.recuperarMensagemDesbloqueioConsignataria(csaCodigo);
                }

                consignatariaDTOs.add(new DadosConsignataria(csaCodigo, csaNome, csaNomeAbrev, csaIdentificador, csaCnpj, csaAtivo, descStatus, csaNomeAbrevScript, mensagemDesbloqueio));
            }
        }

        return consignatariaDTOs;
    }

    @RequestMapping(params = {"acao=listarTodasConsignatarias"})
    public String listarTodasConsignatarias(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> consignatarias = null;

        final String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (final Exception ex1) {
        }

        final String filtro = filtro_tipo == 4 ? request.getParameter("NCA_CODIGO") : JspHelper.verificaVarQryStr(request, "FILTRO");
        final String linkRet = "../v3/manterConsignataria?acao=iniciar&FILTRO=" + JspHelper.verificaVarQryStr(request, "FILTRO") + "&FILTRO_TIPO=" + JspHelper.verificaVarQryStr(request, "FILTRO_TIPO") + "&FILTRO2=" + JspHelper.verificaVarQryStr(request, "FILTRO2") + "&NCA_CODIGO=" + filtro;

        try {
            final CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                final List<Short> statusCsa = new ArrayList<>();
                statusCsa.add(CodedValues.STS_INATIVO);
                statusCsa.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.CSA_ATIVO, statusCsa);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!"".equals(filtro) && (filtro_tipo != -1)) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.CSA_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                        break;
                    case 4:
                        // DESENV-13750 - Filtro Natureza Consignatária
                        campo = Columns.CSA_NCA_NATUREZA;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            if (!"".equals(filtro2)) {
                criterio.setAttribute(Columns.CSA_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            consignatarias = new ArrayList<>();
        }

        // Exibe Botao que leva ao rodapé
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("linkRet", linkRet);

        return viewRedirect("jsp/manterConsignataria/listarTodasConsignatarias", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=editarConsignataria"})
    public String editarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam(value = "csa", required = true, defaultValue = "") String csa) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return carregarDadosParaVisualizacao(request, session, model, responsavel, csa);
    }

    @RequestMapping(params = {"acao=salvar"})
    public String salvarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);

        boolean podeEditarCsa = false;
        if (responsavel.isCsa()) {
            podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA);
        } else {
            podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS);
        }

        final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSA);
        String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "csa");
        final Object paramVrfIpAcesso = ParamSist.getInstance().getParam(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, responsavel);
        final String iniciaCredenciamento = JspHelper.verificaVarQryStr(request, "iniciaCredenciamento");

        // Atualiza a Consignataria
        final String reqColumnsStr = "CSA_IDENTIFICADOR|CSA_NOME";
        final String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        if ((request.getParameter("MM_update") != null) && (msgErro.length() == 0) && (podeEditarCsa || podeEditarEnderecoAcesso)) {
            final String csaIpAcesso = JspHelper.verificaVarQryStr(request, "csa_ip_acesso");
            try {
                // Valida a lista de IPs.
                if (!TextHelper.isNull(csaIpAcesso) && podeEditarEnderecoAcesso) {
                    final List<String> ipsAcesso = Arrays.asList(csaIpAcesso.split(";"));
                    if (!JspHelper.validaListaIps(ipsAcesso)) {
                        throw new ViewHelperException("mensagem.erro.ip.invalido", responsavel);
                    }
                }

                ConsignatariaTransferObject consignataria = null;
                if ((csaCodigo != null) && !"".equals(csaCodigo)) {
                    consignataria = new ConsignatariaTransferObject(csaCodigo);
                } else {
                    consignataria = new ConsignatariaTransferObject();
                    consignataria.setCsaAtivo(CodedValues.STS_ATIVO);
                }

                consignataria.setCsaBairro(JspHelper.verificaVarQryStr(request, "CSA_BAIRRO"));
                consignataria.setCsaCep(JspHelper.verificaVarQryStr(request, "CSA_CEP"));
                consignataria.setCsaCidade(JspHelper.verificaVarQryStr(request, "CSA_CIDADE"));
                consignataria.setCsaCnpj(JspHelper.verificaVarQryStr(request, "CSA_CNPJ"));
                consignataria.setCsaCompl(JspHelper.verificaVarQryStr(request, "CSA_COMPL"));
                consignataria.setCsaDigCta(JspHelper.verificaVarQryStr(request, "CSA_DIG_CTA"));
                consignataria.setCsaCnpjCta(JspHelper.verificaVarQryStr(request, "CSA_CNPJ_CTA"));
                consignataria.setCsaEmail(JspHelper.verificaVarQryStr(request, "CSA_EMAIL"));
                consignataria.setCsaFax(JspHelper.verificaVarQryStr(request, "CSA_FAX"));
                consignataria.setCsaIdentificador(JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR"));
                consignataria.setCsaLogradouro(JspHelper.verificaVarQryStr(request, "CSA_LOGRADOURO"));
                consignataria.setCsaNome(JspHelper.verificaVarQryStr(request, "CSA_NOME"));
                consignataria.setCsaPermiteApi(!"".equals(JspHelper.verificaVarQryStr(request, "CSA_PERMITE_API")) ? JspHelper.verificaVarQryStr(request, "CSA_PERMITE_API") : "N" );
                if ((ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel) != 0) && responsavel.isSup()) {
                    consignataria.setCsaConsultaMargemSemSenha(!JspHelper.verificaVarQryStr(request, "CSA_CONSULTA_MARGEM_SEM_SENHA").isEmpty() ? JspHelper.verificaVarQryStr(request, "CSA_CONSULTA_MARGEM_SEM_SENHA") : "N");
                }
                // Grupo de consignatárias
                final String tgc_codigo = JspHelper.verificaVarQryStr(request, "TGC_CODIGO");
                if (tgc_codigo != null) {
                    if ("".equals(tgc_codigo)) {
                        consignataria.setTgcCodigo(null);
                    } else {
                        consignataria.setTgcCodigo(tgc_codigo);
                    }
                }

                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_NRO"))) {
                    consignataria.setCsaNro(Integer.valueOf(JspHelper.verificaVarQryStr(request, "CSA_NRO")));
                } else {
                    consignataria.setCsaNro(null);
                }
                consignataria.setCsaNroAge(JspHelper.verificaVarQryStr(request, "CSA_NRO_AGE"));
                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_NRO_BCO"))) {
                    consignataria.setCsaNroBco(JspHelper.verificaVarQryStr(request, "CSA_NRO_BCO"));
                } else {
                    consignataria.setCsaNroBco(null);
                }
                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_NRO_CTA"))) {
                    consignataria.setCsaNroCta(JspHelper.verificaVarQryStr(request, "CSA_NRO_CTA"));
                } else {
                    consignataria.setCsaNroCta(null);
                }
                consignataria.setCsaResponsavel(JspHelper.verificaVarQryStr(request, "CSA_RESPONSAVEL"));
                consignataria.setCsaResponsavel2(JspHelper.verificaVarQryStr(request, "CSA_RESPONSAVEL_2"));
                consignataria.setCsaResponsavel3(JspHelper.verificaVarQryStr(request, "CSA_RESPONSAVEL_3"));
                consignataria.setCsaRespCargo(JspHelper.verificaVarQryStr(request, "CSA_RESP_CARGO"));
                consignataria.setCsaRespCargo2(JspHelper.verificaVarQryStr(request, "CSA_RESP_CARGO_2"));
                consignataria.setCsaRespCargo3(JspHelper.verificaVarQryStr(request, "CSA_RESP_CARGO_3"));
                consignataria.setCsaRespTelefone(JspHelper.verificaVarQryStr(request, "CSA_RESP_TELEFONE"));
                consignataria.setCsaRespTelefone2(JspHelper.verificaVarQryStr(request, "CSA_RESP_TELEFONE_2"));
                consignataria.setCsaRespTelefone3(JspHelper.verificaVarQryStr(request, "CSA_RESP_TELEFONE_3"));
                final String telefoneCsa = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_DDD_TEL")) ? JspHelper.verificaVarQryStr(request, "CSA_DDD_TEL") + JspHelper.verificaVarQryStr(request, "CSA_TEL") : JspHelper.verificaVarQryStr(request, "CSA_TEL");
                consignataria.setCsaTel(telefoneCsa);
                consignataria.setCsaUf(JspHelper.verificaVarQryStr(request, "CSA_UF"));
                consignataria.setCsaTxtContato(JspHelper.verificaVarQryStr(request, "CSA_TXT_CONTATO"));
                consignataria.setCsaInstrucaoAnexo(JspHelper.verificaVarQryStr(request, "CSA_INSTRUCAO_ANEXO"));
                consignataria.setCsaContato(JspHelper.verificaVarQryStr(request, "CSA_CONTATO"));
                consignataria.setCsaContatoTel(JspHelper.verificaVarQryStr(request, "CSA_CONTATO_TEL"));
                consignataria.setCsaEndereco2(JspHelper.verificaVarQryStr(request, "CSA_ENDERECO_2"));
                consignataria.setCsaNomeAbreviado(JspHelper.verificaVarQryStr(request, "CSA_NOME_ABREV"));
                if (podeEditarEnderecoAcesso) {
                    consignataria.setCsaIPAcesso(csaIpAcesso);
                    consignataria.setCsaDDNSAcesso(JspHelper.verificaVarQryStr(request, "csa_ddns_acesso"));
                }
                consignataria.setCsaNroContratoZetra(JspHelper.verificaVarQryStr(request, "CSA_NRO_CONTRATO_ZETRA"));
                final String exigeEnderecoNew = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "csa_exige_endereco_acesso")) ? JspHelper.verificaVarQryStr(request, "csa_exige_endereco_acesso") : null;
                final String exigeEnderecoOld = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "csa_exige_endereco_acesso_old")) ? JspHelper.verificaVarQryStr(request, "csa_exige_endereco_acesso_old") : null;
                if (exigeEnderecoNew != null) {
                    // Se o campo estava vazio e o parametro tambem esta vazio
                    // OU Se o campo estava vazio, mas o parametro esta preenchido com um valor diferente
                    // OU Se o campo estava preenchido com um valor diferente do novo
                    if (((exigeEnderecoOld == null) && (paramVrfIpAcesso == null)) || ((exigeEnderecoOld == null) && (paramVrfIpAcesso != null) && !exigeEnderecoNew.equals(paramVrfIpAcesso)) || ((exigeEnderecoOld != null) && !exigeEnderecoNew.equals(exigeEnderecoOld))) {
                        consignataria.setCsaExigeEnderecoAcesso(exigeEnderecoNew);
                    }
                }

                // Projeto inadimplência
                final String partProjInadimplenciaOld = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia_old")) ? JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia_old") : "N";
                final String partProjInadimplencia = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia")) ? JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia") : partProjInadimplenciaOld;

                if (responsavel.isSup() && !partProjInadimplenciaOld.equals(partProjInadimplencia)) {
                    consignataria.setCsaProjetoInadimplencia(partProjInadimplencia);
                }

                if (responsavel.isSup() || responsavel.isCsa()) {
                    // DESENV-14835 -> Validação de obrigatoriedade, se o e-mail projeto inadimplência é valido para salvar no BD.
                    if ((!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia")) && "S".equalsIgnoreCase(JspHelper.verificaVarQryStr(request, "participa_projeto_inadimplencia")) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA"))) || (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_EMAIL_PROJ_INADIMPLENCIA, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA")))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.consignantaria.email.inadimplencia", responsavel));
                        return carregarDadosParaVisualizacao(request, session, model, responsavel, csaCodigo);
                    }

                    if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_EMAIL_PROJ_INADIMPLENCIA, responsavel) && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA")) && !TextHelper.isEmailValid(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA"))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.consignantaria.email.inadimplencia.invalido", responsavel));
                        return carregarDadosParaVisualizacao(request, session, model, responsavel, csaCodigo);
                    } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_EMAIL_PROJ_INADIMPLENCIA, responsavel) && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA")) && TextHelper.isEmailValid(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA"))) {
                        consignataria.setCsaEmailProjInadimplencia(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_PROJ_INADIMPLENCIA"));
                    }

                }

                consignataria.setCsaEmailDesbloqueio(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_DESBLOQUEIO"));

                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_DATA_EXPIRACAO"))) {
                    try {
                        final Date csaDataExpiracao = DateHelper.parse(JspHelper.verificaVarQryStr(request, "CSA_DATA_EXPIRACAO"), LocaleHelper.getDatePattern());
                        consignataria.setCsaDataExpiracao(csaDataExpiracao);

                        consignataria.setCsaEmailExpiracao(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_EXPIRACAO"));
                    } catch (final Exception ex) {
                    }
                } else {
                    consignataria.setCsaDataExpiracao(null);
                }
                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_DATA_EXPIRACAO_CADASTRAL"))) {
                    try {
                        final Date csaDataExpiracaoCadastral = DateHelper.parse(JspHelper.verificaVarQryStr(request, "CSA_DATA_EXPIRACAO_CADASTRAL"), LocaleHelper.getDatePattern());
                        consignataria.setCsaDataExpiracaoCadastral(csaDataExpiracaoCadastral);

                        consignataria.setCsaEmailExpiracao(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_EXPIRACAO"));
                    } catch (final Exception ex) {
                    }
                } else {
                    consignataria.setCsaDataExpiracaoCadastral(null);
                }

                if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_NRO_CONTRATO"))) {
                    consignataria.setCsaNroContrato(JspHelper.verificaVarQryStr(request, "CSA_NRO_CONTRATO"));
                } else {
                    consignataria.setCsaNroContrato(null);
                }
                if (responsavel.isSup()) {
                    consignataria.setCsaUnidadeOrganizacional(JspHelper.verificaVarQryStr(request, "CSA_UNIDADE_ORGANIZACIONAL"));
                    if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR_INTERNO")) && !JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR_INTERNO").equals(ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel))) {
                        consignataria.setCsaIdentificadorInterno(JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR_INTERNO"));
                    } else if (!"".equals(JspHelper.verificaVarQryStr(request, "ajudaBanco")) && JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR_INTERNO").equals(ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel))) {
                        consignataria.setCsaIdentificadorInterno(JspHelper.verificaVarQryStr(request, "ajudaBanco"));
                    } else if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_IDENTIFICADOR_INTERNO, responsavel)) {
                        if (!"".equals(JspHelper.verificaVarQryStr(request, "CSA_IDENTIFICADOR_INTERNO"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.codigo.zetra", responsavel));
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.codigo.zetra.outro", responsavel));
                        }
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    } else {
                        consignataria.setCsaIdentificadorInterno("");
                    }
                }

                if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_CNPJ, responsavel) && TextHelper.isNull(consignataria.getCsaCnpj())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.consignantaria.cnpj", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_CNPJ_CTA, responsavel) && TextHelper.isNull(consignataria.getCsaCnpjCta())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.consignantaria.cnpj.dados.bancarios", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!"".equals(JspHelper.verificaVarQryStr(request, "NCA_CODIGO"))) {
                    consignataria.setCsaNcaNatureza(JspHelper.verificaVarQryStr(request, "NCA_CODIGO"));
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servico.natureza", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                // Status da consignatária
                if (responsavel.isCseSup() && ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_STATUS, responsavel)) {
                    consignataria.setCsaAtivo(Short.valueOf(JspHelper.verificaVarQryStr(request, "CSA_ATIVO")));
                }
                // Permite inclusão de ADEs
                if (responsavel.isCseSup() && ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_PERMITE_INCLUIR_ADE, responsavel)) {
                    consignataria.setCsaPermiteIncluirAde(JspHelper.verificaVarQryStr(request, "CSA_PERMITE_INCLUIR_ADE"));
                }

                if (responsavel.isCseSup() && ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_CODIGO_ANS, responsavel)) {
                    consignataria.setCsaCodigoAns(JspHelper.verificaVarQryStr(request, "CSA_CODIGO_ANS"));
                }

                final Date dataInicioContrato = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_DATA_INICIO_CONTRATO")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "CSA_DATA_INICIO_CONTRATO"), LocaleHelper.getDatePattern()) : null;
                final Date dataRenovacaoContrato = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_DATA_RENOVACAO_CONTRATUAL")) ? DateHelper.parse(JspHelper.verificaVarQryStr(request, "CSA_DATA_RENOVACAO_CONTRATUAL"), LocaleHelper.getDatePattern()) : null;
                final String numProcessoContrato = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_NUMERO_PROCESSO")) ? JspHelper.verificaVarQryStr(request, "CSA_NUMERO_PROCESSO") : null;
                final String obsContrato = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_OBS_CONTRATUAL")) ? JspHelper.verificaVarQryStr(request, "CSA_OBS_CONTRATUAL") : null;

                consignataria.setCsaDataInicioContrato(dataInicioContrato);
                consignataria.setCsaDataRenovacaoContrato(dataRenovacaoContrato);
                consignataria.setCsaNumeroProcessoContrato(numProcessoContrato);
                consignataria.setCsaObsContrato(obsContrato);

                final String celularCsa = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "CSA_DDD_TELEFONE")) ? JspHelper.verificaVarQryStr(request, "CSA_DDD_TELEFONE") + JspHelper.verificaVarQryStr(request, "CSA_WHATSAPP") : JspHelper.verificaVarQryStr(request, "CSA_WHATSAPP");
                consignataria.setCsaWhatsapp(celularCsa);
                consignataria.setCsaEmailContato(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_CONTATO"));
                
    			final String tpaNotificaCsaAlteracaoRco = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, responsavel);
    			final boolean podeNotificaCsaAlteracaoRco = (!TextHelper.isNull(tpaNotificaCsaAlteracaoRco) && tpaNotificaCsaAlteracaoRco.equals(CodedValues.TPC_SIM));              
                final boolean podeNotificarCsaAlteracaoRegraConvenio = (responsavel.isSup() || responsavel.isCsa()) && podeNotificaCsaAlteracaoRco;
                if (podeNotificarCsaAlteracaoRegraConvenio && ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_EMAIL_NOTIFICACAO_RCO, responsavel)) {
                	 consignataria.setCsaEmailNotificacaoRco(JspHelper.verificaVarQryStr(request, "CSA_EMAIL_NOTIFICACAO_RCO"));
                }               

                String rotuloSave = null;
                if (!TextHelper.isNull(csaCodigo)) {
                    consignatariaController.updateConsignataria(consignataria, responsavel);
                    rotuloSave = "mensagem.alteracoes.salvas.sucesso";
                } else {
                    //Criando nova consignataria.
                    csaCodigo = consignatariaController.createConsignataria(consignataria, responsavel);
                    //Colocando um endereço no paramSession
                    final Map<String, String[]> parametros = new HashMap<>();
                    parametros.put("acao", new String[] { "editarConsignataria" });
                    parametros.put("csa", new String[] { csaCodigo });
                    final String link = request.getRequestURI();
                    paramSession.addHistory(link, parametros);
                    rotuloSave = "mensagem.consignataria.criada.sucesso";
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(rotuloSave, responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        if(!TextHelper.isNull(iniciaCredenciamento) && CodedValues.TPC_SIM.equals(iniciaCredenciamento) && !TextHelper.isNull(csaCodigo)) {
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
            try {
                EnviaEmailHelper.enviarEmailNotificacaoCsaModuloCredenciamento(csa, responsavel);
                consignatariaController.criarCredenciamentoConsignataria(csaCodigo, StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo(), DateHelper.getSystemDatetime(), null, responsavel);
            } catch (final ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        return carregarDadosParaVisualizacao(request, session, model, responsavel, csaCodigo);
    }

    /**
     * Busca os dados para a tela de edição
     */
    private String carregarDadosParaVisualizacao(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, String csa) throws InstantiationException, IllegalAccessException {
        final ParamSession paramSession = ParamSession.getParamSession(session);

        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        boolean podeEditarCsa = false;
        if (responsavel.isCsa()) {
            podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA);
        } else {
            podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS);
        }

        final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSA);
        final boolean podeEditarEnderecosCsa = responsavel.temPermissao(CodedValues.FUN_EDITAR_ENDERECOS_CONSIGNATARIA);

        final boolean podeEditarParamCsa = responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_CONSIGNATARIA);
        /* usuario */
        final boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_CSA);
        final boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA);
        /* perfil usuario */
        final boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_CSA);

        // Verifica parâmetro de sistema se permite cadastro de ip interno
        final boolean permiteCadIpInternoCsaCor = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_IP_REDE_INTERNA_CSA_COR, responsavel);

        final int permiteConsultarReservarPortabilidadeCodigo = ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel);

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : csa;
        final String btnCancelar = responsavel.isCsa() ? "../v3/carregarPrincipal" : SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);

        final Object paramVrfIpAcesso = ParamSist.getInstance().getParam(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, responsavel);

        String csaNome = "";
        String csa_ip_acesso = "";
        String csa_ativo = "";
        String csa_ddns_acesso = "";
        String csa_nome_abrev_script = "";
        String cancelar = "../v3/manterConsignataria$acao(editarConsignataria";
        String csaIdentificadorInterno = "";
        String habilitaApi = "N";

        ConsignatariaTransferObject consignataria = null;
        try {
            if (!TextHelper.isNull(csaCodigo)) {
                consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                csa_ip_acesso = (consignataria.getCsaIPAcesso() != null ? consignataria.getCsaIPAcesso() : "");
                csa_ddns_acesso = (consignataria.getCsaDDNSAcesso() != null ? consignataria.getCsaDDNSAcesso() : "");
                csa_ativo = consignataria.getCsaAtivo() != null ? consignataria.getCsaAtivo().toString() : null;
                csa_nome_abrev_script = consignataria.getCsaNomeAbreviado();
                csaIdentificadorInterno = (consignataria.getCsaIdentificadorInterno() != null ? consignataria.getCsaIdentificadorInterno() : "");
                csaNome = consignataria != null ? consignataria.getCsaNomeAbreviado() : "";
                habilitaApi = consignataria.getCsaPermiteApi();

                if ((csaNome == null) || csaNome.isBlank()) {
                    csaNome = consignataria != null ? consignataria.getCsaNome() : "";
                }
                if (!responsavel.isCsa()) {
                    cancelar = "../v3/manterConsignataria$acao(editarConsignataria|csa(" + csaCodigo;
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> lstOcorrencias = null;
        try {
            if (!TextHelper.isNull(csaCodigo)) {
                final OcorrenciaConsignatariaTransferObject criterio = new OcorrenciaConsignatariaTransferObject();
                criterio.setCsaCodigo(csaCodigo);

                final int total = consignatariaController.countOcaConsignatarias(criterio, responsavel);
                final int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                }

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("senha");
                params.remove("serAutorizacao");
                params.remove("cryptedPasswordFieldName");
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                final List<String> requestParams = new ArrayList<>(params);

                lstOcorrencias = consignatariaController.lstOcaConsignatarias(criterio, offset, size, responsavel);

                configurarPaginador("../v3/manterConsignataria?acao=editarConsignataria", "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            lstOcorrencias = new ArrayList<>();
        }

        final String csaAtivo = consignataria != null ? (consignataria.getCsaAtivo() != null ? consignataria.getCsaAtivo().toString() : "1") : "1";

        String mensagemDesbloqueio = "";
        if (!"1".equals(csaAtivo)) {
            mensagemDesbloqueio = consignatariaController.recuperarMensagemDesbloqueioConsignataria(csaCodigo);
        }

        List<TransferObject> lstGrupoConsignataria = new ArrayList<>();
        List<TransferObject> lstNatureza = new ArrayList<>();
        try {
            lstGrupoConsignataria = consignatariaController.lstGrupoConsignataria(null, responsavel);
            lstNatureza = consignatariaController.lstNatureza();
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        boolean podeVisualizarContrato = false;
        boolean podeEditarContrato = false;
        if (responsavel.isCseSupOrg() || responsavel.isCsa()) {
            podeVisualizarContrato = true;
        }
        if (responsavel.isCseSupOrg()) {
            podeEditarContrato = true;
        }

        // Exibe Botao que leva ao rodapé
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        boolean podeNotificaCsaAlteracaoRCO = false;
		try {
			final String tpaNotificaCsaAlteracaoRCO = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO, responsavel);
			podeNotificaCsaAlteracaoRCO = (!TextHelper.isNull(tpaNotificaCsaAlteracaoRCO) && "S".equals(tpaNotificaCsaAlteracaoRCO));
		} catch (ParametroControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
        
        boolean podeNotificarCsaAlteracaoRegraConvenio = false;
        if((responsavel.isSup() || responsavel.isCsa()) && podeNotificaCsaAlteracaoRCO) {
        	podeNotificarCsaAlteracaoRegraConvenio = true;
        }
        
        model.addAttribute("podeNotificarCsaAlteracaoRegraConvenio", podeNotificarCsaAlteracaoRegraConvenio);        
        model.addAttribute("permiteConsultarReservarPortabilidadeCodigo", permiteConsultarReservarPortabilidadeCodigo);
        model.addAttribute("podeEditarContrato", podeEditarContrato);
        model.addAttribute("podeVisualizarContrato", podeVisualizarContrato);
        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("csaIdentificadorInterno", csaIdentificadorInterno);
        model.addAttribute("podeEditarCsa", podeEditarCsa);
        model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("csaAtivo", csaAtivo);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("temCET", temCET);
        model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
        model.addAttribute("podeConsultarUsu", podeConsultarUsu);
        model.addAttribute("podeCriarUsu", podeCriarUsu);
        model.addAttribute("podeEditarParamCsa", podeEditarParamCsa);
        model.addAttribute("cancelar", cancelar);
        model.addAttribute("consignataria", consignataria);
        model.addAttribute("permiteCadIpInternoCsaCor", permiteCadIpInternoCsaCor);
        model.addAttribute("paramVrfIpAcesso", paramVrfIpAcesso);
        model.addAttribute("lstOcorrencias", lstOcorrencias);
        model.addAttribute("csa_ip_acesso", csa_ip_acesso);
        model.addAttribute("btnCancelar", btnCancelar);
        model.addAttribute("csa_ddns_acesso", csa_ddns_acesso);
        model.addAttribute("csa_ativo", csa_ativo);
        model.addAttribute("csa_nome_abrev_script", csa_nome_abrev_script);
        model.addAttribute("mensagemDesbloqueio", mensagemDesbloqueio);
        model.addAttribute("lstGrupoConsignataria", lstGrupoConsignataria);
        model.addAttribute("lstNatureza", lstNatureza);
        /*DESENV-14092 : Campo Codigo ANS deve ser habilitado somente ao informar natureza igual a "Operadora de Benefícios" e se papel do usuário logado for CSE ou SUP*/
        model.addAttribute("habilitaCampoCsaCodigoAns", !TextHelper.isNull(consignataria) && CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS.equals(consignataria.getCsaNcaNatureza()) && responsavel.isCseSup());
        model.addAttribute("podeEditarEnderecosCsa", podeEditarEnderecosCsa);
        model.addAttribute("habilitaApi", habilitaApi);

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_CREDENCIAMENTO_CSA, responsavel) && TextHelper.isNull(csaCodigo)) {
            model.addAttribute("possibilitaCredenciamento", Boolean.TRUE);
        }

        return viewRedirect("jsp/manterConsignataria/editarConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=inserirObsBloqueio"})
    public String inserirObsBloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
        final boolean penalidade = "penalizar".equals(JspHelper.verificaVarQryStr(request, "acao"));
        linkVoltar = "".equals(linkVoltar) ? paramSession.getLastHistory() : linkVoltar;
        linkVoltar = SynchronizerToken.updateTokenInURL(linkVoltar, request);
        final boolean bloqueado = (penalidade) ? "csa_ativo".equals(JspHelper.verificaVarQryStr(request, "bloqueado")) : !request.getParameter("status").equals(CodedValues.STS_ATIVO.toString());
        final String param = (penalidade) ? "acao=penalizar" : "acao=bloquear";

        final List<TransferObject> tiposPenalidade = penalidadeController.lstTiposPenalidade(responsavel);
        List<TransferObject> tiposMotivoOperacao = null;

        try {
            tiposMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignataria(null, responsavel);
        } catch (final TipoMotivoOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        final boolean exigeMotivoOperacaoBloquearDesbloquear = super.isExigeMotivoOperacao(CodedValues.FUN_EDT_CONSIGNATARIAS, responsavel);
        final boolean exigeMotivoOperacaoPenalizar = super.isExigeMotivoOperacao(CodedValues.FUN_INC_PENALIDADE, responsavel);

        model.addAttribute("penalidade", penalidade);
        model.addAttribute("bloqueado", bloqueado);
        model.addAttribute("param", param);
        model.addAttribute("linkVoltar", linkVoltar);
        model.addAttribute("tiposPenalidade", tiposPenalidade);
        model.addAttribute("tiposMotivoOperacao", tiposMotivoOperacao);
        model.addAttribute("exigeMotivoOperacaoBloquearDesbloquear", exigeMotivoOperacaoBloquearDesbloquear);
        model.addAttribute("exigeMotivoOperacaoPenalizar", exigeMotivoOperacaoPenalizar);

        return viewRedirect("jsp/manterConsignataria/inserirObservacaoBloqueio", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=penalizar"})
    public String inserirPenalidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final List<TransferObject> tiposPenalidade = penalidadeController.lstTiposPenalidade(responsavel);
        if ((tiposPenalidade == null) || tiposPenalidade.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.consignataria.nao.existe.penalidade.cadastrada.sistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Cria penalidade para consignatária
        if (request.getParameter("penalidade") != null) {
            final String tpeCodigo = request.getParameter("tpeCodigo");
            final String tmoCodigo = request.getParameter("tmoCodigo");
            final String obs = JspHelper.verificaVarQryStr(request, "OCC_OBS");
            final String csaCodigo = request.getParameter("codigo");
            final ConsignatariaTransferObject csa = new ConsignatariaTransferObject(csaCodigo);

            try {
                if (isExigeMotivoOperacao(CodedValues.FUN_INC_PENALIDADE, responsavel) && TextHelper.isNull(tmoCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_OBSERVACAO_BLOQUEAR_CSA, CodedValues.TPC_SIM, responsavel) && TextHelper.isNull(obs)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (TextHelper.isNull(tpeCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.penalidade.csa", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                consignatariaController.inserePenalidade(csa, obs, tpeCodigo, tmoCodigo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.penalidade.incluida.sucesso", responsavel));
                return listar(request, response, session, model);

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        return inserirObsBloqueio(request, response, session, model);
    }

    @RequestMapping(params = {"acao=bloquear"})
    public String modificarBloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
        String link = "".equals(linkVoltar) ? paramSession.getLastHistory() : linkVoltar;
        link = SynchronizerToken.updateTokenInURL(link, request);

        final String csaCodigo = request.getParameter("codigo");
        final String status = request.getParameter("status");
        final String tpeCodigo = request.getParameter("tpeCodigo");
        final String obs = JspHelper.verificaVarQryStr(request, "OCC_OBS");
        final String tmoCodigo = request.getParameter("tmoCodigo");
        final String dataDesbloqueioAutomatica = request.getParameter("dataDesbloqueioAutomatica");

        final boolean exigeObsBloqueio = ParamSist.paramEquals(CodedValues.TPC_EXIGE_OBSERVACAO_BLOQUEAR_CSA, CodedValues.TPC_SIM, responsavel);
        final boolean bloqueado = status.equals(CodedValues.STS_INATIVO.toString()) || status.equals(CodedValues.STS_DESBLOQUEIO_PENDENTE.toString());
        final boolean exigeMotivoOperacaoBloquearDesbloquear = isExigeMotivoOperacao(CodedValues.FUN_EDT_CONSIGNATARIAS, responsavel);

        if ((exigeObsBloqueio && !bloqueado && TextHelper.isNull(obs)) || (exigeMotivoOperacaoBloquearDesbloquear && TextHelper.isNull(tmoCodigo))) {
            request.setAttribute("link_voltar", link);
            return inserirObsBloqueio(request, response, session, model);
        }

        if (status != null) {
            try {
                final ConsignatariaTransferObject csa = new ConsignatariaTransferObject(csaCodigo);
                ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                final String csaEmailDesbloqueio = consignataria.getCsaEmailDesbloqueio();

                if (!TextHelper.isNull(dataDesbloqueioAutomatica) && !bloqueado) {
                    try {
                        // Realiza o parse do campo String para Date
                        final Date csaDataDesbloqAutomatico = DateHelper.parse(dataDesbloqueioAutomatica, LocaleHelper.getDatePattern());
                        // Valida se a data é futura
                        if (csaDataDesbloqAutomatico.compareTo(DateHelper.getSystemDatetime()) < 1) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.bloqueio.csa.data.desbloq.aut.maior.hoje", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                        csa.setCsaDataDesbloqAutomatico(csaDataDesbloqAutomatico);
                    } catch (final ParseException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.invalida", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                if (exigeMotivoOperacaoBloquearDesbloquear && TextHelper.isNull(tmoCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (exigeObsBloqueio && !bloqueado && TextHelper.isNull(obs)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (bloqueado) {
                    consignatariaController.desbloqueiaCsa(csa, obs, tpeCodigo, tmoCodigo, responsavel);
                    final String motivoBloqueioOld = consignataria.getTmbCodigo();
                    consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                    if (!TextHelper.isNull(csaEmailDesbloqueio) && (consignataria.getCsaAtivo() == 1)) {
                        EnviaEmailHelper.enviarEmailNotificacaoDesbloqueioCsa(consignataria.getCsaNome(), csaEmailDesbloqueio, responsavel);
                    }
                    final String motivoBloqueioNew = consignataria.getTmbCodigo();
                    final String desbloqueioPendenteAprovacao = TipoMotivoBloqueioEnum.DESBLOQUEIO_PENDENTE_APROVACAO.getCodigo();

                    if (!TextHelper.isNull(motivoBloqueioOld) && !TextHelper.isNull(motivoBloqueioNew) && !motivoBloqueioOld.equals(desbloqueioPendenteAprovacao) && motivoBloqueioNew.equals(desbloqueioPendenteAprovacao)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.desbloqueio.nao.efetuado.dependente.aprovacao.suporte", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.consignataria.desbloqueada.singular", responsavel));
                    }
                } else {
                    consignatariaController.bloqueiaCsa(csa, obs, tpeCodigo, tmoCodigo, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.consignataria.bloqueada", responsavel));
                }

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = {"acao=excluir"})
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, UnsupportedEncodingException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csaCodigo = request.getParameter("codigo");
        final ConsignatariaTransferObject csa = new ConsignatariaTransferObject(csaCodigo);
        try {
            consignatariaController.removeConsignataria(csa, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.consignataria.excluida.sucesso", responsavel));
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        return listar(request, response, session, model);

    }

    @RequestMapping(params = {"acao=listarServicos"})
    public String listarServicos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //recuperando o campo que foi passado como parametro
        String csaCodigo = "";
        String titulo = "";

        if (responsavel.isCsa()) {
            //recuperando as informaçoes pelo session
            csaCodigo = responsavel.getCodigoEntidade();
            titulo = responsavel.getNomeEntidade();
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //caso alguma informaçao esteja faltando
        //redireciona a pagina para uma pagina de mensagem generica
        if ("".equals(csaCodigo) || "".equals(titulo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //Verifica se sistema permite cadastro de índice para o serviço
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);

        final boolean podeEditarIndices = responsavel.temPermissao(CodedValues.FUN_EDT_INDICES);

        //List que irá conter todos os serviços
        List<TransferObject> servicos = null;
        int total = 0;
        final int size = JspHelper.LIMITE;
        try {
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CNV_CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.NOT_EQUAL_KEY + CodedValues.SCV_INATIVO);

            total = convenioController.countCnvScvCodigo(criterio, responsavel);
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            servicos = convenioController.listCnvScvCodigo(criterio, offset, size, responsavel);
            model.addAttribute("servicos", servicos);

            for (final TransferObject servico : servicos) {
                final String svcCodigo = (String) servico.getAttribute(Columns.SVC_CODIGO);
                final String svcIdentificador = (String) servico.getAttribute(Columns.SVC_IDENTIFICADOR);

                final Set<String> codigos = new TreeSet<>();

                // Caso tenha mais de um codigo de verba
                final List<TransferObject> convenios = convenioController.getCnvCodVerba(svcCodigo, csaCodigo, responsavel);
                for (final TransferObject convenio : convenios) {
                    final String codVerba = (String) convenio.getAttribute(Columns.CNV_COD_VERBA);
                    if (!TextHelper.isNull(codVerba)) {
                        codigos.add(codVerba);
                    }
                }
                String codVerba = svcIdentificador;
                if (codigos.size() > 0) {
                    codVerba = TextHelper.join(codigos.toArray(), ", ");
                }
                servico.setAttribute(Columns.CNV_COD_VERBA, codVerba);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            servicos = new ArrayList<>();
        }

        final String linkListagem = request.getRequestURI() + "?acao=listarServicos";

        //parâmetro e função que diz se utiliza bloqueio na reserva de margem por vinculo do servidor
        boolean utiBloqVincSer = ((ParamSist.getInstance().getParam(CodedValues.TPC_UTILIZA_BLOQ_VINC_SERVIDOR, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_UTILIZA_BLOQ_VINC_SERVIDOR, responsavel).toString()));
        utiBloqVincSer = utiBloqVincSer && (responsavel.isSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_EDT_BLOQUEIO_VINCULO_SERVIDOR);

        final String linkEdicao = request.getRequestURI() + "?acao=editarServico";
        final String linkCnvVincServidor = request.getRequestURI() + "?acao=editarCnvVincServidor";

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));

        configurarPaginador(linkListagem, "rotulo.listar.servicos.consignataria.titulo", total, size, null, false, request, model);

        model.addAttribute("csa_codigo", csaCodigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("permiteCadIndice", permiteCadIndice);
        model.addAttribute("podeEditarIndices", podeEditarIndices);
        model.addAttribute("linkEdicao", linkEdicao);
        model.addAttribute("linkCnvVincServidor", linkCnvVincServidor);
        model.addAttribute("voltar", voltar);
        model.addAttribute("utiBloqVincSer", utiBloqVincSer);

        return viewRedirect("jsp/manterConsignataria/listarServicos", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=editarServico"})
    public String editarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";
        String csaNome = "";

        final boolean indiceNumerico = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString());
        //  Limite numérico do indice
        final int limiteIndice = ((ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel))) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;

        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
            csaNome = responsavel.getNomeEntidade();
        } else if (responsavel.isCseSup()) {
            csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
            csaNome = JspHelper.verificaVarQryStr(request, "csa_nome");
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        final String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "svc");
        final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        final List<PrazoTransferObject> prazos = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);

        final TransferObject nseTO = servicoController.findNaturezaServico(svcCodigo, responsavel);
        final String nseCodigo = (String) nseTO.getAttribute(Columns.NSE_CODIGO);

        // Verifica os parâmetros de sistema
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);

        // Verifica o parametro de sistema para repetir indice
        // se o parametro de sistema nao permitir que o indice seja repetido entrao seta o
        // parametro da consignataria para nao e o torna desativado
        final boolean podeRepetirIndice = ParamSist.paramEquals(CodedValues.TPC_INDICE_REPETIDO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        final boolean permiteCsaLimitarMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_LIMITAR_USO_MARGEM_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Busca os Parâmetros de Sistema Sobre a Reimplantação
        final boolean tpcReimplantacaoAutomatica = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, responsavel);
        final boolean tpcCsaEscolheReimpl = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, CodedValues.TPC_SIM, responsavel);
        final boolean tpcPreservaPrdRejeitada = ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);
        final boolean tpcCsaEscolhePrdRejeitada = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
        final boolean tpcConcluiNaoPagas = ParamSist.paramEquals(CodedValues.TPC_CONCLUI_NAO_PAGAS, CodedValues.TPC_SIM, responsavel);
        final boolean tpcCsaEscolheConclusao = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_CONCLUSAO_NAO_PAGAS, CodedValues.TPC_SIM, responsavel);
        final boolean tpcDefaultReimplante = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_SIM, responsavel);
        final boolean tpcDefaultPreservacao = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_SIM, responsavel);
        final boolean tpcDefaultConclusao = ParamSist.paramEquals(CodedValues.TPC_DEFAULT_PARAM_SVC_CONCLUI_NAO_PG, CodedValues.TPC_SIM, responsavel);

        // Busca os tipos de parâmetro de serviço disponíveis.
        final List<TransferObject> tiposParams = parametroController.lstTipoParamSvc(responsavel);
        final HashMap<Object, Boolean> parametrosSvc = new HashMap<>();
        final Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            final CustomTransferObject paramSvc = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_CSE_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA))));
            } else if (responsavel.isSup()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_SUP_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA))));
            } else if (responsavel.isCsa()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_CSA_ALTERA) != null) && CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_CSA_ALTERA))));
            }
        }
        final List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvc.containsKey(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF) && parametrosSvc.get(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MINIMA) && parametrosSvc.get(CodedValues.TPS_CARENCIA_MINIMA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MAXIMA) && parametrosSvc.get(CodedValues.TPS_CARENCIA_MAXIMA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_LIBERADO_MINIMO) && parametrosSvc.get(CodedValues.TPS_VLR_LIBERADO_MINIMO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MINIMO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_LIBERADO_MAXIMO) && parametrosSvc.get(CodedValues.TPS_VLR_LIBERADO_MAXIMO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MAXIMO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO) && parametrosSvc.get(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR) && parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA) && parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_INTERVENIENCIA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_INDICE) && parametrosSvc.get(CodedValues.TPS_INDICE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_INDICE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA) && parametrosSvc.get(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL) && parametrosSvc.get(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA) && parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO) && parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO);
        }
        if (permiteCadIndice && (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA) && parametrosSvc.get(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA).booleanValue())) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA) && parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CNV_PODE_DEFERIR) && parametrosSvc.get(CodedValues.TPS_CNV_PODE_DEFERIR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CNV_PODE_DEFERIR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA) && parametrosSvc.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
        }
        if (!temCET && parametrosSvc.containsKey(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA) && parametrosSvc.get(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE) && parametrosSvc.get(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING) && parametrosSvc.get(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING);
        }

        // Parametro que permite fazer contrato com prazo superior ao contrato do servidor ao orgão:
        // Inclui o código do parâmetro na lista dos parametros que serão salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV) && parametrosSvc.get(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV) && parametrosSvc.get(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA) && parametrosSvc.get(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA);
        }
        // Parâmetros para cadastro dos emails de notificação dos eventos de compra de contratos:
        // Inclui o código do parâmetro na lista dos parametros que serão salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA) && parametrosSvc.get(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_BOLETO) && parametrosSvc.get(CodedValues.TPS_EXIBE_BOLETO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIBE_BOLETO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO) && parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO) && parametrosSvc.get(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO) && parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA) && parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES) && parametrosSvc.get(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO) && parametrosSvc.get(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO) && parametrosSvc.get(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE) && parametrosSvc.get(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO) && parametrosSvc.get(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO);
        }

        // Parâmetro para envio de mensagem ao servidor/funcionário após o deferimento de uma solicitação
        // Inclui parâmetro na lista de parâmetros a serem salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA) && parametrosSvc.get(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA);
        }
        // DESENV-10999
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO) && parametrosSvc.get(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO);
        }

        // DESENV-11865
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM) && parametrosSvc.get(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM).booleanValue() && responsavel.isCseSup()) {
            tpsCodigos.add(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO) && parametrosSvc.get(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO);
        }

        // DESENV-14336 : Parâmetro para oferecer contratação de seguro prestamista ao solicitar empréstimo
        if (ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel) &&
                parametrosSvc.containsKey(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC) &&
                parametrosSvc.get(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC);
        }

        // DESENV-14405 : Parâmetro bloqueia inclusão de contratos via lote de servidores de acordo com rse_tipo cadastrado.
        if (parametrosSvc.containsKey(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO) && parametrosSvc.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO);
        }

        // DESENV-17552 : Parametro Serviço verifica se exige senha do servidor via lote
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE) && parametrosSvc.get(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE);
        }

        // DESENV-15679 : Ofertas patrocinadas na página de ranking de empréstimo.
        if (parametrosSvc.containsKey(CodedValues.TPS_RELEVANCIA_CSA_RANKING) && parametrosSvc.get(CodedValues.TPS_RELEVANCIA_CSA_RANKING).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_RELEVANCIA_CSA_RANKING);
        }

        // DESENV-16329 : Projeto MAG - eConsig - Criar novo REST para retornar parâmetros MAG
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO);
        }

        // DESENV-16871 : Parâmetro sobre forma de numeração das parcelas
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS, CodedValues.TPC_SIM, responsavel) &&
                parametrosSvc.containsKey(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS) &&
                parametrosSvc.get(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO) && parametrosSvc.get(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA) && parametrosSvc.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_VALOR_SVC_FIXO_POSTO) && parametrosSvc.get(CodedValues.TPS_VALOR_SVC_FIXO_POSTO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VALOR_SVC_FIXO_POSTO);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA) && parametrosSvc.get(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA) && parametrosSvc.get(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR) && parametrosSvc.get(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MINIMO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_VLR_MINIMO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MAXIMO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_VLR_MAXIMO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
        }


        // Busca os parâmetros de serviço necessários
        String carenciaMaxCse = "";
        boolean serSenhaObrigatoriaCsa = true;
        boolean calculaTacIofCse = false;
        try {
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            carenciaMaxCse = paramSvcCse.getTpsCarenciaMaxima() != null ? paramSvcCse.getTpsCarenciaMaxima() : "";
            serSenhaObrigatoriaCsa = paramSvcCse.isTpsSerSenhaObrigatoriaCsa();
            calculaTacIofCse = paramSvcCse.isTpsCalcTacIofValidaTaxaJuros();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        String diasDesblAutReserva = "";
        String carenciaMinima = "", carenciaMaxima = "", carenciaMinimaCSE = "", carenciaMaximaCSE = "";
        String vlrLiberadoMinimo = "", vlrLiberadoMaximo = "";
        String idadeMaxima = "", idadeMinima = "", idadeMaximaCSE = "", idadeMinimaCSE = "";
        String percMargemSimulador = "", percMargemSimuladorCSE = "";
        String percMargemFolhaLimiteCsa = "", percMargemFolhaLimiteCsaCSE = "";
        String indice = "", valorInterveniencia = "", valorIntervenienciaRef = "", valorIntervenienciaCSE = "", valorIntervenienciaRefCSE = "";
        String limite_aumento_valor_ade = "";
        String limite_aumento_valor_ade_ref = "";
        String vlrMinimoContrato = "", vlrMaximoContrato = "", vlrMinimoContratoCSE = "", vlrMaximoContratoCSE = "";

        boolean incluiIof = calculaTacIofCse, incluiIofCSE = false;
        boolean csaReimplantacaoAut = false, cseReimplantacaoAut = false;
        boolean csaPreservaPrdRejeitada = false, csePreservaPrdRejeitada = false;
        boolean csaConcluiNaoPagas = false, cseConcluiNaoPagas = false;
        boolean reimplanta = false;
        boolean preserva = false;
        boolean concluiNaoPagas = false;
        boolean concluiAdeSerExcluido = false;
        boolean csaDeveContarLimiteRanking = true, cseDeveContarLimiteRanking = false, serSenhaObrigatoriaCse = true;
        boolean csaExigeServidorCorrentista = false;
        boolean tpsExibeBoleto = false;
        boolean permiteValorNegativo = false;
        boolean exigeAssinaturaDigital = false;
        boolean exibeCsaListagemSolicitacao = true;
        boolean exibirComoValorForaMargem = false;
        String tpsPermiteRepetirIndiceCsa = "S", tpsPermiteRepetirIndiceCse = "S";
        String csaPermitePrazoMaior = "N", csePermitePrazoMaior = "N";
        String cnvDefere = "S";
        csaReimplantacaoAut = tpcDefaultReimplante;
        csaPreservaPrdRejeitada = tpcDefaultPreservacao;
        csaConcluiNaoPagas = tpcDefaultConclusao;
        String maxPrazoRenegociacaoPeriodo = "";
        String minPrazoRenegociacaoPeriodo = "0";
        String periodoLimiteAdeDuplicidade = "", periodoLimiteAdeDuplicidadeCSE = "";
        boolean bloqueiaInclusaoLoteRseTipo = false;
        boolean exigeSenhaServidorViaLote = false;
        String relevanciaCsaRanking = "";
        String configurarIdPropostaConvenio = "";
        String configurarDiaVencimentoContrato = "";
        String configurarPeriodoCompetenciaDebito = "";
        String configurarNumeroConvenio = "";
        String configurarCodigoAdesao = "";
        String configurarIdadeMaximaContratacaoSeguro = "";
        String formaNumeracaoParcelas = "";
        String formaNumeracaoParcelasPadrao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_FORMA_NUMERACAO_PARCELAS, responsavel);
        if (TextHelper.isNull(formaNumeracaoParcelasPadrao)) {
            formaNumeracaoParcelasPadrao = CodedValues.FORMA_NUMERACAO_PARCELAS_SEQUENCIAL;
        }
        String obrigaInformacoesServidorSolicitacao = "";
        String obrigaInfoBancariasReserva = "";
        String valorFixoPostoCsaSvc = "";
        boolean permiteInclusaoSerBloqSemSenha = false;
        boolean permiteCancelarRenegociacaoDeixandoMargemNegativa = false;

        // Parametros de Serviço para verificar com os de serviço/consignataria
        final ParamSvcTO paramCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        final List<TransferObject> paramTarif = parametroController.selectParamTarifCse(svcCodigo, responsavel);
        final Iterator<TransferObject> itTarif = paramTarif.iterator();
        CustomTransferObject tarifa = null;
        while (itTarif.hasNext()) {
            tarifa = (CustomTransferObject) itTarif.next();
            if (CodedValues.TPT_VLR_INTERVENIENCIA.equals(tarifa.getAttribute(Columns.TPT_CODIGO)) && (tarifa.getAttribute(Columns.PCV_VLR) != null)) {
                valorIntervenienciaCSE = tarifa.getAttribute(Columns.PCV_VLR).toString();
                valorIntervenienciaRefCSE = tarifa.getAttribute(Columns.PCV_FORMA_CALC).toString();
            }
        }

        final String diasDesblAutReservaCSE = paramCse.getTpsDiasDesblResNaoConf();
        carenciaMinimaCSE = paramCse.getTpsCarenciaMinima();
        carenciaMaximaCSE = paramCse.getTpsCarenciaMaxima();
        incluiIofCSE = (paramCse.getTpsSomaIofSimulacaoReserva() != null);
        cseReimplantacaoAut = paramCse.isTpsReimplantacaoAutomatica();
        csePreservaPrdRejeitada = paramCse.isTpsPreservaPrdRejeitadaReimpl();
        cseConcluiNaoPagas = paramCse.isTpsConcluiAdeNaoPaga();
        cseDeveContarLimiteRanking = paramCse.isTpsCsaDeveContarParaLimiteRanking();
        serSenhaObrigatoriaCse = paramCse.isTpsSerSenhaObrigatoriaCsa();
        tpsPermiteRepetirIndiceCse = (paramCse.isTpsPermiteRepetirIndiceCsa()) ? "S" : "N";
        csePermitePrazoMaior = (paramCse.isTpsPermiteContratoSuperSerCsa()) ? "S" : "N";
        percMargemSimuladorCSE = paramCse.getTpsPercMargemSimulador();
        percMargemFolhaLimiteCsaCSE = paramCse.getTpsPercentualMargemFolhaLimiteCsa();
        idadeMinimaCSE = paramCse.getTpsIdadeMinimaSerSolicSimulacao();
        idadeMaximaCSE = paramCse.getTpsIdadeMaximaSerSolicSimulacao();
        final boolean adeIdentificadorObrigatorioCse = paramCse.isTpsIdentificadorAdeObrigatorio();
        boolean adeIdentificadorObrigatorioCsa = paramCse.isTpsIdentificadorAdeObrigatorio();
        periodoLimiteAdeDuplicidadeCSE = paramCse.getTpsPeriodoLimiteAdeDuplicidade();
        vlrMinimoContratoCSE = paramCse.getTpsVlrMinimoContrato();
        vlrMaximoContratoCSE = paramCse.getTpsVlrMaximoContrato();

        boolean CSE_TPS_DIAS_DESBL_AUT_RESERVA = false;
        boolean CSE_TPS_CARENCIA_MINIMA = false, CSE_TPS_CARENCIA_MAXIMA = false, CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR = false, CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA = false;
        boolean CSE_TPS_VLR_INTERVENIENCIA = false, CSE_TPS_REIMPLANTACAO_AUTOMATICA = false, CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL = false;
        boolean CSE_TPS_PERMITE_REPETIR_INDICE_CSA = false, CSE_TPS_CONCLUI_ADE_NAO_PAGA = false, CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA = false;
        boolean CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING = false, CSE_TPS_SER_SENHA_OBRIGATORIA_CSA = false, CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA = false;
        boolean CSE_TPS_IDADE_MIN_MAX = false, CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO = false, CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE = false;
        boolean CSE_TPS_VLR_MINIMO_CONTRATO = false, CSE_TPS_VLR_MAXIMO_CONTRATO = false;

        // Parametro que permite fazer contrato com prazo superior ao contrato do servidor ao orgão:
        // Define a variavel que irá armazenar o valor já presente do parametro no banco de dados
        String bancoSaldoDevedor = "";
        String agenciaSaldoDevedor = "";
        String contaSaldoDevedor = "";
        String nomeFavorecidoSaldoDevedor = "";
        String cnpjSaldoDevedor = "";
        String destinatariosEmailsCompra = "";

        // Parâmetros para cadastro dos emails de notificação dos eventos de compra de contratos:
        // Define a variavel que irá armazenar o valor já presente do parametro no banco de dados
        String emailInfContratosComprados = "";
        String emailInfSaldoDevedor = "";
        String emailInfAprSaldoDevedor = "";
        String emailInfPgtSaldoDevedor = "";
        String emailInfLiqContratoComprado = "";
        // E-mail para recebimeto de solicitações de saldo devedor
        String emailInfSolicitacaoSaldoDevedor = "";
        //E-mail para recebimeto de notificação de novo leilão
        String emailInfNovoLeilao = "";
        String emailInfAlterCodVerbaConvenioCsa = "";
        String emailNotifCsaSerFezCancelouSolicitacao = "";
        String dataExpiracaoCnv = "";
        String numeroContratoCnv = "";
        String mascaraNumeroContratoBeneficio = "";
        //Mensagem a ser enviada ao servidor/funcionário após o deferimento de uma solicitação
        String mensagemSerSolDeferida = "";
        String exigeConfirmacaoRenegociacao = "";
        String msgExibirSolicitacaoServidorOfertaOutroSvc = "";
        String permiteCadastroSaldoDevedor = "";
        boolean prazoLimitadoDataAdmissaoRse = false;

        try {
            // Pega os valores gravados na base de dados.
            final List<TransferObject> parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            final Iterator<TransferObject> it2 = parametros.iterator();
            CustomTransferObject next = null;
            String pscVlr = null;
            while (it2.hasNext()) {
                next = (CustomTransferObject) it2.next();
                pscVlr = next.getAttribute(Columns.PSC_VLR) != null ? next.getAttribute(Columns.PSC_VLR).toString() : "";

                if (CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    diasDesblAutReserva = pscVlr;
                    CSE_TPS_DIAS_DESBL_AUT_RESERVA = true;
                } else if (CodedValues.TPS_CARENCIA_MINIMA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    carenciaMinima = pscVlr;
                    CSE_TPS_CARENCIA_MINIMA = true;
                } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    carenciaMaxima = pscVlr;
                    CSE_TPS_CARENCIA_MAXIMA = true;
                } else if (CodedValues.TPS_VLR_LIBERADO_MINIMO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    vlrLiberadoMinimo = pscVlr;
                } else if (CodedValues.TPS_VLR_LIBERADO_MAXIMO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    vlrLiberadoMaximo = pscVlr;
                } else if (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    idadeMinima = pscVlr;
                    idadeMaxima = next.getAttribute(Columns.PSC_VLR_REF).toString();
                    CSE_TPS_IDADE_MIN_MAX = true;
                } else if (CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    percMargemSimulador = pscVlr;
                    CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR = true;
                } else if (CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    percMargemFolhaLimiteCsa = pscVlr;
                    CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA = true;
                } else if (CodedValues.TPS_VLR_INTERVENIENCIA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    valorInterveniencia = pscVlr;
                    valorIntervenienciaRef = TextHelper.isNull(next.getAttribute(Columns.PSC_VLR_REF)) ? "1" : next.getAttribute(Columns.PSC_VLR_REF).toString();
                    if (!"".equals(valorInterveniencia)) {
                        CSE_TPS_VLR_INTERVENIENCIA = true;
                    }
                } else if (CodedValues.TPS_REIMPLANTACAO_AUTOMATICA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaReimplantacaoAut = (TextHelper.isNull(pscVlr) ? tpcDefaultReimplante : "S".equals(pscVlr));
                    CSE_TPS_REIMPLANTACAO_AUTOMATICA = true;
                } else if (CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaPreservaPrdRejeitada = (TextHelper.isNull(pscVlr) ? tpcDefaultPreservacao : "S".equals(pscVlr));
                    CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL = true;
                } else if (CodedValues.TPS_INDICE.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    indice = pscVlr;
                } else if (CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    if (!TextHelper.isNull(pscVlr)) {
                        tpsPermiteRepetirIndiceCsa = pscVlr;
                    } else {
                        tpsPermiteRepetirIndiceCsa = CodedValues.TPC_SIM;
                    }
                    CSE_TPS_PERMITE_REPETIR_INDICE_CSA = true;
                } else if (CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaPermitePrazoMaior = pscVlr;
                    CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA = true;
                } else if (CodedValues.TPS_CONCLUI_ADE_NAO_PAGA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaConcluiNaoPagas = (TextHelper.isNull(pscVlr) ? tpcDefaultConclusao : "S".equals(pscVlr));
                    CSE_TPS_CONCLUI_ADE_NAO_PAGA = true;
                } else if (CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    concluiAdeSerExcluido = (TextHelper.isNull(pscVlr) ? tpcDefaultConclusao : "S".equals(pscVlr));
                } else if (CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaDeveContarLimiteRanking = (TextHelper.isNull(pscVlr) || !"N".equals(pscVlr));
                    CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING = true;
                } else if (CodedValues.TPS_CNV_PODE_DEFERIR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    cnvDefere = (TextHelper.isNull(pscVlr) ? "S" : pscVlr);
                } else if (CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    serSenhaObrigatoriaCsa = (!TextHelper.isNull(pscVlr) ? !"N".equals(pscVlr) : serSenhaObrigatoriaCsa);
                    CSE_TPS_SER_SENHA_OBRIGATORIA_CSA = true;
                } else if (CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    csaExigeServidorCorrentista = (!TextHelper.isNull(pscVlr) ? "S".equals(pscVlr) : csaExigeServidorCorrentista);
                } else if (CodedValues.TPS_EXIBE_BOLETO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    tpsExibeBoleto = (!TextHelper.isNull(pscVlr) ? "S".equals(pscVlr) : tpsExibeBoleto);
                } else if (CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    permiteValorNegativo = (!TextHelper.isNull(pscVlr) && "S".equals(pscVlr));
                } else if (CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    exigeAssinaturaDigital = (!TextHelper.isNull(pscVlr) && "S".equals(pscVlr));
                } else if (!temCET && CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    incluiIof = (!TextHelper.isNull(pscVlr) ? "S".equals(pscVlr) : calculaTacIofCse);
                    CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA = true;
                } else if (CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    bancoSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    agenciaSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    contaSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    nomeFavorecidoSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    cnpjSaldoDevedor = pscVlr;

                    // Parâmetros para cadastro dos emails de notificação dos eventos de compra de contratos:
                    // Obtém o valor do parametro no banco de dados para exibição no campo html
                } else if (CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfContratosComprados = pscVlr;
                } else if (CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfAprSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfPgtSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfLiqContratoComprado = pscVlr;
                } else if (CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfSolicitacaoSaldoDevedor = pscVlr;
                } else if (CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfNovoLeilao = pscVlr;
                } else if (CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailInfAlterCodVerbaConvenioCsa = pscVlr;
                } else if (CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    limite_aumento_valor_ade = pscVlr;
                    limite_aumento_valor_ade_ref = (next.getAttribute(Columns.PSC_VLR_REF) == null ? "" : next.getAttribute(Columns.PSC_VLR_REF).toString());
                } else if (CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    destinatariosEmailsCompra = pscVlr;
                } else if (CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    adeIdentificadorObrigatorioCsa = (!TextHelper.isNull(pscVlr) && "S".equals(pscVlr));
                    CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO = true;
                } else if (CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    maxPrazoRenegociacaoPeriodo = pscVlr;
                } else if (CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    minPrazoRenegociacaoPeriodo = pscVlr;
                } else if (CodedValues.TPS_DATA_EXPIRACAO_CONVENIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    dataExpiracaoCnv = pscVlr;
                } else if (CodedValues.TPS_NUMERO_CONTRATO_CONVENIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    numeroContratoCnv = pscVlr;
                } else if (CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    periodoLimiteAdeDuplicidade = pscVlr;
                    CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE = true;
                } else if (CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    mascaraNumeroContratoBeneficio = pscVlr;
                } else if (CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    mensagemSerSolDeferida = pscVlr;
                } else if (CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    emailNotifCsaSerFezCancelouSolicitacao = pscVlr;
                } else if (CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    exibeCsaListagemSolicitacao = !"N".equals(pscVlr);
                } else if (CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    exibirComoValorForaMargem = (!TextHelper.isNull(pscVlr) && "S".equals(pscVlr));
                } else if (CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    exigeConfirmacaoRenegociacao = pscVlr;
                } else if (CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    msgExibirSolicitacaoServidorOfertaOutroSvc = pscVlr;
                } else if (CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    bloqueiaInclusaoLoteRseTipo = (!TextHelper.isNull(pscVlr) ? "S".equals(pscVlr) : bloqueiaInclusaoLoteRseTipo);
                } else if (CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    exigeSenhaServidorViaLote = (!TextHelper.isNull(pscVlr) ? "S".equals(pscVlr) : exigeSenhaServidorViaLote);
                } else if (CodedValues.TPS_RELEVANCIA_CSA_RANKING.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    relevanciaCsaRanking = (!TextHelper.isNull(pscVlr) ? pscVlr : relevanciaCsaRanking);
                } else if (CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarIdPropostaConvenio = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarIdPropostaConvenio);
                } else if (CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarDiaVencimentoContrato = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarDiaVencimentoContrato);
                } else if (CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarPeriodoCompetenciaDebito = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarPeriodoCompetenciaDebito);
                } else if (CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarNumeroConvenio = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarNumeroConvenio);
                } else if (CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarCodigoAdesao = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarCodigoAdesao);
                } else if (CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    configurarIdadeMaximaContratacaoSeguro = (!TextHelper.isNull(pscVlr) ? pscVlr : configurarIdadeMaximaContratacaoSeguro);
                } else if (CodedValues.TPS_FORMA_NUMERACAO_PARCELAS.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    formaNumeracaoParcelas = pscVlr;
                } else if (CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    obrigaInformacoesServidorSolicitacao = pscVlr;
                } else if (CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    obrigaInfoBancariasReserva = pscVlr;
                } else if (CodedValues.TPS_VALOR_SVC_FIXO_POSTO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    valorFixoPostoCsaSvc = pscVlr;
                } else if (CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    permiteInclusaoSerBloqSemSenha = (!TextHelper.isNull(pscVlr) ? "1".equals(pscVlr) : permiteInclusaoSerBloqSemSenha);
                } else if (CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    permiteCadastroSaldoDevedor = (!TextHelper.isNull(pscVlr) ? pscVlr : "");
                } else if (CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    permiteCancelarRenegociacaoDeixandoMargemNegativa = (!TextHelper.isNull(pscVlr) ? "1".equals(pscVlr) : permiteCancelarRenegociacaoDeixandoMargemNegativa);
                } else if (CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    prazoLimitadoDataAdmissaoRse = (!TextHelper.isNull(pscVlr) ? CodedValues.PSC_BOOLEANO_SIM.equals(pscVlr) : prazoLimitadoDataAdmissaoRse);
                } else if (CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    vlrMinimoContrato = pscVlr;
                    CSE_TPS_VLR_MINIMO_CONTRATO = true;
                } else if (CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    vlrMaximoContrato = pscVlr;
                    CSE_TPS_VLR_MAXIMO_CONTRATO = true;
                }
            }
            if (tpcReimplantacaoAutomatica && (!tpcCsaEscolheReimpl || (tpcCsaEscolheReimpl && csaReimplantacaoAut))) {
                reimplanta = true;
            }
            if (reimplanta && tpcPreservaPrdRejeitada && (!tpcCsaEscolhePrdRejeitada || (tpcCsaEscolhePrdRejeitada && csaPreservaPrdRejeitada))) {
                preserva = true;
            }
            if (!reimplanta && tpcConcluiNaoPagas && (!tpcCsaEscolheConclusao || (tpcCsaEscolheConclusao && csaConcluiNaoPagas))) {
                concluiNaoPagas = true;
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);
        model.addAttribute("csa_codigo", csaCodigo);
        model.addAttribute("csa_nome", csaNome);
        model.addAttribute("temCET", temCET);
        model.addAttribute("cnv_codigo", cnvCodigo);
        model.addAttribute("svc_codigo", svcCodigo);
        model.addAttribute("svc_identificador", svcIdentificador);
        model.addAttribute("svc_descricao", svcDescricao);
        model.addAttribute("nse_codigo", nseCodigo);
        model.addAttribute("permiteCadIndice", permiteCadIndice);
        model.addAttribute("podeRepetirIndice", podeRepetirIndice);
        model.addAttribute("temModuloCompra", temModuloCompra);
        model.addAttribute("temEtapaAprovacaoSaldo", temEtapaAprovacaoSaldo);
        model.addAttribute("temSimulacaoConsignacao", temSimulacaoConsignacao);
        model.addAttribute("permiteCsaLimitarMargem", permiteCsaLimitarMargem);
        model.addAttribute("tpcReimplantacaoAutomatica", tpcReimplantacaoAutomatica);
        model.addAttribute("tpcCsaEscolheReimpl", tpcCsaEscolheReimpl);
        model.addAttribute("tpcPreservaPrdRejeitada", tpcPreservaPrdRejeitada);
        model.addAttribute("tpcCsaEscolhePrdRejeitada", tpcCsaEscolhePrdRejeitada);
        model.addAttribute("tpcConcluiNaoPagas", tpcConcluiNaoPagas);
        model.addAttribute("tpcCsaEscolheConclusao", tpcCsaEscolheConclusao);
        model.addAttribute("parametrosSvc", parametrosSvc);
        model.addAttribute("carenciaMaxCse", carenciaMaxCse);
        model.addAttribute("serSenhaObrigatoriaCsa", serSenhaObrigatoriaCsa);
        model.addAttribute("diasDesblAutReserva", diasDesblAutReserva);
        model.addAttribute("diasDesblAutReservaCSE", diasDesblAutReservaCSE);
        model.addAttribute("carenciaMinima", carenciaMinima);
        model.addAttribute("carenciaMaxima", carenciaMaxima);
        model.addAttribute("carenciaMinimaCSE", carenciaMinimaCSE);
        model.addAttribute("carenciaMaximaCSE", carenciaMaximaCSE);
        model.addAttribute("vlrLiberadoMinimo", vlrLiberadoMinimo);
        model.addAttribute("vlrLiberadoMaximo", vlrLiberadoMaximo);
        model.addAttribute("idadeMaxima", idadeMaxima);
        model.addAttribute("idadeMinima", idadeMinima);
        model.addAttribute("idadeMaximaCSE", idadeMaximaCSE);
        model.addAttribute("idadeMinimaCSE", idadeMinimaCSE);
        model.addAttribute("percMargemSimulador", percMargemSimulador);
        model.addAttribute("percMargemSimuladorCSE", percMargemSimuladorCSE);
        model.addAttribute("percMargemFolhaLimiteCsa", percMargemFolhaLimiteCsa);
        model.addAttribute("percMargemFolhaLimiteCsaCSE", percMargemFolhaLimiteCsaCSE);
        model.addAttribute("indice", indice);
        model.addAttribute("valorInterveniencia", valorInterveniencia);
        model.addAttribute("valorIntervenienciaRef", valorIntervenienciaRef);
        model.addAttribute("valorIntervenienciaCSE", valorIntervenienciaCSE);
        model.addAttribute("valorIntervenienciaRefCSE", valorIntervenienciaRefCSE);
        model.addAttribute("limite_aumento_valor_ade", limite_aumento_valor_ade);
        model.addAttribute("limite_aumento_valor_ade_ref", limite_aumento_valor_ade_ref);
        model.addAttribute("incluiIof", incluiIof);
        model.addAttribute("incluiIofCSE", incluiIofCSE);
        model.addAttribute("cseReimplantacaoAut", cseReimplantacaoAut);
        model.addAttribute("csePreservaPrdRejeitada", csePreservaPrdRejeitada);
        model.addAttribute("cseConcluiNaoPagas", cseConcluiNaoPagas);
        model.addAttribute("reimplanta", reimplanta);
        model.addAttribute("preserva", preserva);
        model.addAttribute("concluiNaoPagas", concluiNaoPagas);
        model.addAttribute("concluiAdeSerExcluido", concluiAdeSerExcluido);
        model.addAttribute("csaDeveContarLimiteRanking", csaDeveContarLimiteRanking);
        model.addAttribute("cseDeveContarLimiteRanking", cseDeveContarLimiteRanking);
        model.addAttribute("serSenhaObrigatoriaCse", serSenhaObrigatoriaCse);
        model.addAttribute("csaExigeServidorCorrentista", csaExigeServidorCorrentista);
        model.addAttribute("tpsExibeBoleto", tpsExibeBoleto);
        model.addAttribute("permiteValorNegativo", permiteValorNegativo);
        model.addAttribute("exigeAssinaturaDigital", exigeAssinaturaDigital);
        model.addAttribute("adeIdentificadorObrigatorioCse", adeIdentificadorObrigatorioCse);
        model.addAttribute("adeIdentificadorObrigatorioCsa", adeIdentificadorObrigatorioCsa);
        model.addAttribute("exibeCsaListagemSolicitacao", exibeCsaListagemSolicitacao);
        model.addAttribute("exibirComoValorForaMargem", exibirComoValorForaMargem);
        model.addAttribute("tpsPermiteRepetirIndiceCsa", tpsPermiteRepetirIndiceCsa);
        model.addAttribute("tpsPermiteRepetirIndiceCse", tpsPermiteRepetirIndiceCse);
        model.addAttribute("csaPermitePrazoMaior", csaPermitePrazoMaior);
        model.addAttribute("csePermitePrazoMaior", csePermitePrazoMaior);
        model.addAttribute("cnvDefere", cnvDefere);
        model.addAttribute("maxPrazoRenegociacaoPeriodo", maxPrazoRenegociacaoPeriodo);
        model.addAttribute("minPrazoRenegociacaoPeriodo", minPrazoRenegociacaoPeriodo);
        model.addAttribute("periodoLimiteAdeDuplicidade", periodoLimiteAdeDuplicidade);
        model.addAttribute("periodoLimiteAdeDuplicidadeCSE", periodoLimiteAdeDuplicidadeCSE);
        model.addAttribute("CSE_TPS_DIAS_DESBL_AUT_RESERVA", CSE_TPS_DIAS_DESBL_AUT_RESERVA);
        model.addAttribute("CSE_TPS_CARENCIA_MINIMA", CSE_TPS_CARENCIA_MINIMA);
        model.addAttribute("CSE_TPS_CARENCIA_MAXIMA", CSE_TPS_CARENCIA_MAXIMA);        
        model.addAttribute("CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR", CSE_TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR);
        model.addAttribute("CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA", CSE_TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA);
        model.addAttribute("CSE_TPS_VLR_INTERVENIENCIA", CSE_TPS_VLR_INTERVENIENCIA);
        model.addAttribute("CSE_TPS_REIMPLANTACAO_AUTOMATICA", CSE_TPS_REIMPLANTACAO_AUTOMATICA);
        model.addAttribute("CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL", CSE_TPS_PRESERVA_PRD_REJEITADA_REIMPL);
        model.addAttribute("CSE_TPS_PERMITE_REPETIR_INDICE_CSA", CSE_TPS_PERMITE_REPETIR_INDICE_CSA);
        model.addAttribute("CSE_TPS_CONCLUI_ADE_NAO_PAGA", CSE_TPS_CONCLUI_ADE_NAO_PAGA);
        model.addAttribute("CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA", CSE_TPS_SOMA_IOF_SIMULACAO_RESERVA);
        model.addAttribute("CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING", CSE_TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING);
        model.addAttribute("CSE_TPS_SER_SENHA_OBRIGATORIA_CSA", CSE_TPS_SER_SENHA_OBRIGATORIA_CSA);
        model.addAttribute("CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA", CSE_TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
        model.addAttribute("CSE_TPS_IDADE_MIN_MAX", CSE_TPS_IDADE_MIN_MAX);
        model.addAttribute("CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO", CSE_TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        model.addAttribute("CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE", CSE_TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE);
        model.addAttribute("CSE_TPS_VLR_MINIMO_CONTRATO", CSE_TPS_VLR_MINIMO_CONTRATO);
        model.addAttribute("CSE_TPS_VLR_MAXIMO_CONTRATO", CSE_TPS_VLR_MAXIMO_CONTRATO);
        model.addAttribute("bancoSaldoDevedor", bancoSaldoDevedor);
        model.addAttribute("agenciaSaldoDevedor", agenciaSaldoDevedor);
        model.addAttribute("contaSaldoDevedor", contaSaldoDevedor);
        model.addAttribute("nomeFavorecidoSaldoDevedor", nomeFavorecidoSaldoDevedor);
        model.addAttribute("cnpjSaldoDevedor", cnpjSaldoDevedor);
        model.addAttribute("destinatariosEmailsCompra", destinatariosEmailsCompra);
        model.addAttribute("emailInfContratosComprados", emailInfContratosComprados);
        model.addAttribute("emailInfSaldoDevedor", emailInfSaldoDevedor);
        model.addAttribute("emailInfAprSaldoDevedor", emailInfAprSaldoDevedor);
        model.addAttribute("emailInfPgtSaldoDevedor", emailInfPgtSaldoDevedor);
        model.addAttribute("emailInfLiqContratoComprado", emailInfLiqContratoComprado);
        model.addAttribute("emailInfSolicitacaoSaldoDevedor", emailInfSolicitacaoSaldoDevedor);
        model.addAttribute("emailInfNovoLeilao", emailInfNovoLeilao);
        model.addAttribute("emailInfAlterCodVerbaConvenioCsa", emailInfAlterCodVerbaConvenioCsa);
        model.addAttribute("emailNotifCsaSerFezCancelouSolicitacao", emailNotifCsaSerFezCancelouSolicitacao);
        model.addAttribute("dataExpiracaoCnv", dataExpiracaoCnv);
        model.addAttribute("numeroContratoCnv", numeroContratoCnv);
        model.addAttribute("mascaraNumeroContratoBeneficio", mascaraNumeroContratoBeneficio);
        model.addAttribute("mensagemSerSolDeferida", mensagemSerSolDeferida);
        model.addAttribute("prazos", prazos);
        model.addAttribute("indiceNumerico", indiceNumerico);
        model.addAttribute("limiteIndice", limiteIndice);
        model.addAttribute("exigeConfirmacaoRenegociacao", exigeConfirmacaoRenegociacao);
        model.addAttribute("msgExibirSolicitacaoServidorOfertaOutroSvc", msgExibirSolicitacaoServidorOfertaOutroSvc);
        model.addAttribute("bloqueiaInclusaoLoteRseTipo", bloqueiaInclusaoLoteRseTipo);
        model.addAttribute("exigeSenhaServidorViaLote", exigeSenhaServidorViaLote);
        model.addAttribute("relevanciaCsaRanking", relevanciaCsaRanking);
        model.addAttribute("configurarIdPropostaConvenio", configurarIdPropostaConvenio);
        model.addAttribute("configurarDiaVencimentoContrato", configurarDiaVencimentoContrato);
        model.addAttribute("configurarPeriodoCompetenciaDebito", configurarPeriodoCompetenciaDebito);
        model.addAttribute("configurarNumeroConvenio", configurarNumeroConvenio);
        model.addAttribute("configurarCodigoAdesao", configurarCodigoAdesao);
        model.addAttribute("configurarIdadeMaximaContratacaoSeguro", configurarIdadeMaximaContratacaoSeguro);
        model.addAttribute("formaNumeracaoParcelas", formaNumeracaoParcelas);
        model.addAttribute("formaNumeracaoParcelasPadrao", formaNumeracaoParcelasPadrao);
        model.addAttribute("obrigaInformacoesServidorSolicitacao", obrigaInformacoesServidorSolicitacao);
        model.addAttribute("obrigaInfoBancariasReserva", obrigaInfoBancariasReserva);
        model.addAttribute("valorFixoPostoCsaSvc", valorFixoPostoCsaSvc);
        model.addAttribute("permiteInclusaoSerBloqSemSenha", permiteInclusaoSerBloqSemSenha);
        model.addAttribute("permiteCadastroSaldoDevedor", permiteCadastroSaldoDevedor);
        model.addAttribute("permiteCancelarRenegociacaoDeixandoMargemNegativa", permiteCancelarRenegociacaoDeixandoMargemNegativa);
        model.addAttribute("prazoLimitadoDataAdmissaoRse", prazoLimitadoDataAdmissaoRse);
        model.addAttribute("vlrMinimoContrato", vlrMinimoContrato);
        model.addAttribute("vlrMaximoContrato", vlrMaximoContrato);
        model.addAttribute("vlrMinimoContratoCSE", vlrMinimoContratoCSE);
        model.addAttribute("vlrMaximoContratoCSE", vlrMaximoContratoCSE);

        return viewRedirect("jsp/manterConsignataria/editarServico", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=salvarServico"})
    @SuppressWarnings("java:S2441")
    public String salvarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";

        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCseSup()) {
            csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
        final String csaNome = JspHelper.verificaVarQryStr(request, "csa_nome");
        final String nseCodigo = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");

        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "svc");

        // Verifica os parâmetros de sistema
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);

        // Busca os tipos de parâmetro de serviço disponíveis.
        final List<TransferObject> tiposParams = parametroController.lstTipoParamSvc(responsavel);
        final HashMap<Object, Boolean> parametrosSvc = new HashMap<>();
        final Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            final CustomTransferObject paramSvc = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_CSE_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA))));
            } else if (responsavel.isSup()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_SUP_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA))));
            } else if (responsavel.isCsa()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_CSA_ALTERA) != null) && CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_CSA_ALTERA))));
            }
        }
        final List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvc.containsKey(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF) && parametrosSvc.get(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MINIMA) && parametrosSvc.get(CodedValues.TPS_CARENCIA_MINIMA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CARENCIA_MAXIMA) && parametrosSvc.get(CodedValues.TPS_CARENCIA_MAXIMA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_LIBERADO_MINIMO) && parametrosSvc.get(CodedValues.TPS_VLR_LIBERADO_MINIMO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MINIMO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_LIBERADO_MAXIMO) && parametrosSvc.get(CodedValues.TPS_VLR_LIBERADO_MAXIMO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_LIBERADO_MAXIMO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO) && parametrosSvc.get(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR) && parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA) && parametrosSvc.get(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_INTERVENIENCIA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_INDICE) && parametrosSvc.get(CodedValues.TPS_INDICE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_INDICE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA) && parametrosSvc.get(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_REIMPLANTACAO_AUTOMATICA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL) && parametrosSvc.get(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA) && parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO) && parametrosSvc.get(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO);
        }
        if (permiteCadIndice && (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA) && parametrosSvc.get(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA).booleanValue())) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA) && parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CNV_PODE_DEFERIR) && parametrosSvc.get(CodedValues.TPS_CNV_PODE_DEFERIR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CNV_PODE_DEFERIR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA) && parametrosSvc.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
        }
        if (!temCET && parametrosSvc.containsKey(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA) && parametrosSvc.get(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE) && parametrosSvc.get(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING) && parametrosSvc.get(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING);
        }

        // Parametro que permite fazer contrato com prazo superior ao contrato do servidor ao orgão:
        // Inclui o código do parâmetro na lista dos parametros que serão salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV) && parametrosSvc.get(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV) && parametrosSvc.get(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA) && parametrosSvc.get(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA);
        }
        // Parâmetros para cadastro dos emails de notificação dos eventos de compra de contratos:
        // Inclui o código do parâmetro na lista dos parametros que serão salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA) && parametrosSvc.get(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_BOLETO) && parametrosSvc.get(CodedValues.TPS_EXIBE_BOLETO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIBE_BOLETO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO) && parametrosSvc.get(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_VALOR_NEGATIVO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO) && parametrosSvc.get(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO) && parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA) && parametrosSvc.get(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES) && parametrosSvc.get(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO) && parametrosSvc.get(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO) && parametrosSvc.get(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_NUMERO_CONTRATO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE) && parametrosSvc.get(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO) && parametrosSvc.get(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO);
        }

        //Parâmetro para envio de mensagem ao servidor/funcionário após o deferimento de uma solicitação
        //Inclui parâmetro na lista de parâmetros a serem salvos
        if (parametrosSvc.containsKey(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA) && parametrosSvc.get(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA);
        }
        //DESENV-10999
        if (parametrosSvc.containsKey(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR) && parametrosSvc.get(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO) && parametrosSvc.get(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO);
        }
        // DESENV-11865
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM) && parametrosSvc.get(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM).booleanValue() && responsavel.isCseSup()) {
            tpsCodigos.add(CodedValues.TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM);
        }
        // DESENV-14162
        if (parametrosSvc.containsKey(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO) && parametrosSvc.get(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO);
        }
        // DESENV-14336 : Parâmetro para oferecer contratação de seguro prestamista ao solicitar empréstimo
        if (parametrosSvc.containsKey(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC) && parametrosSvc.get(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC);
        }
        // DESENV-14405 : Parâmetro bloqueia inclusão de contratos via lote de servidores de acordo com rse_tipo cadastrado.
        if (parametrosSvc.containsKey(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO) && parametrosSvc.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO);
        }
        // DESENV-17552 : Parametro Serviço verifica se exige senha do servidor via lote
        if (parametrosSvc.containsKey(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE) && parametrosSvc.get(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE);
        }
        // DESENV-15679 : Ofertas patrocinadas na página de ranking de empréstimo.
        if (parametrosSvc.containsKey(CodedValues.TPS_RELEVANCIA_CSA_RANKING) && parametrosSvc.get(CodedValues.TPS_RELEVANCIA_CSA_RANKING).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_RELEVANCIA_CSA_RANKING);
        }
        // DESENV-16329 : Projeto MAG - eConsig - Criar novo REST para retornar parâmetros MAG
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_NUMERO_CONVENIO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_CODIGO_ADESAO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO) && parametrosSvc.get(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO);
        }

        // DESENV-16871 : Parâmetro sobre forma de numeração das parcelas
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS, CodedValues.TPC_SIM, responsavel) &&
                parametrosSvc.containsKey(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS) &&
                parametrosSvc.get(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_FORMA_NUMERACAO_PARCELAS);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO) && parametrosSvc.get(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);
        }
        //DESENV-17581
        if (parametrosSvc.containsKey(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA) && parametrosSvc.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA);
        }
        // DESENV-17057
        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA) && parametrosSvc.get(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_VALOR_SVC_FIXO_POSTO) && parametrosSvc.get(CodedValues.TPS_VALOR_SVC_FIXO_POSTO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VALOR_SVC_FIXO_POSTO);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR) && parametrosSvc.get(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA) && parametrosSvc.get(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA);
        }

        if (parametrosSvc.containsKey(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR) && parametrosSvc.get(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MINIMO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_VLR_MINIMO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
        }
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_MAXIMO_CONTRATO) && parametrosSvc.get(CodedValues.TPS_VLR_MAXIMO_CONTRATO).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
        }

        String permanecerNaPaginaEditarServico = "../v3/manterConsignataria?acao=editarServico&svc=" + svcCodigo + "&SVC_IDENTIFICADOR=" + svcIdentificador + "&SVC_DESCRICAO=" + svcDescricao + "&csa_codigo=" + csaCodigo + "&csa_nome=" + csaNome;
        if (!"".equals(svcCodigo)) {
            // Salva os parâmetros do serviço

            try {
                final List<TransferObject> parametros = setParametros(tpsCodigos, svcCodigo, csaCodigo, request, session);

                if (responsavel.isCseSupOrg() || responsavel.isCsa()) {
                    final Map<String, List<TransferObject>> servicosParamCsa = new HashMap<>();
                    final List<TransferObject> servicosNatureza = convenioController.lstSvcCnvAtivos(nseCodigo, csaCodigo, true, responsavel);
                    final List<String> svcCodigos = new ArrayList<>();
                    for (final TransferObject servico : servicosNatureza) {
                        svcCodigos.add((String) servico.getAttribute(Columns.SVC_CODIGO));
                    }

                    for (final TransferObject parametro : parametros) {
                        final String tpsCodigo = (String) parametro.getAttribute(Columns.TPS_CODIGO);
                        final String pscVlr = (String) parametro.getAttribute(Columns.PSC_VLR);
                        final String pscVlrRef = (String) parametro.getAttribute(Columns.PSC_VLR_REF);

                        final List<String> tpsCodigosSvc = new ArrayList<>();
                        tpsCodigosSvc.add(tpsCodigo);

                        final List<String> csaCodigos = new ArrayList<>();
                        csaCodigos.add(csaCodigo);

                        final List<TransferObject> listaParamSvcCsa = parametroController.selectParamSvcCsaDiferente(svcCodigos, csaCodigos, tpsCodigosSvc, pscVlr, pscVlrRef, false, responsavel);

                        if ((listaParamSvcCsa != null) && !listaParamSvcCsa.isEmpty()) {
                            final List<TransferObject> listSvcVlrPadrao = new ArrayList<>();
                            for (final TransferObject paramSvcCsa : listaParamSvcCsa) {
                                paramSvcCsa.setAttribute("pscSvcVlrPadrao", pscVlr);
                                paramSvcCsa.setAttribute("pscSvcVlrRef", pscVlrRef);
                                paramSvcCsa.setAttribute("tpsCodigo", tpsCodigo);
                                listSvcVlrPadrao.add(paramSvcCsa);
                            }
                            servicosParamCsa.put(tpsCodigo, listSvcVlrPadrao);
                        }
                    }

                    if (!servicosParamCsa.isEmpty()) {
                        model.addAttribute("parametros", parametros);
                        model.addAttribute("servicosParamCsa", servicosParamCsa);
                        model.addAttribute("svc_identificador", svcIdentificador);
                        model.addAttribute("svc_descricao", svcDescricao);
                        model.addAttribute("csa_nome", csaNome);
                        model.addAttribute("svc_codigo", svcCodigo);
                        model.addAttribute("nse_codigo", nseCodigo);
                        model.addAttribute("csa_codigo", csaCodigo);
                        model.addAttribute("tps_codigos", tpsCodigos);

                        permanecerNaPaginaEditarServico = SynchronizerToken.updateTokenInURL(permanecerNaPaginaEditarServico, request);
                        model.addAttribute("permanecerNaPaginaEditarServico", permanecerNaPaginaEditarServico);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
                        return viewRedirect("jsp/manterConsignataria/editarServicoParam", request, session, model, responsavel);
                    }
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(permanecerNaPaginaEditarServico, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = {"acao=editarCnvVincServidor"})
    public String editarCnvVincServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ParametroControllerException, ServicoControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "csa");
        if (TextHelper.isNull(csaCodigo) || (!responsavel.isCsa() && !responsavel.isSup())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");
        final String svc_identificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        final String svc_descricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        final boolean vrsAtivo = true;
        List<TransferObject> vrs = null;
        List<String> cnvVincRse = null;

        try {
            vrs = servidorController.selectVincRegistroServidor(vrsAtivo, responsavel);
            cnvVincRse = servidorController.selectCnvVincRseSer(csaCodigo, svc_codigo, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));

        final boolean paramInforBloqPadrao = (parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel) != null) && CodedValues.TPA_SIM.equals(parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel));
        final List<TransferObject> svcList = servicoController.selectServicosCsa(csaCodigo, responsavel);

        final int total = consignatariaController.countOccBloqDesbloqVinculosByCsa(csaCodigo, responsavel);
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

        final String linkCnvVincServidor = request.getRequestURI() + "?acao=editarCnvVincServidor";
        configurarPaginador(linkCnvVincServidor, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

        final List<TransferObject> historicoBloqDesbloqVinculos = consignatariaController.listaOccBloqDesbloqVinculosByCsa(csaCodigo, offset, size, responsavel);

        model.addAttribute("historicoBloqDesbloqVinculos", historicoBloqDesbloqVinculos);
        model.addAttribute("ParamBloqPadrao", paramInforBloqPadrao);
        model.addAttribute("svcList", svcList);
        model.addAttribute("csa_codigo", csaCodigo);
        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("svc_descricao", svc_descricao);
        model.addAttribute("svc_identificador", svc_identificador);
        model.addAttribute("vrs", vrs);
        model.addAttribute("cnvVincRse", cnvVincRse);
        model.addAttribute("voltar", voltar);

        return viewRedirect("jsp/manterConsignataria/editarCnvVincServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=salvarCnvVincServidor"})
    public String salvarCnvVincServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csa_codigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "csa");
        if (TextHelper.isNull(csa_codigo) || (!responsavel.isCsa() && !responsavel.isSup())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");

        final String[] svcsReplica = request.getParameterValues("services");

        try {
            final List<String> servicos = new ArrayList<>();
            final List<String> svcIdentificadores = new ArrayList<>();
            if(svcsReplica != null) {
                for (String str : svcsReplica) {
                    int index = str.indexOf('-');

                    servicos.add(str.substring(0, index));
                    svcIdentificadores.add(str.substring(index + 1));
                }
            }
            final String[] vinculo = request.getParameterValues("vinculo");
            final List<String> vinculos = vinculo != null ? Arrays.asList(vinculo) : new ArrayList<>();
            final Map<String, List<String>> vinculosBloqDesbloq = new HashMap<>();
            vinculosBloqDesbloq.put("bloqueados", new ArrayList<>());
            vinculosBloqDesbloq.put("desbloqueados", new ArrayList<>());

            if (servicos.isEmpty()) {
                servidorController.updateCnvVincRseSer(csa_codigo, svc_codigo, vinculos, vinculosBloqDesbloq, responsavel);
            } else {
                boolean svcPadrao = true;
                for (final String svcCodigo : servicos) {
                    if (svcCodigo.equals(svc_codigo)) {
                        svcPadrao = false;
                    }
                    servidorController.updateCnvVincRseSer(csa_codigo, svcCodigo, vinculos, vinculosBloqDesbloq, responsavel);
                }
                if (svcPadrao) {
                    servidorController.updateCnvVincRseSer(csa_codigo, svc_codigo, vinculos, vinculosBloqDesbloq, responsavel);
                }
            }

            String servicosIdentificadores = String.join("," , svcIdentificadores);
            List<String> occCodigos = new ArrayList<>();
            if(!vinculosBloqDesbloq.get("bloqueados").isEmpty()) {
                String vinculosBloqueados = String.join(",", vinculosBloqDesbloq.get("bloqueados"));
                occCodigos.add(consignatariaController.incluirOcorrenciaConsignataria(csa_codigo, CodedValues.TOC_BLOQUEIO_VINCULO, ApplicationResourcesHelper.getMessage("mensagem.informacao.vinculos.bloqueados", responsavel, vinculosBloqueados, servicosIdentificadores), responsavel));
            }
            if(!vinculosBloqDesbloq.get("desbloqueados").isEmpty()) {
                String vinculosDesbloqueados = String.join(",", vinculosBloqDesbloq.get("desbloqueados"));
                occCodigos.add(consignatariaController.incluirOcorrenciaConsignataria(csa_codigo, CodedValues.TOC_DESBLOQUEIO_VINCULO, ApplicationResourcesHelper.getMessage("mensagem.informacao.vinculos.desbloqueados", responsavel, vinculosDesbloqueados, servicosIdentificadores), responsavel));
            }
            //Enviar email para csa
            consignatariaController.enviarEmailNotificacaoVinculosBloqDesbloq(csa_codigo, occCodigos, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = {"acao=salvarParametrosServicos"})
    public String salvarParametrosServicos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "svc_codigo");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        final String csaNome = JspHelper.verificaVarQryStr(request, "csa_nome");
        final String svcIdentificador = JspHelper.verificaVarQryStr(request, "svc_identificador");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "svc_descricao");

        final String[] svcTpsCodigos = request.getParameterValues("svcCodigos");
        final List<TransferObject> parametrosSvc = new ArrayList<>();

        if (svcTpsCodigos == null) {
            final String permanecerNaPaginaEditarServico = "../v3/manterConsignataria?acao=editarServico&svc=" + svcCodigo + "&SVC_IDENTIFICADOR=" + svcIdentificador + "&SVC_DESCRICAO=" + svcDescricao + "&csa_codigo=" + csaCodigo + "&csa_nome=" + csaNome;
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(permanecerNaPaginaEditarServico, request)));
            return "jsp/redirecionador/redirecionar";
        }

        try {
            for (final String svcTpsCodigo : svcTpsCodigos) {
                final String[] codigo = svcTpsCodigo.split(";");
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.TPS_CODIGO, codigo[1]);
                cto.setAttribute(Columns.PSC_SVC_CODIGO, codigo[0]);
                cto.setAttribute(Columns.PSC_CSA_CODIGO, csaCodigo);
                cto.setAttribute(Columns.PSC_VLR, codigo.length < 3 ? "" : codigo[2]);
                cto.setAttribute(Columns.PSC_VLR_REF, codigo.length < 4 ? "" : codigo[3]);
                parametrosSvc.add(cto);
            }
            parametroController.updateParamSvcCsa(parametrosSvc, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        final String permanecerNaPaginaEditarServico = "../v3/manterConsignataria?acao=editarServico&svc=" + svcCodigo + "&SVC_IDENTIFICADOR=" + svcIdentificador + "&SVC_DESCRICAO=" + svcDescricao + "&csa_codigo=" + csaCodigo + "&csa_nome=" + csaNome;
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(permanecerNaPaginaEditarServico, request)));
        return "jsp/redirecionador/redirecionar";
    }

    private List<TransferObject> setParametros(List<String> tpsCodigos, String svcCodigo, String csaCodigo, HttpServletRequest request, HttpSession session) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean tpcReimplantacaoAutomatica = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, responsavel);
        final boolean tpcPreservaPrdRejeitada = ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);
        final boolean tpcConcluiNaoPagas = ParamSist.paramEquals(CodedValues.TPC_CONCLUI_NAO_PAGAS, CodedValues.TPC_SIM, responsavel);

        final List<TransferObject> parametros = new ArrayList<>();
        final List<TransferObject> tpsCodigosIgualCse = new ArrayList<>();
        try {
            for (final String element : tpsCodigos) {
                final CustomTransferObject cto = new CustomTransferObject();
                String pscVlr = JspHelper.verificaVarQryStr(request, "tps_" + element);
                String pscVlrRef = "";
                if ((CodedValues.TPS_CARENCIA_MINIMA.equals(element) || CodedValues.TPS_CARENCIA_MAXIMA.equals(element)) && !"".equals(pscVlr)) {
                    pscVlr = Integer.valueOf(JspHelper.verificaVarQryStr(request, "tps_" + element)).toString();
                } else if (CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE.equals(element)) {
                    final String[] valores = pscVlr.split(";");
                    pscVlr = "";
                    if (valores.length > 0) {
                        pscVlr = valores[0].replace(',', '.').replace("\'", "\'\'");
                    }
                    if (valores.length > 1) {
                        pscVlrRef = valores[1];
                    }
                } else if (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(element)) {
                    if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tps_" + element + "_ref"))) {
                        pscVlrRef = JspHelper.verificaVarQryStr(request, "tps_" + element + "_ref");
                    }
                } else if (CodedValues.TPS_VLR_INTERVENIENCIA.equals(element)) {
                    if (!"".equals(pscVlr)) {
                        pscVlr = NumberHelper.reformat(pscVlr, NumberHelper.getLang(), "en");
                        pscVlrRef = JspHelper.verificaVarQryStr(request, "tps_" + element + "_REF");
                    }
                } else if (CodedValues.TPS_REIMPLANTACAO_AUTOMATICA.equals(element) && "".equals(pscVlr)) {
                    pscVlr = (tpcReimplantacaoAutomatica) ? "S" : "N";
                } else if (CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL.equals(element) && "".equals(pscVlr)) {
                    pscVlr = (tpcPreservaPrdRejeitada) ? "S" : "N";
                } else if (CodedValues.TPS_CONCLUI_ADE_NAO_PAGA.equals(element) && "".equals(pscVlr)) {
                    pscVlr = (tpcConcluiNaoPagas) ? "S" : "N";
                } else if (CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR.equals(element) && !"".equals(pscVlr)) {
                    pscVlr = Integer.valueOf(JspHelper.verificaVarQryStr(request, "tps_" + element)).toString();
                } else if (CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA.equals(element) && ("null".equals(JspHelper.verificaVarQryStr(request, "csaExigeServidorCorrentista")) && "N".equals(pscVlr))) {
                    continue;
                }

                cto.setAttribute(Columns.TPS_CODIGO, element);
                cto.setAttribute(Columns.PSC_SVC_CODIGO, svcCodigo);
                cto.setAttribute(Columns.PSC_CSA_CODIGO, csaCodigo);
                cto.setAttribute(Columns.PSC_VLR_REF, pscVlrRef);
                cto.setAttribute(Columns.PSC_VLR, !"".equals(pscVlr) ? pscVlr : "");
                parametros.add(cto);

                if ("1".equals(JspHelper.verificaVarQryStr(request, "check_" + element)) || "".equals(cto.getAttribute(Columns.PSC_VLR))) {
                    tpsCodigosIgualCse.add(cto);
                }
            }
            parametroController.updateParamSvcCsa(parametros, responsavel);
            parametroController.deleteParamIgualCse(tpsCodigosIgualCse, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return parametros;
    }

    @RequestMapping(params = {"acao=edtLimiteMargemCsaOrg"})
    public String edtLimiteMargemCsaOrg(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        if (TextHelper.isNull(csaCodigo) || (!responsavel.isCsa() && !responsavel.isSup())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final List<TransferObject> lstLimiteMargemCsaOrg = consignatariaController.lstLimiteMargemCsaOrgByCsaCodigo(csaCodigo, responsavel);
            final List<MargemTO> margens = parametroController.lstMargensIncidentes(null, csaCodigo, null, null, null, responsavel);
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);

            final TransferObject criterios = new CustomTransferObject();
            criterios.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            final List<TransferObject> orgaos = consignanteController.lstOrgaos(criterios, responsavel);

            final ParamSession paramSession = ParamSession.getParamSession(session);
            final String link = request.getAttribute("linkVoltar") !=null ? (String) request.getAttribute("linkVoltar") : paramSession.getLastHistory();

            model.addAttribute("lstLimiteMargemCsaOrg",lstLimiteMargemCsaOrg);
            model.addAttribute("margens",margens);
            model.addAttribute("orgaos",orgaos);
            model.addAttribute("csaCodigo",csa.getCsaCodigo());
            model.addAttribute("csaNome",csa.getCsaNome());
            model.addAttribute("csaIdentificador",csa.getCsaIdentificador());
            model.addAttribute("linkVoltar",link);

            return viewRedirect("jsp/manterConsignataria/editarLimiteMargemCsaOrg", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvarLimiteMargemCsaOrg" })
    public String salvarLimiteMargemCsaOrg(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        if (TextHelper.isNull(csaCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final TransferObject criterios = new CustomTransferObject();
            criterios.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            final List<TransferObject> orgaos = consignanteController.lstOrgaos(criterios, responsavel);
            final List<MargemTO> margens = parametroController.lstMargensIncidentes(null, csaCodigo, null, null, null, responsavel);
            final List<LimiteMargemCsaOrg> lstLimiteMargem = new ArrayList<>();

            for (final TransferObject orgao : orgaos) {
                final String orgCodigo = (String) orgao.getAttribute(Columns.ORG_CODIGO);
                for (final MargemTO margem : margens) {
                    final Short marCodigo = margem.getMarCodigo();
                    final String strLmcValor = JspHelper.verificaVarQryStr(request, "org_" + orgCodigo + "_mar_" + marCodigo);
                    if (!TextHelper.isNull(strLmcValor)) {
                        final LimiteMargemCsaOrg limiteMargem = new LimiteMargemCsaOrg();
                        final BigDecimal lmcValor = new BigDecimal(String.valueOf(NumberHelper.parse(strLmcValor, NumberHelper.getLang()))).multiply(new BigDecimal("0.01"));

                        limiteMargem.setMarCodigo(marCodigo);
                        limiteMargem.setCsaCodigo(csaCodigo);
                        limiteMargem.setOrgCodigo(orgCodigo);
                        limiteMargem.setLmcValor(lmcValor);
                        limiteMargem.setLmcData(DateHelper.getSystemDatetime());

                        lstLimiteMargem.add(limiteMargem);
                    }
                }
            }

            if (!lstLimiteMargem.isEmpty()) {
                consignatariaController.salvarLimiteMargemCsaOrg(lstLimiteMargem, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            request.setAttribute("linkVoltar", request.getParameter("linkVoltar"));
            return "forward:/v3/manterConsignataria?acao=edtLimiteMargemCsaOrg&CSA_CODIGO=" + csaCodigo + "&_skip_history_=true";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=iniciarCredenciamentoCsa"})
    public String iniciarCredenciamentoCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        if (TextHelper.isNull(csaCodigo) || (!responsavel.isCse() && !responsavel.isSup())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
        try {
            final CredenciamentoCsa credenciamentoCsa = consignatariaController.findByCsaCodigoCredenciamentoCsa(csaCodigo, responsavel);
            if ((credenciamentoCsa != null) && !credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.FINALIZADO.getCodigo())) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.em.andamento", responsavel));
                return editarConsignataria(request, response, session, model, csaCodigo);
            }
            EnviaEmailHelper.enviarEmailNotificacaoCsaModuloCredenciamento(csa, responsavel);
            consignatariaController.criarCredenciamentoConsignataria(csaCodigo, StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo(), DateHelper.getSystemDatetime(), null, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.credenciamento.iniciado", responsavel));
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return editarConsignataria(request, response, session, model, csaCodigo);

    }

    @RequestMapping(params = {"acao=editarVlrPostoFixo"})
    public String editarVlrFixoPostoFixo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, ParametroControllerException, PostoRegistroServidorControllerException {
        final ParamSession paramSession = ParamSession.getParamSession(session);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<TransferObject> postos = null;
        List<TransferObject> valorPostoFixo = null;
        boolean exibePostos = false;
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        final String postoRefresh = JspHelper.verificaVarQryStr(request, "POSTO_VOLTA");
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        final String svcSelected = JspHelper.verificaVarQryStr(request, "SVC_SELECTED");
        final List<TransferObject> servicos = parametroController.selectSvcByValorFixo(csaCodigo, responsavel);


        if ("S".equals(postoRefresh)) {
            postos = postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel);
            valorPostoFixo = postoRegistroServidorController.findValorFixoByCsaSvc(svcSelected, csaCodigo, null, responsavel);

            for (final TransferObject ctoPos : postos) {
                for (final TransferObject ctoVlr : valorPostoFixo) {
                    if (ctoPos.getAttribute(Columns.POS_CODIGO).equals(ctoVlr.getAttribute(Columns.PSP_POS_CODIGO))) {
                        ctoPos.setAttribute(Columns.PSP_PPO_VALOR, ctoVlr.getAttribute(Columns.PSP_PPO_VALOR));
                    }
                }
            }

            model.addAttribute("svcSelected", svcSelected);
            exibePostos = true;
        }


        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("servicos", servicos);
        model.addAttribute("svcSelected", svcSelected);
        model.addAttribute("postos", postos);
        model.addAttribute("exibePostos", exibePostos);
        model.addAttribute("btnCancelar", voltar);


        try {
            return viewRedirect("jsp/manterConsignataria/editarVlrFixoPostoCsaSvc", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=salvarValorFixoPosto"})
    public String salvarValorFixoPosto(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws PostoRegistroServidorControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo;
        String svcCodigo;
        try {
            final List<ParamPostoCsaSvc> postoParamVlr = new ArrayList<>();
            final List<TransferObject> postos = postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel);

            csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            for (final TransferObject ctoVlr : postos) {
                final String ctoVlrCampo = (String) ctoVlr.getAttribute(Columns.POS_CODIGO);
                final String vlr = JspHelper.verificaVarQryStr(request, ctoVlrCampo);
                    final ParamPostoCsaSvc paramPostoCsaSvc = new ParamPostoCsaSvc();
                    paramPostoCsaSvc.setPpoVlr(!vlr.isEmpty() && !"0,00".equals(vlr) ? vlr : "0");
                    paramPostoCsaSvc.setCsaCodigo(csaCodigo);
                    paramPostoCsaSvc.setSvcCodigo(svcCodigo);
                    paramPostoCsaSvc.setPosCodigo(ctoVlrCampo);
                    paramPostoCsaSvc.setTpsCodigo(CodedValues.TPS_ADE_VLR);

                    postoParamVlr.add(paramPostoCsaSvc);
            }

            postoRegistroServidorController.saveUpdateVlrFixoPosto(postoParamVlr, svcCodigo, csaCodigo, responsavel);
        } catch (final PostoRegistroServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.argumento", responsavel, ex.getMessage()));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = "acao=uploadDownload")
    public String editarAnexos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String tipo = "consignataria";
        try {
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            final UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if ((usuarioResp != null) && (usuarioResp.getUsuDeficienteVisual() != null) && "S".equals(usuarioResp.getUsuDeficienteVisual())) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            final FileFilter filtro = arq -> {
                final String arqNome = arq.getName().toLowerCase();
                return (arqNome.endsWith(".docx") ||
                        arqNome.endsWith(".jfif") ||
                        arqNome.endsWith(".jpeg") ||
                        arqNome.endsWith(".doc") ||
                        arqNome.endsWith(".rtf") ||
                        arqNome.endsWith(".pdf") ||
                        arqNome.endsWith(".png") ||
                        arqNome.endsWith(".jfi") ||
                        arqNome.endsWith(".jpe") ||
                        arqNome.endsWith(".jpg") ||
                        arqNome.endsWith(".gif"));
            };

            absolutePath += File.separatorChar + tipo + File.separatorChar + csaCodigo;
            final File diretorio = new File(absolutePath);
            if (!diretorio.exists() && !diretorio.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.listar.arquivos.download.rescisao.criacao.diretorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<File> arquivos = null;
            final File[] temp = diretorio.listFiles(filtro);
            if (temp != null) {
                arquivos = new ArrayList<>(Arrays.asList(temp));
            }

            if (arquivos != null) {
                Collections.sort(arquivos, (f1, f2) -> {
                    final Long d1 = f1.lastModified();
                    final Long d2 = f2.lastModified();
                    return d2.compareTo(d1);
                });
            }

            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            final int total = arquivos.size();

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

            final String linkListagem = "../v3/listarArquivosDownloadRescisao?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, requestParams, false, request, model);

            final List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, absolutePath, null, offset, size, responsavel);

            final String btnVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            model.addAttribute("btnVoltar", btnVoltar);
            model.addAttribute("tipo", tipo);
            model.addAttribute("arquivos", arquivosPaginaAtual);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        } catch (final UsuarioControllerException ex){
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            throw new UsuarioControllerException(ex);
        }

        return viewRedirect("jsp/manterConsignataria/editarAnexosConsignataria", request, session, model, responsavel);
    }

}
