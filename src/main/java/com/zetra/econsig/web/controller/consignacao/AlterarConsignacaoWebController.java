package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.BadPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.indice.IndiceController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AlterarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso AlterarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarConsignacao" })
public class AlterarConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private IndiceController indiceController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.alterar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/alterarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("acaoListarCidades", "alterarConsignacao");
        model.addAttribute("tipoArquivoAnexo", "anexo_consignacao");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_ANEXO, responsavel));
            model.addAttribute("exibirNovoValor", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_NOVO_VALOR, responsavel));
            model.addAttribute("exibirNumPrestacoesRest", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_NUM_PRESTACOES_REST, responsavel));
            model.addAttribute("exibirNovoValorLiq", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_NOVO_VALOR_LIQ, responsavel));
            model.addAttribute("exibirInfoBancaria", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_INFO_BANCARIA ,responsavel));
            model.addAttribute("exibirMotivoOperacao", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_MOTIVO_OPERACAO, responsavel));
            model.addAttribute("exibirCadVlrTac", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_TAC, responsavel));
            model.addAttribute("exibirCadVlrIof", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_IOF, responsavel));
            model.addAttribute("exibirCadVlrMensVinc", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_CAD_VLR_MENS_VINC, responsavel));
            model.addAttribute("exibirSegPrestamista", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_SEG_PRESTAMISTA, responsavel));
            model.addAttribute("exibirVlrLqdTxJuros", ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_VLR_LIQ_TX_JUROS, responsavel));


            model.addAttribute("motivoOperacaoObrigatorio", isMotivoOperacaoObrigatorio(responsavel));
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


    protected boolean isMotivoOperacaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_MOTIVO_OPERACAO, responsavel);
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.ALTERAR_CONSIGNACAO_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        if (responsavel.isCseSup()) {
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        }
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_AGUARD_CONF_E_DEF, CodedValues.TPC_SIM, responsavel)) {
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        }
        if (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ALTERAR_ADE_ESTOQUE, CodedValues.TPC_SIM, responsavel)) {
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        }
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/alterarConsignacao?acao=editar";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.alterar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.alterar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alterar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("ALT_CONSIGNACAO", CodedValues.FUN_ALT_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnAlterarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/alterarConsignacao?acao=detalharConsignacao";
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
        criterio.setAttribute("TIPO_OPERACAO", "alterar");
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel)){
           criterio.setAttribute("FILTRO_DECISAO_JUDICIAL", Boolean.TRUE);
        }
        return criterio;
    }

    /**
     * Permite customizações na alteração de consignação em classes que estendem este Web Controller
     * @param autdes
     * @param responsavel
     */
    protected void configurarAlteracaoConsignacao(CustomTransferObject autdes, HttpServletRequest request, Model model, AcessoSistema responsavel) {
        if (!responsavel.isCseSupOrg()) {
            // Oculta opções avançadas, e deixa fixo conforme a operação de adequação à margem
            model.addAttribute("omitirOpcoesAvancadas", Boolean.TRUE);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String alterarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("paramSession", paramSession);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String adeCodigo = request.getParameter("ADE_CODIGO");
        if (StringUtils.isBlank(adeCodigo)) {
            adeCodigo = request.getParameter("ade");
        }

        if ((adeCodigo == null) || "".equals(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca o contrato a ser alterado
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String cnvCodigo = autdes.getAttribute(Columns.CNV_CODIGO).toString();
        final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        final String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        final Short intFolha = (Short) autdes.getAttribute(Columns.ADE_INT_FOLHA);
        final Short incMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
        final Date adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
        final String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);

        if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_ALT_CONSIGNACAO, responsavel.getUsuCodigo(), svcCodigo)) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca os dados do contrato que podem ser alterados
        final Map<String, String> dadosAutorizacao = new HashMap<>();
        final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
        for (final TransferObject tda : tdaList) {
            final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
            final String tdaValor = autorizacaoController.getValorDadoAutDesconto(adeCodigo, tdaCodigo, responsavel);
            dadosAutorizacao.put(tdaCodigo, tdaValor);
        }

        // Parâmetros de Sistema Necessários
        // Busca status se mensagem de margem comprometida esta ativa
        final boolean mensagemMargemComprometida = (ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel).toString());
        // Verifica se sistema tem controle de compulsórios
        final boolean temControleCompulsorios = (ParamSist.getInstance().getParam(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, responsavel).toString());
        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        // Verifica se está habilitado cadastro de decisão judicial
        final boolean habilitaCadDecisaoJudicial = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel);

        // Define permissões do usuário
        final boolean usuPossuiAltAvancadaAde = usuarioTemPermissaoAlteracaoAvancada(responsavel);
        final boolean usuarioPodeAnexarAlteracao = usuarioPodeAnexarAlteracao(responsavel);
        final boolean exigeMotivoOperacao = isExigeMotivoOperacao(CodedValues.FUN_ALT_CONSIGNACAO, responsavel);
        final boolean exibeMotivoOperacao = exigeMotivoOperacao || usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && habilitaCadDecisaoJudicial);

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final Short adeIncMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
        final Short adeIntFolha = paramSvcCse.getTpsIntegraFolha(); // Integra folha sim ou não
        final String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
        final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
        String adeVlrPadrao = ""; //paramSvcCse.getTpsAdeVlr() != null ? paramSvcCse.getTpsAdeVlr() : ""; // Valor da prestação fixo para o serviço
        final boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
        final int maxPrazo = ((paramSvcCse.getTpsMaxPrazo() != null) && !"".equals(paramSvcCse.getTpsMaxPrazo())) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;

        final boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac();
        final boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof();
        final boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado();
        final boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();
        final boolean serInfBancariaObrigatoria = (!responsavel.isSer() && paramSvcCse.isTpsInfBancariaObrigatoria());
        final boolean boolTpsSegPrestamista = paramSvcCse.isTpsExigeSeguroPrestamista();
        final boolean permiteVlrLiqTxJuros = paramSvcCse.isTpsVlrLiqTaxaJuros();
        final String permiteAumentarVlrPrz = paramSvcCse.getTpsPermiteAumVlrPrzConsignacao();
        final boolean permiteAumentarVlr = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(permiteAumentarVlrPrz) || CodedValues.PERMITE_AUMENTAR_APENAS_VLR_CONTRATO.equals(permiteAumentarVlrPrz);
        final boolean permiteAumentarPrz = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(permiteAumentarVlrPrz) || CodedValues.PERMITE_AUMENTAR_APENAS_PRZ_CONTRATO.equals(permiteAumentarVlrPrz);
        final boolean servicoCompulsorio = (temControleCompulsorios) && (paramSvcCse.isTpsServicoCompulsorio());
        final String exigeSenhaServidor = (!responsavel.isSer() ? paramSvcCse.getTpsExigeSenhaAlteracaoContratos() : CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS);
        final boolean paramSvcTaxa = paramSvcCse.isTpsValidarTaxaJuros();
        final String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();
        final boolean permiteAlterarVlrLiberado = paramSvcCse.isTpsPermiteAlterarVlrLiberado();
        final boolean retemMargemSvcPercentual = paramSvcCse.isTpsRetemMargemSvcPercentual();

        final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
        final boolean exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao));

        final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
        final boolean exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa));

        // Parâmetro de SVC/CSA que determina se permite valor negativo de contrato
        final boolean permiteVlrNegativo = parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel);

        // Salva o Token de sincronização para evitar duplo request
        SynchronizerToken.saveToken(request);

        // Parâmetros de convênios necessários
        CustomTransferObject convenio = null;
        try {
            convenio = convenioController.getParamCnv(cnvCodigo, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svcPrioridade = convenio.getAttribute(Columns.SVC_PRIORIDADE) != null ? convenio.getAttribute(Columns.SVC_PRIORIDADE).toString() : "";
        final boolean permitePrazoMaiorContSer = ((convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null) && "S".equals(convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO")));

        // Parâmetro de identificador ADE obrigatório
        final boolean identificadorAdeObrigatorio = (!TextHelper.isNull(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) ? "S".equals(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) : paramSvcCse.isTpsIdentificadorAdeObrigatorio());

        final int prazo = autdes.getAttribute(Columns.ADE_PRAZO) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRAZO)) : -1;
        final int pagas = autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS)) : 0;
        int prazoRest = prazo - pagas;

        List<ParcelaDescontoPeriodo> parcelasEmProcessamento = null;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_CONSIDERA_PARCELAS_AGUARD_PROCESSAMENTO, responsavel)) {
            parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);
            prazoRest -= parcelasEmProcessamento != null ? parcelasEmProcessamento.size() : 0;
        }

        String mensagem = "";
        if (autdes.getAttribute(Columns.PRD_ADE_CODIGO) != null) {
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel) + "\n";
            JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel));
        }

        if (responsavel.isCsaCor() && !TextHelper.isNull(paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa())) {
            // Se é consignatária ou correspondente que está reservando margem, então exibe a mensagem do parâmetro
            JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa());
        }

        // Obtém a margem do servidor
        MargemDisponivel margemDisponivel = null;
        try {
            margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, responsavel);
        } catch (final ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final BigDecimal margemRestOld = margemDisponivel.getMargemRestante();

        // Margem restante é a margem + o valor do contrato
        final BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
        final BigDecimal margemRestNew = (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) ? margemRestOld.add(adeVlr) : margemRestOld;
        final BigDecimal adeVlrPercentual = (autdes.getAttribute(Columns.ADE_VLR_PERCENTUAL) != null ? (BigDecimal) autdes.getAttribute(Columns.ADE_VLR_PERCENTUAL) : new BigDecimal("0"));

        // Se tipo valor igual a margem total, coloca no campo de adeVlr o
        // valor da margem disponível para o serviço
        if (CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(tipoVlr)) {
            adeVlrPadrao = margemRestNew.toString();
        }

        // Se existe simulação de consignação ou cadastro de juros, então adiciona combo para seleção
        // de prazos para a autorização, se houverem prazos cadastrados
        final Set<Integer> prazosPossiveis = new TreeSet<>();
        final boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
        final boolean permiteQualquerPrazo = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_QUALQUER_PRAZO_ALTERACAO_ADE, responsavel);

        if (!permiteQualquerPrazo && (temSimulacaoConsignacao || paramSvcTaxa)) {
            // Se não permite qualquer prazo e tem simulação ou validação de taxa,
            // então seleciona os prazos ativos para o serviço selecionado.
            // Se permitir qualquer prazo, deixa a lista de prazos como null
            // abrindo assim um campo de texto para inclusão do novo prazo.
            try {
                final int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                final List<TransferObject> prazos = simulacaoController.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, responsavel);
                if ((prazos != null) && !prazos.isEmpty()) {
                    if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                        prazosPossiveis.addAll(PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel));
                    } else {
                        prazos.forEach(p -> prazosPossiveis.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                    }
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        // Calcula o valor da carência que ainda resta para o contrato, de acordo
        // com a data inicial e o período atual de lançamento
        Integer valorAdeCarencia = 0;
        boolean podeAlterarCarencia = false;
        try {
            final Date adeAnoMesIni = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
            final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

            if (adeAnoMesIni.compareTo(periodoAtual) >= 0) {
                // Se a data inicial do contrato é maior ou igual ao período atual de lançamento, ainda pode alterar a carência
                valorAdeCarencia = PeriodoHelper.getInstance().calcularCarencia(orgCodigo, adeAnoMesIni, TextHelper.forHtmlContent(adePeriodicidade), responsavel);
                podeAlterarCarencia = true;
            }
        } catch (final PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // recupera dados de autorização do contrato
        String tdaModalidadeOp = null;
        String tdaMatriculaCsa = null;

        if (exigeModalidadeOperacao) {
            tdaModalidadeOp = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MODALIDADE_OPERACAO, responsavel);
        }

        if (exigeMatriculaSerCsa) {
            tdaMatriculaCsa = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MATRICULA_SER_NA_CSA, responsavel);
        }

        final boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
        final String rotuloPeriodicidadePrazo = quinzenal ? "" : " (" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")";

        // Se não é servidor, permite cadastro e alteração do índice, e o índice não é somente automático ...
        if (!responsavel.isSer() &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_INDICE, CodedValues.TPC_SIM, responsavel) &&
                !ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel)) {
            // ... habilita exibição do campo indice
            model.addAttribute("exibirCampoIndice", Boolean.TRUE);

            // Define se o indice eh numero ou nao (true numero) se !existe =null
            final boolean indiceNumerico = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString());

            // Limite numérico do indice
            final int limiteIndice = ((ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel))) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;
            final String mascaraIndice = (indiceNumerico ? "#D" : "#A") + String.valueOf(limiteIndice).length();

            // Índice padrão
            final String indicePadrao = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel).toString() : null;

            // Cadastro de indices
            final String indPadCsa = ((convenio.getAttribute("VLR_INDICE") != null) && !"".equals(convenio.getAttribute("VLR_INDICE"))) ? convenio.getAttribute("VLR_INDICE").toString() : "";

            boolean geraCombo = false;
            boolean vlrIndiceDisabled = false;
            String vlrIndice = (autdes.getAttribute(Columns.ADE_INDICE) != null ? (String) autdes.getAttribute(Columns.ADE_INDICE) : "");

            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);
            criterio.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);

            // Verifica a existencia de registros de indice ja cadastrados pela csa ou cse
            final List<TransferObject> indices = indiceController.selectIndices(-1, -1, criterio, responsavel);

            if (indices.isEmpty()) {
                if (TextHelper.isNull(vlrIndice)) {
                    // Se não existir nenhum indice cadastrado, então utilizar parâmetro de convenio 41 OU
                    // parâmetro de sistema 79 (que é sobreposto pelo 41)
                    if (!TextHelper.isNull(indPadCsa)) {
                        vlrIndice = indPadCsa;
                        // Se existir um valor padrão para o parâmetro então o campo estará desabilitado
                        vlrIndiceDisabled = true;
                    } else {
                        vlrIndice = indicePadrao;
                    }
                }
            } else // Se existir um registro apenas, exibir este registro no campo de indice
            if (indices.size() == 1) {
                final TransferObject c0 = indices.get(0);
                vlrIndice = c0.getAttribute(Columns.IND_CODIGO).toString();
                vlrIndiceDisabled = true;
            } else {
                // Se existir mais de um, exibir um combo de seleção com as possibilidades existentes
                geraCombo = true;
            }

            model.addAttribute("vlrIndice", vlrIndice);
            if (!geraCombo) {
                model.addAttribute("vlrIndiceDisabled", vlrIndiceDisabled);
                model.addAttribute("mascaraIndice", mascaraIndice);
            } else {
                model.addAttribute("lstIndices", indices);
            }
        }

        // DESENV-10459 - Permite alterar contratos deferidos que ainda não foram enviados para a folha sem senha do servidor.
        final CustomTransferObject permiteAlterarComLimitacao = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO, responsavel);

        if ((permiteAlterarComLimitacao != null) && (permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR) != null) && "1".equals(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR)) ) {
            final Double percentualAlteracao = Double.parseDouble(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR).toString());
            final List<ParcelaDescontoTO> parcelasProcessadas = parcelaController.findParcelas(adeCodigo, null, responsavel);

            final boolean permiteAlterarSemSenhaTemp = CodedValues.SAD_DEFERIDA.equals(autdes.getAttribute(Columns.ADE_SAD_CODIGO)) && (parcelasProcessadas != null) && parcelasProcessadas.isEmpty() && (parcelasEmProcessamento != null) && parcelasEmProcessamento.isEmpty();

            model.addAttribute("percentualAlteracao", percentualAlteracao);
            model.addAttribute("permiteAlterarSemSenhaTemp", permiteAlterarSemSenhaTemp);
        }

        //Busca tdas específico de sistema que utiliza saúde sem regras do módulo.
        if(!TextHelper.isNull(paramSvcCse.getTpsPermiteDescontoViaBoleto()) &&
                (CodedValues.PAGAMENTO_VIA_BOLETO_OPICIONAL.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()) || CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()))) {
            final CustomTransferObject naturezaServido = servicoController.findNaturezaServico(svcCodigo, responsavel);
            final String nseCodigo = (String) naturezaServido.getAttribute(Columns.NSE_CODIGO);
            if(!TextHelper.isNull(nseCodigo) && (CodedValues.NSE_PLANO_DE_SAUDE.equals(nseCodigo) || CodedValues.NSE_PLANO_ODONTOLOGICO.equals(nseCodigo))) {
                final String tdaFormaPagamento = CodedValues.TDA_FORMA_PAGAMENTO;
                final String tdaValor = autorizacaoController.getValorDadoAutDesconto(adeCodigo, tdaFormaPagamento, responsavel);
                model.addAttribute("formaPagamento",!TextHelper.isNull(tdaValor) ? tdaValor : "0");
                model.addAttribute("servicoPermitePagamentoViaBoleto",paramSvcCse.getTpsPermiteDescontoViaBoleto());
                final Beneficiario beneficiario = beneficiarioController.buscaBeneficiarioAdeCodigo(adeCodigo, responsavel);
                model.addAttribute("beneficiario", beneficiario);
            }
        }
        // Define se é alteração de contrato de Decisão Judicial
        final String tipoDecisaoJudicial = JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial");

        String djuCodigo = null;
        boolean djuRevogada = false;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel)) {
            final TransferObject decisaoJudicial = autorizacaoController.verificaAdeTemDecisaoJudicial(adeCodigo, responsavel);
             if(CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO.equals(tipoDecisaoJudicial) && !TextHelper.isNull(decisaoJudicial)){
                djuCodigo = decisaoJudicial.getAttribute(Columns.DJU_CODIGO) != null ? (String) decisaoJudicial.getAttribute(Columns.DJU_CODIGO) : null;
                djuRevogada = decisaoJudicial.getAttribute(Columns.DJU_DATA_REVOGACAO) != null;
             } else if (CodedValues.FUN_ALT_CONSIGNACAO.equals(responsavel.getFunCodigo()) && ((decisaoJudicial != null) && (decisaoJudicial.getAttribute(Columns.DJU_DATA_REVOGACAO) == null))) {
                 session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alteracao.decisao.judicial.bloqueada", responsavel));
                 return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
             }
        }

        model.addAttribute("responsavel", responsavel);

        model.addAttribute("tipoDecisaoJudicial", tipoDecisaoJudicial);
        model.addAttribute("djuCodigo", djuCodigo);
        model.addAttribute("djuRevogado", djuRevogada);
        model.addAttribute("retemMargemSvcPercentual", retemMargemSvcPercentual);
        model.addAttribute("tipoVlr", tipoVlr);
        model.addAttribute("usuPossuiAltAvancadaAde", usuPossuiAltAvancadaAde);
        model.addAttribute("usuarioPodeAnexarAlteracao", usuarioPodeAnexarAlteracao);
        model.addAttribute("permiteAumentarVlr", permiteAumentarVlr);
        model.addAttribute("exigeSenhaServidor", exigeSenhaServidor);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("adePeriodicidade", adePeriodicidade);
        model.addAttribute("paramSvcTaxa", paramSvcTaxa);
        model.addAttribute("prazosPossiveis", prazosPossiveis);
        model.addAttribute("prazo", prazo);
        model.addAttribute("maxPrazo", maxPrazo);
        model.addAttribute("permiteCadVlrTac", permiteCadVlrTac);
        model.addAttribute("permiteCadVlrIof", permiteCadVlrIof);
        model.addAttribute("permiteCadVlrLiqLib", permiteCadVlrLiqLib);
        model.addAttribute("permiteCadVlrMensVinc", permiteCadVlrMensVinc);
        model.addAttribute("permiteAumentarPrz", permiteAumentarPrz);
        model.addAttribute("exibeMotivoOperacao", exibeMotivoOperacao);
        model.addAttribute("exigeMotivoOperacao", exigeMotivoOperacao);
        model.addAttribute("exigeModalidadeOperacao", exigeModalidadeOperacao);
        model.addAttribute("exigeMatriculaSerCsa", exigeMatriculaSerCsa);
        model.addAttribute("permiteVlrNegativo", permiteVlrNegativo);
        model.addAttribute("mensagemMargemComprometida", mensagemMargemComprometida);
        model.addAttribute("mensagem", mensagem);
        model.addAttribute("permiteQualquerPrazo", permiteQualquerPrazo);
        model.addAttribute("servicoCompulsorio", servicoCompulsorio);
        model.addAttribute("autdes", autdes);
        model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
        model.addAttribute("alteraAdeVlr", alteraAdeVlr);
        model.addAttribute("labelTipoVlr", labelTipoVlr);
        model.addAttribute("rotuloPeriodicidadePrazo", rotuloPeriodicidadePrazo);
        model.addAttribute("valorAdeCarencia", valorAdeCarencia);
        model.addAttribute("podeAlterarCarencia", podeAlterarCarencia);
        model.addAttribute("permiteAlterarVlrLiberado", permiteAlterarVlrLiberado);
        model.addAttribute("boolTpsSegPrestamista", boolTpsSegPrestamista);
        model.addAttribute("permiteVlrLiqTxJuros", permiteVlrLiqTxJuros);
        model.addAttribute("temCET", temCET);
        model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
        model.addAttribute("tdaModalidadeOp", tdaModalidadeOp);
        model.addAttribute("tdaMatriculaCsa", tdaMatriculaCsa);
        model.addAttribute("tdaList", tdaList);
        model.addAttribute("dadosAutorizacao", dadosAutorizacao);
        model.addAttribute("intFolha", intFolha);
        model.addAttribute("adeIntFolha", adeIntFolha);
        model.addAttribute("adeIncMargem", adeIncMargem);
        model.addAttribute("permitePrazoMaiorContSer", permitePrazoMaiorContSer);
        model.addAttribute("incMargem", incMargem);
        model.addAttribute("identificadorAdeObrigatorio", identificadorAdeObrigatorio);
        model.addAttribute("prazoRest", prazoRest);
        model.addAttribute("svcPrioridade", svcPrioridade);

        final String fileName = JspHelper.getPhoto(autdes.getAttribute(Columns.SER_CPF).toString(), autdes.getAttribute(Columns.RSE_CODIGO).toString(), responsavel);
        model.addAttribute("fileName", fileName);

        final Set<Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);
        model.addAttribute("periodos", periodos);

        String adeVlrAtual = (!"".equals(adeVlrPadrao) ? adeVlrPadrao : (retemMargemSvcPercentual && CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr) ? adeVlrPercentual.toString() : adeVlr.toString()));
        adeVlrAtual = NumberHelper.reformat(adeVlrAtual, "en", NumberHelper.getLang());
        model.addAttribute("adeVlrAtual", adeVlrAtual);

        if (usuPossuiAltAvancadaAde || (responsavel.isCsa() && habilitaCadDecisaoJudicial)) {
            final List<TransferObject> statusAutorizacao = pesquisarConsignacaoController.lstStatusAutorizacao(CodedValues.SAD_CODIGOS_ALTERACAO_AVANCADA, responsavel);
            model.addAttribute("statusAutorizacao", statusAutorizacao);

            final List<MargemTO> margens = margemController.lstMargemRaiz(responsavel);
            model.addAttribute("margens", margens);

            final List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
            model.addAttribute("lstMtvOperacao", lstMtvOperacao);

            final List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);
            model.addAttribute("lstTipoJustica", lstTipoJustica);
        } else if(exigeMotivoOperacao) {
            final List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
            model.addAttribute("lstMtvOperacao", lstMtvOperacao);
        }

        // Configura a operação, caso necessário
        configurarAlteracaoConsignacao(autdes, request, model, responsavel);

        return viewRedirect("jsp/alterarConsignacao/alterarConsignacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {
        final ParamSession paramSession = ParamSession.getParamSession(session);

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final boolean usuPossuiAltAvancadaAde = usuarioTemPermissaoAlteracaoAvancada(responsavel);

        final String adeCodigo = request.getParameter("adeCodigo");

    	 // verifica anexo obrigatorio
        final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
        final String idAnexo = session.getId();
        final String aadDescricao = JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO");

        // Busca o contrato a ser alterado
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        final String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        final Short intFolha = (Short) autdes.getAttribute(Columns.ADE_INT_FOLHA);
        final Short incMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
        final String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final boolean retemMargemSvcPercentual = paramSvcCse.isTpsRetemMargemSvcPercentual();
        final String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
        final String permiteAumentarVlrPrz = paramSvcCse.getTpsPermiteAumVlrPrzConsignacao();
        final boolean permiteAumentarVlr = CodedValues.PERMITE_AUMENTAR_VLR_PRZ_CONTRATO.equals(permiteAumentarVlrPrz) || CodedValues.PERMITE_AUMENTAR_APENAS_VLR_CONTRATO.equals(permiteAumentarVlrPrz);
        final String exigeSenhaServidor = (!responsavel.isSer() ? paramSvcCse.getTpsExigeSenhaAlteracaoContratos() : CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS);
        final boolean paramSvcTaxa = paramSvcCse.isTpsValidarTaxaJuros();

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.consignacao.ja.efetuada", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            BigDecimal adeVlrOld = null;
            if (retemMargemSvcPercentual && CodedValues.TIPO_VLR_PERCENTUAL.equals(tipoVlr)) {
                adeVlrOld = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR_PERCENTUAL);
            } else {
                adeVlrOld = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
            }
            final BigDecimal adeVlr = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlr"), NumberHelper.getLang(), "en"));

            Integer adePrazoOld = (Integer) autdes.getAttribute(Columns.ADE_PRAZO);
            if (adePrazoOld != null) {
                // Se não é indeterminado, veja se já tem parcelas pagas, e ajusta o prazo para o valor restante,
                // já que na operação o usuário informa a quantidade de parcelas ainda a serem pagas.
                final Integer adePrdPagasOld = (Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS);
                if (adePrdPagasOld != null) {
                    adePrazoOld = adePrazoOld.intValue() - adePrdPagasOld.intValue();
                }
            }
            Integer adePrazo = null;
            if (!"".equals(JspHelper.verificaVarQryStr(request, "adePrazo")) && !"-1".equals(JspHelper.verificaVarQryStr(request, "adePrazo"))) {
                adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "adePrazo"));
            }

            // Verifica se aumentou o valor ou o prazo
            final boolean aumentouValor = (adeVlrOld.compareTo(adeVlr) == -1);
            final boolean aumentouPrazo = (((adePrazoOld != null) && (adePrazo == null)) ||
                    ((adePrazoOld != null) && (adePrazo != null) && (adePrazoOld.intValue() < adePrazo.intValue())));

            // Verifica se aumentou o capital devido
            final boolean aumentouCapitalDevido = (((adePrazoOld != null) && (adePrazo != null)) ?
                    (adePrazo.doubleValue() * adeVlr.doubleValue()) > (adePrazoOld.doubleValue() * adeVlrOld.doubleValue()) : true);

            // Se aumentou e não pode, então retorna erro para o usuário
            if (!usuPossuiAltAvancadaAde && (aumentouValor && !permiteAumentarVlr)) {
			    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.valor.parcela.maior.atual", responsavel));
			    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}

            boolean exigeSenhaAltAvancada = true;
            if (usuPossuiAltAvancadaAde && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "exigeSenhaAltAvancada"))) {
                exigeSenhaAltAvancada = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "exigeSenhaAltAvancada"));
            }

            // Valida a senha do servidor para fazer a alteração do contrato
            if (exigeSenhaAltAvancada) {
                if (CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS.equals(exigeSenhaServidor) ||
                        (CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR.equals(exigeSenhaServidor) && (aumentouValor || aumentouPrazo)) ||
                        (CodedValues.EXIGE_SENHA_ALTERACAO_CAPITAL_DEVIDO_MAIOR.equals(exigeSenhaServidor) && aumentouCapitalDevido)) {

                    final CustomTransferObject permiteAlterarComLimitacao = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO, responsavel);
                    List<ParcelaDescontoTO> parcelasProcessadas = new ArrayList<>();
                    List<ParcelaDescontoPeriodo> parcelasEmProcessamento = new ArrayList<>();

                    boolean valorAlteradoDentroLimite = false;
                    boolean permiteAlterarSemSenha = false;
                    // DESENV-10459 - Permite alterar contratos deferidos que ainda não foram enviados para a folha sem senha do servidor.
                    if ((permiteAlterarComLimitacao != null) && (permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR) != null) && "1".equals(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR))) {
                        parcelasProcessadas = parcelaController.findParcelas(adeCodigo, null, responsavel);
                        parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);

                        final Double percentualAlteracao = Double.parseDouble(permiteAlterarComLimitacao.getAttribute(Columns.PSE_VLR).toString());
                        final BigDecimal valorContrato = adePrazo != null ? adeVlr.multiply(new BigDecimal(adePrazo)) : adeVlr;
                        final BigDecimal valorContratoOld = adePrazoOld != null ? adeVlrOld.multiply(new BigDecimal(adePrazoOld)) : adeVlrOld;

                        final boolean valorParcelaDentroLimite = ((adeVlr.divide(adeVlrOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;
                        final boolean valorContratoDentroLimite = ((valorContrato.divide(valorContratoOld)).subtract(BigDecimal.valueOf(1))).multiply(BigDecimal.valueOf(100)).doubleValue() <= percentualAlteracao;

                        valorAlteradoDentroLimite =  valorContratoDentroLimite && valorParcelaDentroLimite;

                        if (valorAlteradoDentroLimite && CodedValues.SAD_DEFERIDA.equals(autdes.getAttribute(Columns.ADE_SAD_CODIGO)) && (parcelasProcessadas != null) && parcelasProcessadas.isEmpty() && (parcelasEmProcessamento != null) && parcelasEmProcessamento.isEmpty()) {
                            permiteAlterarSemSenha = true;
                        }
                    }

                    if (!permiteAlterarSemSenha) {
                        if (TextHelper.isNull(request.getParameter("serAutorizacao")) && TextHelper.isNull(request.getParameter("tokenOAuth2"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        } else {
                            SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, false, true, responsavel);
                        }
                    }
                }
            }

            // Valida o novo valor do contrato
            AutorizacaoHelper.validarValorAutorizacao(adeVlr, svcCodigo, csaCodigo, responsavel);

            // Informações financeiras
            BigDecimal adeVlrTac = null;
            BigDecimal adeVlrIof = null;
            BigDecimal adeVlrLiquido = null;
            BigDecimal adeVlrMensVinc = null;

            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeVlrTac"))) {
                adeVlrTac = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrTac"), NumberHelper.getLang(), "en"));
            }

            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeVlrIof"))) {
                adeVlrIof = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrIof"), NumberHelper.getLang(), "en"));
            }

            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeVlrLiquido"))) {
                adeVlrLiquido = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrLiquido"), NumberHelper.getLang(), "en"));
            }

            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeVlrMensVinc"))) {
                adeVlrMensVinc = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrMensVinc"), NumberHelper.getLang(), "en"));
            }

            // Seguro Prestamista
            BigDecimal adeVlrSegPrestamista = null;
            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeVlrSegPrestamista"))) {
                adeVlrSegPrestamista = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeVlrSegPrestamista"), NumberHelper.getLang(), "en"));
            }

            // Taxa de Juros
            BigDecimal adeTaxaJuros = null;
            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeTaxaJuros"))) {
                adeTaxaJuros = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "adeTaxaJuros"), NumberHelper.getLang(), "en"));
            }

            final String adeIdentificador = JspHelper.verificaVarQryStr(request, "adeIdentificador");
            final String adeIndice = JspHelper.verificaVarQryStr(request, "adeIndice");

            Integer adeCarencia = null;
            if (!"".equals(JspHelper.verificaVarQryStr(request, "adeCarencia"))) {
                adeCarencia = Integer.valueOf(JspHelper.verificaVarQryStr(request, "adeCarencia"));
            }

            String senha = null;
            String serLogin = null;

            boolean dispensaValidacaoDigital = false;
            if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                final ServidorTransferObject servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                dispensaValidacaoDigital = !TextHelper.isNull(servidorTO.getSerDispensaDigital()) && CodedValues.TPC_SIM.equals(servidorTO.getSerDispensaDigital());
            }

            final boolean validaDigitais = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !dispensaValidacaoDigital;
            final boolean digitalServidorValidada = ((session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA) != null) && (rseCodigo != null) &&
                    rseCodigo.equals(session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA).toString()));
            if (!validaDigitais || !digitalServidorValidada) {
                // Obtém a Senha criptografada
                if (session.getAttribute("serAutorizacao") != null) {
                    // Se o parâmetro com a Senha está na sessão, então dá preferencia para ele
                    senha = (String) session.getAttribute("serAutorizacao");
                    serLogin = (String) session.getAttribute("serLogin");
                    session.removeAttribute("serAutorizacao");
                } else {
                    // A senha não está na sessão, então pode estar no request
                    senha = request.getParameter("serAutorizacao");
                    serLogin = request.getParameter("serLogin");
                }
            }

            String senhaAberta = null;
            if ((senha != null) && !"".equals(senha)) {
                final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);

                try {
                    senhaAberta = RSA.decrypt(senha, keyPair.getPrivate());
                } catch (final BadPaddingException e) {
                    // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                    throw new ViewHelperException("mensagem.senha.servidor.consulta.invalida", responsavel);
                }
            }

            final String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
            final String adeObs = JspHelper.verificaVarQryStr(request, "adeObs");

            final boolean exigeMotivoOperacao = isExigeMotivoOperacao(CodedValues.FUN_ALT_CONSIGNACAO, responsavel);
            if (exigeMotivoOperacao && TextHelper.isNull(tmoCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazo, adeIdentificador, adeIndice,
                    adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeVlrSegPrestamista, adeCarencia, serLogin, senhaAberta);

            alterarParam.setAdePeriodicidade(adePeriodicidade);
            alterarParam.setNomeAnexo(nomeAnexo);
            alterarParam.setIdAnexo(idAnexo);
            alterarParam.setAadDescricao(aadDescricao);
            alterarParam.setTmoCodigo(tmoCodigo);
            alterarParam.setOcaObs(adeObs);

            if (usuPossuiAltAvancadaAde || (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel))) {
                final boolean alteraMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "afetaMargem"));
                final boolean validaMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "validaMargem"));
                final boolean validaTaxaJuros = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "validaTaxa"));
                final boolean alterarValorPrazoSemLimite = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "valorPrazoSemLimite"));
                final boolean criarNovoContratoDif = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "novoContratoDif"));
                final boolean calcularPrazoDifValor = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "calcularPrazoDifValor"));
                final boolean manterDifValorMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "manterDifValorMargem"));
                final String novaSituacaoContrato = JspHelper.verificaVarQryStr(request, "novoSadCodigo");
                final boolean incluiOcorrencia = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "insereOcorrencia"));
                final boolean validaLimiteAde = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "validaLimiteAde"));
                final boolean liquidaRelacionamentoJudicial = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "liquidaRelacionamentoJudicial"));
                final Short adeIntegraFolha = Short.valueOf(JspHelper.verificaVarQryStr(request, "integraFolha"));
                final Short adeIncideMargem = Short.valueOf(JspHelper.verificaVarQryStr(request, "incideMargem"));
                final boolean permiteAltEntidadesBloqueadas = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, "permiteAltEntidadesBloqueadas"));

                final String[] nomeAnexos = JspHelper.verificaVarQryStr(request, "multipleFiles").split(";");
                String[] visibilidadeAnexos = request.getParameterValues("aadExibe");
                final Set<String> exibirTodos = Set.of(CodedValues.PAP_SUPORTE, CodedValues.PAP_CONSIGNANTE, CodedValues.PAP_ORGAO, CodedValues.PAP_CONSIGNATARIA, CodedValues.PAP_CORRESPONDENTE, CodedValues.PAP_SERVIDOR);
                // Caso tenha selecionado todos, é porque não optou por um específico, sendo assim, consideramos que não é uma alteração avançada da visibilidade
                if (!TextHelper.isNull(visibilidadeAnexos)) {
                	final Set<String> verificaTodosSelecionados = Set.copyOf(Arrays.asList(visibilidadeAnexos));
                	if (verificaTodosSelecionados.containsAll(exibirTodos)) {
                		visibilidadeAnexos = null;
                	}
                }
                final String dirAnexos = session.getId();

                final boolean isAlteracaoAvancada = (!adeIncideMargem.equals(incMargem) ||
                        !adeIntegraFolha.equals(intFolha) ||
                        (alteraMargem != AlterarConsignacaoParametros.PADRAO_ALTERA_MARGEM) ||
                        (!CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS.equals(exigeSenhaServidor) && (exigeSenhaAltAvancada != AlterarConsignacaoParametros.PADRAO_EXIGE_SENHA)) ||
                        (permiteAltEntidadesBloqueadas != AlterarConsignacaoParametros.PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS) ||
                        (validaMargem != AlterarConsignacaoParametros.PADRAO_VALIDA_MARGEM) ||
                        (alterarValorPrazoSemLimite != AlterarConsignacaoParametros.PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE) ||
                        (criarNovoContratoDif != AlterarConsignacaoParametros.PADRAO_CRIAR_NOVO_CONTRATO_DIF) ||
                        (calcularPrazoDifValor != AlterarConsignacaoParametros.PADRAO_CALCULAR_PRAZO_DIF_VALOR) ||
                        (manterDifValorMargem != AlterarConsignacaoParametros.PADRAO_MANTER_DIF_VALOR_MARGEM) ||
                        (incluiOcorrencia != AlterarConsignacaoParametros.PADRAO_INCLUI_OCORRENCIA) ||
                        !TextHelper.isNull(novaSituacaoContrato) ||
                        (paramSvcTaxa && (validaTaxaJuros != AlterarConsignacaoParametros.PADRAO_VALIDA_TAXA_JUROS)) ||
                        (validaLimiteAde != AlterarConsignacaoParametros.PADRAO_VALIDA_LIMITE_ADE) ||
                        !TextHelper.isNull(visibilidadeAnexos) ||
                        !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "djuNumProcesso")));

                if (isAlteracaoAvancada && TextHelper.isNull(tmoCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                alterarParam.setAlteracaoAvancada(isAlteracaoAvancada);
                alterarParam.setAlteraMargem(alteraMargem);
                alterarParam.setExigeSenha(exigeSenhaAltAvancada);
                alterarParam.setPermiteAltEntidadesBloqueadas(permiteAltEntidadesBloqueadas);
                alterarParam.setValidaMargem(validaMargem);
                if (paramSvcTaxa) {
                    alterarParam.setValidaTaxaJuros(validaTaxaJuros);
                }
                alterarParam.setAlterarValorPrazoSemLimite(alterarValorPrazoSemLimite);
                alterarParam.setCriarNovoContratoDif(criarNovoContratoDif);
                alterarParam.setCalcularPrazoDifValor(calcularPrazoDifValor);
                alterarParam.setManterDifValorMargem(manterDifValorMargem);
                alterarParam.setNovaSituacaoContrato(novaSituacaoContrato);
                alterarParam.setIncluiOcorrencia(incluiOcorrencia);
                alterarParam.setAdeIntFolha(adeIntegraFolha);
                alterarParam.setAdeIncideMargem(adeIncideMargem);
                alterarParam.setValidaLimiteAde(validaLimiteAde);
                alterarParam.setLiquidaRelacionamentoJudicial(liquidaRelacionamentoJudicial);
                alterarParam.setNomeAnexos(nomeAnexos);
                alterarParam.setVisibilidadeAnexos(visibilidadeAnexos);
                alterarParam.setDirAnexos(dirAnexos);

                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                    // Dados de decisão judicial
                    final String tjuCodigo = JspHelper.verificaVarQryStr(request, "tjuCodigo");
                    final String cidCodigo = JspHelper.verificaVarQryStr(request, "cidCodigo");
                    final String djuNumProcesso = JspHelper.verificaVarQryStr(request, "djuNumProcesso");
                    final String djuData = JspHelper.verificaVarQryStr(request, "djuData");
                    final String djuTexto = JspHelper.verificaVarQryStr(request, "djuTexto");
                    final String djuCodigo = JspHelper.verificaVarQryStr(request, "djuCodigo");
                    final String djuRevogacao = JspHelper.verificaVarQryStr(request, "djuRevogacao");

                    if ((isTipoJusticaObrigatorio(responsavel) && TextHelper.isNull(tjuCodigo)) ||
                            (isComarcaJusticaObrigatorio(responsavel) && TextHelper.isNull(cidCodigo)) ||
                            (isNumeroProcessoObrigatorio(responsavel) && TextHelper.isNull(djuNumProcesso)) ||
                            (isDataDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuData)) ||
                            (isTextoDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuTexto))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alteracao.avancada.decisao.judicial.dados.minimos", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    if (!TextHelper.isNull(tjuCodigo) && !TextHelper.isNull(djuTexto) && !TextHelper.isNull(djuData)) {
                        // Se informado, pelo menos tipo de justiça, texto e data devem ser informados. Os demais são opcionais.
                        alterarParam.setTjuCodigo(tjuCodigo);
                        alterarParam.setCidCodigo(cidCodigo);
                        alterarParam.setDjuNumProcesso(djuNumProcesso);
                        alterarParam.setDjuData(DateHelper.parse(djuData, LocaleHelper.getDatePattern()));
                        alterarParam.setDjuTexto(djuTexto);
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel) && "S".equals(djuRevogacao)) {
                        alterarParam.setDjuCodigo(djuCodigo);
                        alterarParam.setDjuDataRevogacao(Calendar.getInstance().getTime());
                    }
                }
            }

            alterarParam.setTdaModalidadeOperacao(JspHelper.verificaVarQryStr(request, "tdaModalidadeOp"));
            alterarParam.setTdaMatriculaSerCsa(JspHelper.verificaVarQryStr(request, "tdaMatriculaCsa"));
            alterarParam.setOcaPeriodo(JspHelper.verificaVarQryStr(request, "ocaPeriodo"));

            // Altera os dados da consignação
            final List<TransferObject> dadList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
            for (final TransferObject dad : dadList) {
                final String tdaCodigo = (String)dad.getAttribute(Columns.TDA_CODIGO);
                final String tdaValor = JspHelper.parseValor(request, null, "TDA_" +(String) dad.getAttribute(Columns.TDA_CODIGO), (String) dad.getAttribute(Columns.TDA_DOMINIO));
                alterarParam.setDadoAutorizacao(tdaCodigo, tdaValor);
            }

            final String formaPagamento = JspHelper.verificaVarQryStr(request, "permiteDescontoViaBoleto");

            if(!TextHelper.isNull(paramSvcCse.getTpsPermiteDescontoViaBoleto()) &&
                (CodedValues.PAGAMENTO_VIA_BOLETO_OPICIONAL.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()) || CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()))) {
                alterarParam.setDadoAutorizacao(CodedValues.TDA_FORMA_PAGAMENTO, !TextHelper.isNull(formaPagamento) && "S".equals(formaPagamento) ? CodedValues.FORMA_PAGAMENTO_BOLETO : CodedValues.FORMA_PAGAMENTO_FOLHA);
                if (!TextHelper.isNull(formaPagamento) && "S".equals(formaPagamento)) {
                    // DESENV-18035: Necessário verificar quando o desconto é por poleto se a classificação do beneficiário permitie.
                    final Beneficiario beneficiario = beneficiarioController.buscaBeneficiarioAdeCodigo(adeCodigo, responsavel);
                    if (!TextHelper.isNull(beneficiario) && !TextHelper.isNull(beneficiario.getBfcClassificacao()) && CodedValues.BFC_CLASSIFICACAO_ESPECIAL.equals(beneficiario.getBfcClassificacao())) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.reservar.margem.dependente.via.boleto.nao.permitido", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

            if ((validaDigitais && digitalServidorValidada) ||
                ((request.getParameter("tokenOAuth2") != null) && (request.getAttribute("senhaServidorOK") != null) &&
                 request.getParameter("tokenOAuth2").equals(request.getAttribute("senhaServidorOK")))) {
                alterarParam.setValidaSenhaServidor(false);
            }

            final Date adeAnoMesIniOld = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
            alterarConsignacaoController.alterar(alterarParam, responsavel);

            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final Date adeAnoMesIniNew = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
            final MargemRegistroServidor mrsRse = consultarMargemController.getMargemRegistroServidor(rseCodigo, incMargem, responsavel);

            if((mrsRse !=null) && (adeAnoMesIniNew.compareTo(adeAnoMesIniOld) > 0) && (adeCarencia > 0) && (mrsRse.getMrsPeriodoIni() != null) && (adeAnoMesIniNew.compareTo(mrsRse.getMrsPeriodoIni()) >=0) ) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.consignacao.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                final String ocaPeriodo = JspHelper.verificaVarQryStr(request, "ocaPeriodo");
                final java.util.Date ocaPeriodDate = (!TextHelper.isNull(ocaPeriodo)) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;

                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                        (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), ocaPeriodDate,
                        (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            try {
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
                    final boolean soAceitaExclusao = ParamSist.paramEquals(CodedValues.TPC_PERIODO_COM_APENAS_REDUCOES_SOMENTE_EXCLUSAO, CodedValues.TPC_SIM, responsavel);
                    if (aumentouValor || aumentouPrazo || soAceitaExclusao) {
                        // Inclui alerta na sessão do usuário se o período usado só permite reduções
                        java.sql.Date ocaPeriodo = (!TextHelper.isNull(request.getParameter("ocaPeriodo")) ? DateHelper.toSQLDate(DateHelper.parse(request.getParameter("ocaPeriodo"), "yyyy-MM-dd")) : null);
                        if (ocaPeriodo == null) {
                            ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                        }
                        final java.sql.Date ocaPeriodoValido = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), responsavel);
                        if (!ocaPeriodoValido.equals(ocaPeriodo)) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.data.base.operacao.ajustada.periodo.apenas.reducoes", responsavel));
                        }
                    }
                }
            } catch (final PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    /**
     * Retorna TRUE caso o usuário tenha permissão de alteração avançada
     * @param responsavel
     * @return
     */
    protected boolean usuarioTemPermissaoAlteracaoAvancada(AcessoSistema responsavel) {
        return responsavel.temPermissao(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO);
    }

    /**
     * Retorna TRUE caso o usuário possa anexar arquivos na alteração de consignação
     * @param responsavel
     * @return
     */
    protected boolean usuarioPodeAnexarAlteracao(AcessoSistema responsavel) {
        return ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_ALTERACAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);
    }
}
