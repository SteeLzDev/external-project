package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: LiquidarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Liquidar Consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/liquidarConsignacao" })
public class LiquidarConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.liquidar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/liquidarConsignacao");
        model.addAttribute("acaoListarCidades", "liquidarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("operacaoPermiteSelecionarPeriodo", true);
        model.addAttribute("nomeCampo", "ADE_CODIGO");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_ANEXO, responsavel));

            model.addAttribute("tipoJusticaObrigatorio", isTipoJusticaObrigatorio(responsavel));
            model.addAttribute("comarcaJusticaObrigatorio", isComarcaJusticaObrigatorio(responsavel));
            model.addAttribute("numeroProcessoObrigatorio", isNumeroProcessoObrigatorio(responsavel));
            model.addAttribute("dataDecisaoObrigatorio", isDataDecisaoObrigatorio(responsavel));
            model.addAttribute("textoDecisaoObrigatorio", isTextoDecisaoObrigatorio(responsavel));
            model.addAttribute("anexoObrigatorio", isAnexoDecisaoObrigatorio(responsavel));
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.permissao.nao.encontrado", responsavel, ex.getMessage());
        }
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.LIQUIDAR_CONSIGNACAO_ANEXO, responsavel);
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String[] adeCodigos = request.getParameterValues("chkADE");
        if ((adeCodigos == null || adeCodigos.length == 0) && request.getParameter("ADE_CODIGO") != null) {
            adeCodigos = new String[1];
            adeCodigos[0] = request.getParameter("ADE_CODIGO");
        }

        if (adeCodigos == null || adeCodigos.length == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CustomTransferObject autDesconto = null;
        boolean exigeSenhaServidor = false;

        try {
            for (String adeCodigo : adeCodigos) {
                if (!TextHelper.isNull(adeCodigo)) {
                    autDesconto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO((String) autDesconto.getAttribute(Columns.SVC_CODIGO), responsavel);
                    if (!exigeSenhaServidor) {
                        exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerLiquidarConsignacao();
                    }
                }
            }
        } catch (AutorizacaoControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("exigeSenhaServidor", responsavel.isCsaCor() && exigeSenhaServidor);

        String urlDestino = "../v3/liquidarConsignacao?acao=liquidar";
        String funCodigo = CodedValues.FUN_LIQ_CONTRATO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return confirmarLiquidacao(request, response, session, model);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona operacao para liquidar consignacao
        String link = "../v3/liquidarConsignacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao.nao.reverter.renegociacao", responsavel);

        acoes.add(new AcaoConsignacao("LIQ_CONTRATO", CodedValues.FUN_LIQ_CONTRATO, descricao, descricaoCompleta, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkADE"));

        // Adiciona o editar consignacao
        link = "../v3/liquidarConsignacao?acao=detalharConsignacao";
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
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "liquidar");
        return criterio;
    }

    protected String confirmarLiquidacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String[] adeCodigos = request.getParameterValues("chkADE");
        if ((adeCodigos == null || adeCodigos.length == 0) && request.getParameter("ADE_CODIGO") != null) {
            adeCodigos = new String[1];
            adeCodigos[0] = request.getParameter("ADE_CODIGO");
        }

        if (adeCodigos == null || adeCodigos.length == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca dados da ade
        List<TransferObject> autdesList = new ArrayList<>();

        String svcCodigo = null;
        String orgCodigo = null;
        java.util.Date adeAnoMesFim = null;

        for (String adeCodigo : adeCodigos) {
            try {
                CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                autdesList.add(autdes);

                svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                adeAnoMesFim = DateHelper.leastDate((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM), adeAnoMesFim);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_LIQ_CONTRATO, responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        model.addAttribute("autdesList", autdesList);
        model.addAttribute("temPermissaoAnexarLiquidar", temPermissaoAnexarLiquidar(responsavel));

        try {
            List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);
            model.addAttribute("lstTipoJustica", lstTipoJustica);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            Set<java.util.Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);
            model.addAttribute("periodos", periodos);
        } catch (PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/liquidarConsignacao/confirmarLiquidacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=liquidar" })
    public String liquidar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, CompraContratoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token de sessao para evitar a chamada direta da operacao
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? (Integer.valueOf(paramTamMaxArqAnexo)).intValue() : 200);

            boolean temPermissaoAnexarLiquidar = temPermissaoAnexarLiquidar(responsavel);
            UploadHelper uploadHelper = null;

            if (temPermissaoAnexarLiquidar) {
                try {
                    uploadHelper = new UploadHelper();
                    uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            String[] ades = JspHelper.obterParametrosRequisicao(request, uploadHelper, new String[] {"ADE_CODIGO", "chkADE"});
            if ((ades == null || ades.length == 0) && JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO") != null) {
                ades = new String[1];
                ades[0] = JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO");
            }

            // Remove valores nulos
            List<String> adeCodigos = new ArrayList<>();
            for (String ade : ades) {
                if (!TextHelper.isNull(ade)) {
                    adeCodigos.add(ade);
                }
            }

            if (adeCodigos == null || adeCodigos.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String tipo = JspHelper.verificaVarQryStr(request, uploadHelper, "tipo");
            String funCodigo = ("confirmar_liquidacao".equals(tipo) ? CodedValues.FUN_CONF_LIQUIDACAO : CodedValues.FUN_LIQ_CONTRATO);

            String msg = "";
            for (String adeCodigo : adeCodigos) {
                String adeNumero = null;
                try {
                    CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                    String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                    String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
                    adeNumero = autdes.getAttribute(Columns.ADE_NUMERO).toString();

                    if (!AcessoFuncaoServico.temAcessoFuncao(request, funCodigo, responsavel.getUsuCodigo(), svcCodigo)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                    boolean exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerLiquidarConsignacao();

                    if (responsavel.isCsaCor() && exigeSenhaServidor) {
                        if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "serAutorizacao")) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "tokenOAuth2"))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        } else {
                            try {
                                SenhaHelper.validarSenha(request, uploadHelper, rseCodigo, svcCodigo, false, true, false, responsavel);
                            } catch (ZetraException ex) {
                                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }

                    String[] visibilidadeAnexos = null;
                    if (uploadHelper != null && uploadHelper.isRequisicaoUpload() && uploadHelper.getValoresCampoFormulario("aadExibe") != null) {
                        visibilidadeAnexos = uploadHelper.getValoresCampoFormulario("aadExibe").toArray(new String[0]);
                    } else {
                        visibilidadeAnexos = request.getParameterValues("aadExibe");
                    }

                    // Salva o arquivo
                    File anexo = null;

                    try {
                        String fileName2Senha = TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "fileName2Senha")) ? "" : JspHelper.verificaVarQryStr(request, uploadHelper, "fileName2Senha");
                        if ((uploadHelper != null && uploadHelper.hasArquivosCarregados()) || !fileName2Senha.equals("")) {
                            String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                            String diretorioTemporario = diretorioRaizArquivos + File.separator + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS;

                            TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);
                            String path = "anexo" + File.separatorChar + DateHelper.format((Date) ade.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
                            File diretorioTemporarioAde = new File(diretorioTemporario + File.separatorChar + "anexo" + File.separatorChar + File.separatorChar + adeCodigo);
                            if (uploadHelper.hasArquivosCarregados()) {
                                anexo = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO);
                            } else {
                                File arquivo = new File(diretorioTemporarioAde.getPath() + File.separatorChar + fileName2Senha);
                                if (arquivo.getCanonicalPath().startsWith(arquivo.getPath())) {
                                    anexo = arquivo;
                                } else {
                                    throw new ZetraException("mensagem.erro.anexo.invalido.suspender.consignacao", responsavel);
                                }
                                File diretorioDestino = new File(diretorioRaizArquivos + File.separator + path);
                                File arquivoDestino = new File(diretorioDestino.getPath() + File.separator + fileName2Senha);
                                if (diretorioDestino.exists() || diretorioDestino.mkdirs()) {
                                    try {
                                        anexo = arquivoDestino;
                                    } catch (Exception e) {
                                        if (diretorioTemporarioAde.exists()) {
                                            FileHelper.deleteDir(diretorioTemporarioAde.getPath());
                                        }
                                        throw new ZetraException("mensagem.erro.upload.anexo.suspender.consignacao", responsavel);
                                    }
                                } else {
                                    throw new ZetraException("mensagem.erro.upload.anexo.suspender.consignacao", responsavel);
                                }
                                FileHelper.deleteDir(diretorioTemporarioAde.getPath());
                            }
                        }
                    } catch (ZetraException | IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    Date ocaPeriodoDate = null;
                    String ocaPeriodo = JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO");

                    if (!TextHelper.isNull(ocaPeriodo)) {
                        ocaPeriodoDate = (!TextHelper.isNull(ocaPeriodo)) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;
                    }

                    // POJO com parâmetros para a operação
                    LiquidarConsignacaoParametros parametros = new LiquidarConsignacaoParametros();
                    parametros.setOcaPeriodo(ocaPeriodoDate);
                    parametros.setVisibilidadeAnexos(visibilidadeAnexos);

                    if (anexo != null) {
                        List<File> anexos = new ArrayList<>();
                        anexos.add(anexo);
                        parametros.setAnexos(anexos);
                    }

                    if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                        // Dados de decisão judicial
                        String tjuCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "tjuCodigo");
                        String cidCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "cidCodigo");
                        String djuNumProcesso = JspHelper.verificaVarQryStr(request, uploadHelper, "djuNumProcesso");
                        String djuData = JspHelper.verificaVarQryStr(request, uploadHelper, "djuData");
                        String djuTexto = JspHelper.verificaVarQryStr(request, uploadHelper, "djuTexto");

                        if ((isTipoJusticaObrigatorio(responsavel) && TextHelper.isNull(tjuCodigo)) || (isComarcaJusticaObrigatorio(responsavel) && TextHelper.isNull(cidCodigo)) || (isNumeroProcessoObrigatorio(responsavel) && TextHelper.isNull(djuNumProcesso)) || (isDataDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuData)) || (isTextoDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuTexto))) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.reativacao.decisao.judicial.dados.minimos", responsavel));
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

                    CustomTransferObject tipoMotivoOperacao = null;
                    if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "TMO_CODIGO"))) {
                        tipoMotivoOperacao = new CustomTransferObject();
                        tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                        tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, uploadHelper, "TMO_CODIGO"));
                        tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_OBS"));
                    }

                    liquidarConsignacaoController.liquidar(adeCodigo, tipoMotivoOperacao, parametros, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.concluido.sucesso", responsavel));

                    if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                    }

                    if (responsavel.isCsaCor()) {
                        // Verifica se a consignataria pode ser desbloqueada automaticamente
                        String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                        if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.csa.desbloqueada.automaticamente", responsavel));
                        }

                        if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_LIQUI_COMPRA)) {
                            // Executa o desbloqueio automatico tambem para os contratos resultantes da compra.
                            // Evita que caso alguma consignataria tenha sido bloqueada por nao informacao
                            // de pagamento de saldo devedor ela seja desbloqueada, se possí­vel, em funcao
                            // da liquidacao do contrato origiario.
                            List<String> adesDestinoCompra = compraContratoController.recuperarAdesCodigosDestinoCompra(adeCodigo);
                            compraContratoController.executarDesbloqueioAutomaticoConsignatarias(adesDestinoCompra, responsavel);
                        }
                    }

                } catch (AutorizacaoControllerException mae) {
                    LOG.error(mae);
                    msg += adeNumero + ": " + mae.getMessage() + "<BR>";
                }
            }

            // Em caso de erro ao desliquidar algum dos contratos, retorna mensagem de erro
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
                session.removeAttribute(CodedValues.MSG_INFO);
            }

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    protected boolean temPermissaoAnexarLiquidar(AcessoSistema responsavel) {
        return responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);
    }
}
