package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jodd.util.ArraysUtil;

/**
 * <p>Title: AlterarMultiplasConsignacoesWebController</p>
 * <p>Description: Controlador Web para o caso de uso AlterarMultiplasConsignacoes.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarMultiplasConsignacoes" })
public class AlterarMultiplasConsignacoesWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarMultiplasConsignacoesWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.alterar.multiplo.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/alterarMultiplasConsignacoes");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("exibirFiltroDataInclusao", true);
        model.addAttribute("exibirFiltroSituacaoContrato",true);
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_ANEXO, responsavel));

            model.addAttribute("tipoJusticaObrigatorio", isTipoJusticaObrigatorio(responsavel));
            model.addAttribute("comarcaJusticaObrigatorio", isComarcaJusticaObrigatorio(responsavel));
            model.addAttribute("numeroProcessoObrigatorio", isNumeroProcessoObrigatorio(responsavel));
            model.addAttribute("dataDecisaoObrigatorio", isDataDecisaoObrigatorio(responsavel));
            model.addAttribute("textoDecisaoObrigatorio", isTextoDecisaoObrigatorio(responsavel));
            model.addAttribute("anexoObrigatorio", isAnexoDecisaoObrigatorio(responsavel));

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.permissao.nao.encontrado", responsavel, ex.getMessage());
        }
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_MULTIPLAS_CONSIGNACOES_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = null;

        if (request.getParameterValues("SAD_CODIGO") != null) {
            sadCodigos = Arrays.asList(request.getParameterValues("SAD_CODIGO"));
        } else {
            sadCodigos = new ArrayList<>();
        }

        final ArrayList<String> sadCodigoList = new ArrayList<>();
        if (!TextHelper.isNull(sadCodigos) && !sadCodigos.isEmpty()) {
            sadCodigoList.addAll(sadCodigos);
        } else {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            if (responsavel.isCseSup()) {
                sadCodigos.add(CodedValues.SAD_ESTOQUE);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
            }
        }
        return !sadCodigoList.isEmpty() ? sadCodigoList : sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/alterarMultiplasConsignacoes?acao=iniciarAlteracaoMultiplosAdes&" + SynchronizerToken.generateToken4URL(request);
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.alterar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.alterar", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("ALTERAR_MULTIPLOS_CONTRATOS", CodedValues.FUN_ALTERAR_MULTIPLOS_CONTRATOS, descricao, descricaoCompleta, "editar.gif", "btnAlterarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkAltMultiplos"));

        // Adiciona o editar consignação
        link = "../v3/alterarMultiplasConsignacoes?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "alterar-multiplos");

        final String[] nseCodigos = request.getParameterValues("NSE_CODIGO");
        if (nseCodigos != null && nseCodigos.length > 0 && !TextHelper.isNull(nseCodigos[0])) {
            criterio.setAttribute("nseCodigos", Arrays.asList(nseCodigos));
        }

        final String[] marCodigos = request.getParameterValues("MAR_CODIGO");
        if (marCodigos != null && marCodigos.length > 0 && !TextHelper.isNull(marCodigos[0])) {
            final List<Short> marCodigosShort = Arrays.asList(marCodigos).stream().map(Short::parseShort).collect(Collectors.toList());
            criterio.setAttribute("marCodigos", marCodigosShort);
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

        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel)){
            criterio.setAttribute("FILTRO_DECISAO_JUDICIAL", Boolean.TRUE);
         }

        return criterio;
    }

    @Override
    protected void carregarInformacoesAcessorias(String rseCodigo, String adeNumero, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica se as consignações pertencem ao mesmo registro servidor
        if (request.getAttribute("resultadoMultiplosServidores") != null) {
            throw new AutorizacaoControllerException("mensagem.erro.multiplo.servidor.nao.permitido", responsavel);
        }
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Remove da sessão os atributos usados na operação
        session.removeAttribute("alterarMultiplasConsignacoesWebController.autdesList");
        session.removeAttribute("alterarMultiplasConsignacoesWebController.parametros");

        if (responsavel.isCseSupOrg()) {
            // Carrega lista com naturezas de serviço e margens para filtros
            carregarListaNaturezaServico(request, session, model, responsavel);
            List<MargemTO> lstMargens;
            try {
                lstMargens = margemController.lstMargemRaiz(true,responsavel);
                if (lstMargens == null || lstMargens.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.nao.existe.margem.configurada", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                model.addAttribute("lstMargens", lstMargens);
            } catch (final MargemControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        model.addAttribute("alterarMultiplasConsignacoes", "true");

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=iniciarAlteracaoMultiplosAdes" })
    public String iniciarAlteracaoMultiplosAdes(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Verifica os contratos selecionados para alteração
        final String[] chkAde = request.getParameterValues("chkAltMultiplos");
        final StringBuilder chkAdeCodigo = new StringBuilder();
        final StringBuilder adeNaoSelecionadas = new StringBuilder();
        List<String> adesAlt = null;
        if (chkAde != null) {
            adesAlt = Arrays.asList(chkAde);
            String sep = "";

            for (final String element : chkAde) {
                chkAdeCodigo.append(sep).append(element);
                sep = ",";
            }
        }

        List<TransferObject> autdesList = null;
        try {
            autdesList = pesquisarConsignacaoController.buscaAutorizacao(adesAlt, true, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean permiteReverterValor = true;
        boolean exibirRestaurarIncidencia = false;
        boolean exibirDesbloquearServidor = false;
        boolean omitirAlterarIncidencia = false;
        boolean registroServidorDesbloqueado = false;
        final List<String> adeNumerosAltPosterior = new ArrayList<>();
        BigDecimal totalAtual = BigDecimal.ZERO;
        String rseCodigo = null;
        final List<String> adeCodigos = new ArrayList<>();
        final Map<Short, Boolean> adeIncMargens = new HashMap<>();
        final String marCodigosSelecionados = JspHelper.verificaVarQryStr(request, "marCodigos");

        if (autdesList != null && !autdesList.isEmpty()) {
            for (final TransferObject contrato : autdesList) {
                rseCodigo = contrato.getAttribute(Columns.RSE_CODIGO).toString();
                final String adeCodigo = contrato.getAttribute(Columns.ADE_CODIGO).toString();
                final String adeNumero = contrato.getAttribute(Columns.ADE_NUMERO).toString();
                final String svcCodigo = contrato.getAttribute(Columns.SVC_CODIGO).toString();
                final BigDecimal adeVlr = (BigDecimal) contrato.getAttribute(Columns.ADE_VLR);
                final Short adeIncMargem = (Short) contrato.getAttribute(Columns.ADE_INC_MARGEM);
                adeCodigos.add(adeCodigo);
                adeIncMargens.put(adeIncMargem, true);

                totalAtual = totalAtual.add((BigDecimal) contrato.getAttribute(Columns.ADE_VLR));

                if (permiteReverterValor) {
                    // Busca o valor anterior da última operação
                    final String dadValor = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA, responsavel);
                    if (TextHelper.isDecimalNum(dadValor)) {
                        // Determina se houve alguma alteração posterior
                        final BigDecimal adeVlrAnt = new BigDecimal(dadValor);
                        if (alterarConsignacaoController.verificarAlteracaoPosterior(adeCodigo, adeVlr, adeVlrAnt, responsavel)) {
                            adeNumerosAltPosterior.add(adeNumero);
                        }
                    } else {
                        permiteReverterValor = false;
                    }
                }

                if (!exibirRestaurarIncidencia) {
                    ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse.setSvcCodigo(svcCodigo);
                    paramSvcCse = parametroController.findParamSvcCse(paramSvcCse, responsavel);
                    final Short svcIncideMargem = paramSvcCse != null && TextHelper.isNum(paramSvcCse.getPseVlr()) ? Short.valueOf(paramSvcCse.getPseVlr()) : CodedValues.INCIDE_MARGEM_SIM;
                    if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO) && !adeIncMargem.equals(svcIncideMargem)) {
                        exibirRestaurarIncidencia = true;
                    }
                }
            }
        }

        // Omitir opção de alterar incidência das consignações caso todas as selecionadas sejam da mesma margem
        if (adeIncMargens.size() == 1) {
            omitirAlterarIncidencia = true;
        }

        if (adesAlt == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (responsavel.isCseSupOrg()) {
            // Consulta as margens para montar o combo de margem na escolha de limitação das novas consignações
            final List<MargemTO> margensCheia = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), false, null, false, null, responsavel);
            model.addAttribute("margensCheia", margensCheia);

            final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), false, null, false, null, responsavel);

            final Map <Short, BigDecimal> somatorioPorIncidencia = new HashMap<>();
            final List<String> adesNaoMarcadas = new ArrayList<>();
            for (final MargemTO margem : margens) {
                BigDecimal somatorio = somatorioPorIncidencia.get(margem.getMarCodigo());

                // Recupera total incidencia
                if (adeCodigos != null && !adeCodigos.isEmpty()) {
                    if (TextHelper.isNull(somatorio)) {
                        if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS, responsavel)) {
                            final List<TransferObject> lstAdeNaoSelecionadas = pesquisarConsignacaoController.lstAdeVlrPorPeriodoInclusao(rseCodigo, null, null, margem.getMarCodigo(), adeCodigos, responsavel);
                            if(lstAdeNaoSelecionadas != null && !lstAdeNaoSelecionadas.isEmpty()) {
                                BigDecimal somaAdeNaoSelecionada = BigDecimal.ZERO;
                                for (final TransferObject adeNaoSelecionada : lstAdeNaoSelecionadas) {
                                    adesNaoMarcadas.add(adeNaoSelecionada.getAttribute(Columns.ADE_CODIGO).toString());
                                    somaAdeNaoSelecionada = somaAdeNaoSelecionada.add((BigDecimal) adeNaoSelecionada.getAttribute(Columns.ADE_VLR));
                                }
                                somatorio = somaAdeNaoSelecionada;
                            } else {
                                somatorio = BigDecimal.ZERO;
                            }
                        } else {
                            somatorio = BigDecimal.ZERO;
                        }

                        somatorioPorIncidencia.put(margem.getMarCodigo(), somatorio);
                    }

                    if (margem != null && !TextHelper.isNull(margem.getMrsMargem())) {
                        margem.setMrsMargem(margem.getMrsMargem().subtract(somatorio));
                    } else {
                        margem.setMrsMargem(somatorio.negate());
                    }
                }
            }

            BigDecimal somaContratosNaoSelecionados = BigDecimal.ZERO;
            for (final Map.Entry<Short, BigDecimal> soma : somatorioPorIncidencia.entrySet()) {
            	somaContratosNaoSelecionados = somaContratosNaoSelecionados.add(soma.getValue());
            }

            for (final MargemTO margem : margens) {
            	if (somatorioPorIncidencia.get(margem.getMarCodigo()) != null && somatorioPorIncidencia.get(margem.getMarCodigo()).compareTo(BigDecimal.ZERO) == 0) {
            		margem.setMrsMargem(margem.getMrsMargem().subtract(somaContratosNaoSelecionados));
            	}
            }

            if (!adesNaoMarcadas.isEmpty()) {
            	String separador = "";
            	for (final String adeCodigo : adesNaoMarcadas) {
            		adeNaoSelecionadas.append(separador).append(adeCodigo);
            		separador = ",";
            	}
            	model.addAttribute("adeNaoSelecionadas", adeNaoSelecionadas.toString());
            }

            model.addAttribute("margens", margens);
        }

        if (responsavel.isCseSupOrg() && permiteReverterValor) {
            // Verifica se o servidor tem bloqueio de natureza de serviço empréstimo para exibir o desbloqueio
            final Map<String, Long> nseBloqueios = parametroController.getBloqueioNseRegistroServidor(rseCodigo, CodedValues.NSE_EMPRESTIMO, responsavel);
            exibirDesbloquearServidor = nseBloqueios.get("B") != null && nseBloqueios.get("B").intValue() > 0;
        }

        if (responsavel.isCseSupOrg() && !TextHelper.isNull(rseCodigo)) {
            final RegistroServidorTO registroServidorTO = servidorController.findRegistroServidor(rseCodigo, responsavel);
            registroServidorDesbloqueado = !registroServidorTO.isBloqueado();
        }

        model.addAttribute("autdesList", autdesList);
        model.addAttribute("totalAtual", totalAtual);
        model.addAttribute("chkAdeCodigo", chkAdeCodigo.toString());
        model.addAttribute("permiteReverterValor", permiteReverterValor);
        model.addAttribute("exibirRestaurarIncidencia", exibirRestaurarIncidencia);
        model.addAttribute("exibirDesbloquearServidor", exibirDesbloquearServidor);
        model.addAttribute("omitirAlterarIncidencia", omitirAlterarIncidencia);
        model.addAttribute("registroServidorDesbloqueado", registroServidorDesbloqueado);
        model.addAttribute("marCodigos", marCodigosSelecionados);

        if (permiteReverterValor && !adeNumerosAltPosterior.isEmpty()) {
            model.addAttribute("msgAlertaAlteracaoPosterior", ApplicationResourcesHelper.getMessage("mensagem.aviso.alterar.multiplo.consignacao.restaurar.valor.alt.posterior", responsavel, TextHelper.join(adeNumerosAltPosterior, ", ")));
        }

        return viewRedirect("jsp/alterarConsignacao/alterarMultiplasConsignacoes", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String ades = request.getParameter("adeList");
        final String[] adeArray = ades.split(",");
        final List<String> adeList = new ArrayList<>();
        for (int i = 0; i < adeArray.length; i++) {
            adeList.add(i, adeArray[i].trim());
        }

        final String adesNaoSelecionadas = request.getParameter("adeNaoSelecionadas");
        final List<String> adeListNaoSelecionadas = new ArrayList<>();
        if (!TextHelper.isNull(adesNaoSelecionadas)) {
        	final String[] adeNaoSelecionadasArray = adesNaoSelecionadas.split(",");
        	for (int i = 0; i < adeNaoSelecionadasArray.length; i++) {
        		adeListNaoSelecionadas.add(i, adeNaoSelecionadasArray[i].trim());
        	}
        }
        final String marCodigosSelecionados = JspHelper.verificaVarQryStr(request, "marCodigos");

        if (!adeList.isEmpty()) {
            try {
                final AlterarMultiplasConsignacoesParametros parametros = new AlterarMultiplasConsignacoesParametros();
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS, responsavel) && !adeListNaoSelecionadas.isEmpty()) {
                	parametros.setAdeCodigosNaoSelecionados(adeListNaoSelecionadas);
                	adeList.addAll(adeListNaoSelecionadas);
                	parametros.setAdeCodigos(adeList);
                    if (!TextHelper.isNull(marCodigosSelecionados)){
                        parametros.setMarCodigosSelecionados(Arrays.stream(marCodigosSelecionados.split(";")).map(String::trim).filter(s -> !s.isEmpty()).map(Short::valueOf).toList());
                    }
                } else {
                	parametros.setAdeCodigos(adeList);
                }

                parametros.setIgnorarAltPosterior(true);
                parametros.setAlterarPrazo(!"false".equals(request.getParameter("alterarPrazo")));
                parametros.setRestaurarValor("true".equals(request.getParameter("restaurarValor")));

                String marCodigo = null;
                if (responsavel.isCseSupOrg()) {
                    parametros.setAlterarIncidencia("true".equals(request.getParameter("alterarIncidencia")));
                    parametros.setRestaurarIncidencia("true".equals(request.getParameter("restaurarIncidencia")));
                    parametros.setBloquearServidor("true".equals(request.getParameter("bloquearServidor")));
                    parametros.setDesbloquearServidor("true".equals(request.getParameter("desbloquearServidor")));
                    parametros.setBloquearRegistroServidor("true".equals(request.getParameter("bloquearRegistroServidor")));
                    parametros.setDesbloquearRegistroServidor("true".equals(request.getParameter("desbloquearRegistroServidor")));

                    if(!TextHelper.isNull(request.getParameter("motivoBloqueioRegistroServidor"))) {
                        parametros.setMotivoBloqueioRegistroServidor(request.getParameter("motivoBloqueioRegistroServidor").replace(" ", "_!_"));
                    }

                    marCodigo = request.getParameter("margemLimite");
                    if (TextHelper.isNum(marCodigo)) {
                        parametros.setMarCodigo(Short.valueOf(marCodigo));
                    }
                }

                BigDecimal vlrTotalNovo = null;
                if (!TextHelper.isNull(request.getParameter("totalNovo"))) {
                    vlrTotalNovo = new BigDecimal(NumberHelper.reformat(request.getParameter("totalNovo"), NumberHelper.getLang(), "en"));
                }
                if (!parametros.isRestaurarValor() && (vlrTotalNovo == null || vlrTotalNovo.signum() <= 0)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.valor.total.invalido", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                parametros.setVlrTotalNovo(vlrTotalNovo);

                if (!TextHelper.isNull(request.getParameter("percentualMargem"))) {
                    final BigDecimal percentualMargem = new BigDecimal(NumberHelper.reformat(request.getParameter("percentualMargem"), NumberHelper.getLang(), "en"));
                    parametros.setPercentualMargem(percentualMargem);
                }

                // Realiza a alteração múltipla transacional
                final List<TransferObject> autdesList = alterarConsignacaoController.validarAlteracaoMultiplosAdes(parametros, responsavel);

                // Exibir um alerta na página informando que existem consignações abertas que incidem na margem selecionada
                if (responsavel.isCseSupOrg() && autdesList != null && !autdesList.isEmpty()) {
                    final BigDecimal somatorio = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(autdesList.get(0).getAttribute(Columns.RSE_CODIGO).toString(), null, null, !TextHelper.isNull(marCodigo) ? Short.valueOf(marCodigo) : null, adeList, responsavel);
                    if (somatorio != null && somatorio.compareTo(BigDecimal.ZERO) > 0 || !adeListNaoSelecionadas.isEmpty()) {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.alterar.multiplo.consignacao.incide.margem.selecionada", responsavel));
                    }
                }

                final List<Map<String, Object>> adeMapList = autdesList.stream().map(TransferObject::getAtributos).collect(Collectors.toList());

                final ObjectMapper mapperAdes = new ObjectMapper();

                model.addAttribute("autdesListRaw", new String(Base64.getEncoder().encode(mapperAdes.writeValueAsString(adeMapList).replace(" ", "").getBytes())));
                model.addAttribute("parametros", new String(Base64.getEncoder().encode(mapperAdes.writeValueAsString(parametros).replace(" ", "").getBytes())));

                final boolean exibeAdequarMargem = (ParamSist.paramEquals(CodedValues.TPC_ADEQUAR_MARGEM_SERVIDOR_CONFORME_MARGEM_LIMITE, CodedValues.TPC_SIM, responsavel) && !parametros.isRestaurarValor()) == true;

                model.addAttribute("exibeAdequarMargem", exibeAdequarMargem);

                // Dados da tela de validação: CPF, Matrícula, Nome, CSA, Verba, Serviço, Valor Antigo, Valor Novo, Prazo Antigo, Prazo Novo e Última Parcela.
                final String[] colunas = {
                        FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA,
                        FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO,
                        FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO,
                        FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA,
                        FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA_NOVO,
                        FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO,
                        FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO_NOVO,
                        FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_ULTIMA_PARCELA
                };
                formatarValoresListaConsignacao(autdesList, colunas, responsavel);
                model.addAttribute("autdesList", autdesList);

                if (autdesList != null && !autdesList.isEmpty()) {
                    model.addAttribute("servidor", autdesList.get(0));

                    BigDecimal totalAtual = BigDecimal.ZERO;
                    BigDecimal totalNovo = BigDecimal.ZERO;
                    for (final TransferObject ade : autdesList) {
                    	final String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);

                    	if (adeListNaoSelecionadas.isEmpty() || !adeListNaoSelecionadas.isEmpty() && !adeListNaoSelecionadas.contains(adeCodigo)) {
                    		totalNovo = totalNovo.add((BigDecimal) ade.getAttribute("adeVlrNovo"));
                    		totalAtual = totalAtual.add((BigDecimal) ade.getAttribute(Columns.ADE_VLR));
                    	}
                    }
                    model.addAttribute("totalAtual", totalAtual);
                    model.addAttribute("totalNovo", totalNovo);
                }

                final List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
                model.addAttribute("lstMtvOperacao", lstMtvOperacao);

                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                    final List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);
                    model.addAttribute("lstTipoJustica", lstTipoJustica);
                }

                model.addAttribute("adeListNaoSelecionadas", adeListNaoSelecionadas);

                return viewRedirect("jsp/alterarConsignacao/validarAlteracaoMultiplasConsignacoes", request, session, model, responsavel);
            } catch (final ZetraException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (final ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (final JsonProcessingException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.nenhum.registro.selecionado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<Map<String, Object>> autdesListRaw = null;
        Map<String, Object> parametrosMap = null;

        if (request.getParameter("autdesListRaw") != null && request.getParameter("parametros") != null) {
            final Gson gson = new Gson();
            autdesListRaw = gson.fromJson(new String(Base64.getDecoder().decode(request.getParameter("autdesListRaw"))), List.class);
            parametrosMap = gson.fromJson(new String(Base64.getDecoder().decode(request.getParameter("parametros"))), Map.class);
        }

        List<TransferObject> autdesList = null;

        if (autdesListRaw != null && !autdesListRaw.isEmpty()) {
            for (final Map<String, Object> adeMap: autdesListRaw) {
                if (autdesList ==  null) {
                    autdesList = new ArrayList<>();
                }

                final CustomTransferObject newTO = new CustomTransferObject();
                newTO.setAtributos(adeMap);
                if (newTO.getAttribute(Columns.ADE_INC_MARGEM) != null) {
                    newTO.setAttribute(Columns.ADE_INC_MARGEM, ((Double) newTO.getAttribute(Columns.ADE_INC_MARGEM)).shortValue());
                }
                if (newTO.getAttribute(Columns.ADE_DATA) != null) {
                    newTO.setAttribute(Columns.ADE_DATA, new Date(((Double) newTO.getAttribute(Columns.ADE_DATA)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_ANO_MES_INI) != null) {
                    newTO.setAttribute(Columns.ADE_ANO_MES_INI, new Date(((Double) newTO.getAttribute(Columns.ADE_ANO_MES_INI)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_ANO_MES_FIM) != null) {
                    newTO.setAttribute(Columns.ADE_ANO_MES_FIM, new Date(((Double) newTO.getAttribute(Columns.ADE_ANO_MES_FIM)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_INT_FOLHA) != null) {
                    newTO.setAttribute(Columns.ADE_INT_FOLHA, ((Double) newTO.getAttribute(Columns.ADE_INT_FOLHA)).shortValue());
                }
                if (newTO.getAttribute("adeVlrNovo") != null) {
                    newTO.setAttribute("adeVlrNovo", new BigDecimal((Double) newTO.getAttribute("adeVlrNovo")));
                }
                if (newTO.getAttribute("adePrazoNovo") != null) {
                    newTO.setAttribute("adePrazoNovo", ((Double) newTO.getAttribute("adePrazoNovo")).intValue());
                }
                autdesList.add(newTO);
            }
        }

        try {
            AlterarMultiplasConsignacoesParametros parametros = null;
            if (parametrosMap != null) {
                parametros = new AlterarMultiplasConsignacoesParametros();
                parametros.setarCampos(parametrosMap);
            }

            if (parametros == null || autdesList == null || autdesList.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.nenhum.registro.selecionado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            parametros.setTmoCodigo(JspHelper.verificaVarQryStr(request, "tmoCodigo"));
            parametros.setOcaObs(JspHelper.verificaVarQryStr(request, "adeObs"));

            if(ParamSist.paramEquals(CodedValues.TPC_ADEQUAR_MARGEM_SERVIDOR_CONFORME_MARGEM_LIMITE, CodedValues.TPC_SIM, responsavel)) {
                parametros.setAdequarMargemServidor("S".equals(JspHelper.verificaVarQryStr(request, "adequarMargem")));
            }

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                // Dados de decisão judicial
                final String tjuCodigo = JspHelper.verificaVarQryStr(request, "tjuCodigo");
                final String cidCodigo = JspHelper.verificaVarQryStr(request, "cidCodigo");
                final String djuNumProcesso = JspHelper.verificaVarQryStr(request, "djuNumProcesso");
                final String djuData = JspHelper.verificaVarQryStr(request, "djuData");
                final String djuTexto = JspHelper.verificaVarQryStr(request, "djuTexto");

                if (isTipoJusticaObrigatorio(responsavel) && TextHelper.isNull(tjuCodigo) ||
                        isComarcaJusticaObrigatorio(responsavel) && TextHelper.isNull(cidCodigo) ||
                        isNumeroProcessoObrigatorio(responsavel) && TextHelper.isNull(djuNumProcesso) ||
                        isDataDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuData) ||
                        isTextoDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuTexto)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alteracao.avancada.decisao.judicial.dados.minimos", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!TextHelper.isNull(tjuCodigo) && !TextHelper.isNull(djuTexto) && !TextHelper.isNull(djuData)) {
                    // Se informado, pelo menos tipo de justiça, texto e data devem ser informados. Os demais são opcionais.
                    parametros.setTjuCodigo(tjuCodigo);
                    parametros.setCidCodigo(cidCodigo);
                    parametros.setDjuNumProcesso(djuNumProcesso);
                    parametros.setDjuData(DateHelper.parse(djuData, LocaleHelper.getDatePattern()));
                    parametros.setDjuTexto(djuTexto);
                }
            }

            // Realiza a alteração múltipla transacional
            alterarConsignacaoController.alterarMultiplosAdes(autdesList, parametros, responsavel);

            // Define mensagem de sucesso e retorna para o usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.multiplo.consignacao.sucesso", responsavel));

            // Redireciona para listagem de relatórios
            return "forward:/v3/listarRelatorio?tipo=alteracao_multiplas_ade&" + SynchronizerToken.generateToken4URL(request);

        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private List<TransferObject> formatarValoresListaConsignacao(List<TransferObject> lstConsignacao, String[] colunas, AcessoSistema responsavel) {
        for (TransferObject ade : lstConsignacao) {
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);

            for (final String chaveCampo : colunas) {
                String valorCampo = "";

                if (FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + (!TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString());

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_NUMERO).toString();

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO.equals(chaveCampo)) {
                    final String adeCodReg = ade.getAttribute(Columns.ADE_COD_REG) != null && !"".equals(ade.getAttribute(Columns.ADE_COD_REG)) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
                    valorCampo = (!TextHelper.isNull(ade.getAttribute(Columns.CNV_COD_VERBA)) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString()) + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_INDICE)) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "") + " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString() + (CodedValues.COD_REG_ESTORNO.equals(adeCodReg) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : "");

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA.equals(chaveCampo)) {
                    try {
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrAtual")) ? NumberHelper.reformat(ade.getAttribute("adeVlrAtual").toString(), "en", NumberHelper.getLang()) : "");
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA_NOVO.equals(chaveCampo)) {
                    try {
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrNovo")) ? NumberHelper.reformat(ade.getAttribute("adeVlrNovo").toString(), "en", NumberHelper.getLang()) : "");
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO.equals(chaveCampo)) {
                    if (!TextHelper.isNull(ade.getAttribute("adePrazoAtual"))) {
                        valorCampo = ade.getAttribute("adePrazoAtual").toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                    }

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO_NOVO.equals(chaveCampo)) {
                    if (!TextHelper.isNull(ade.getAttribute("adePrazoNovo"))) {
                        valorCampo = ade.getAttribute("adePrazoNovo").toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                    }

                } else if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_ULTIMA_PARCELA.equals(chaveCampo)) {
                    try {
                        if (ade.getAttribute("vlrParcelaExtra") != null) {
                            valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("vlrParcelaExtra")) ? NumberHelper.reformat(ade.getAttribute("vlrParcelaExtra").toString(), "en", NumberHelper.getLang()) : "");
                        } else {
                            valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrNovo")) ? NumberHelper.reformat(ade.getAttribute("adeVlrNovo").toString(), "en", NumberHelper.getLang()) : "");
                        }
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                ade.setAttribute(chaveCampo, valorCampo);
            }
        }

        return lstConsignacao;
    }
}
