package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.zetra.econsig.dto.parametros.SuspenderConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
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
import com.zetra.econsig.persistence.entity.FuncaoAlteraMargemAde;
import com.zetra.econsig.persistence.entity.FuncaoAlteraMargemAdeHome;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.SuspenderConsignacaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SuspenderConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso SuspenderConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/suspenderConsignacao" })
public class SuspenderConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SuspenderConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private MargemController margemController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.suspender.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/suspenderConsignacao");
        model.addAttribute("acaoListarCidades", "suspenderConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.SUSPENDER_CONSIGNACAO_ANEXO, responsavel));

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
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.SUSPENDER_CONSIGNACAO_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        if (responsavel.isCseSup() || responsavel.isCsa()) {
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
        String link = "../v3/suspenderConsignacao?acao=confirmarSuspensao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.suspender.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.suspender", responsavel);
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("SUSP_CONSIGNACAO", CodedValues.FUN_SUSP_CONSIGNACAO, descricao, descricaoCompleta, "desbloqueado.gif", "btnSuspenderConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkSuspender"));

        // Adiciona o editar consignação
        link = "../v3/suspenderConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "suspender");
        return criterio;
    }

    @RequestMapping(params = { "acao=confirmarSuspensao" })
    public String confirmarSuspensao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String strAdeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");
            Object[] adeCodigos = request.getParameterValues("chkSuspender");
            if ((request.getParameter("origem") != null) && "pesquisa_avancada".equals(request.getParameter("origem"))) {
                adeCodigos = request.getParameterValues("chkADE");
            }

            if (((strAdeCodigo == null) || "".equals(strAdeCodigo)) && (adeCodigos == null)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.selecione.ade", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean exibeChkIncidirNaMargem = false;
            boolean possuiAdeIncMargem = false;

            // Verifica se exige motivo para a suspensão da consignação
            final boolean exigeMotivoOperacao = exigeMotivoOperacao(responsavel);

            // Verifica se o usuário tem permissão para suspensão avançada
            final boolean temPermissaoSuspensaoAvancada = temPermissaoSuspensaoAvancada(responsavel);

            // Somente exibe campo para upload de arquivo caso o usuário seja gestor ou suporte
            // e tenha permissão para suspender e editar anexo da consignação
            final boolean temPermissaoAnexarSuspensao = temPermissaoAnexarSuspensao(responsavel);

            // alterações para exibir ades afetadas
            final List<TransferObject> autdesList = new ArrayList<>();
            String orgCodigo = null;
            Date adeAnoMesFim = null;

            new ArrayList<>();

            boolean exigeSenhaServidor = false;

            final FuncaoAlteraMargemAde funcaoAlteraMargemAde = suspenderConsignacaoController.buscarFuncaoAlteraMargemAde(CodedValues.FUN_SUSP_CONSIGNACAO, responsavel);

            final boolean possuiFuncaoAlteraMargemAde = (funcaoAlteraMargemAde != null);

            try {
                if (((strAdeCodigo == null) || "".equals(strAdeCodigo)) && (adeCodigos != null)) {
                    CustomTransferObject autdes = null;
                    for (final Object adeCodigo : adeCodigos) {
                        autdes = pesquisarConsignacaoController.buscaAutorizacao((String) adeCodigo, responsavel);
                        autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                        autdesList.add(autdes);
                        orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                        adeAnoMesFim = DateHelper.leastDate((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM), adeAnoMesFim);
                        final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel);
                        if(!exigeSenhaServidor) {
                            exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerSuspenderConsignacao();
                        }
                        if((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString()) && !TextHelper.isNull(funcaoAlteraMargemAde) && autdes.getAttribute(Columns.ADE_INC_MARGEM).equals(funcaoAlteraMargemAde.getMarCodigoOrigem())) {
                            possuiAdeIncMargem = true;
                        }
                    }
                } else {
                    CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(strAdeCodigo, responsavel);
                    autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                    autdesList.add(autdes);
                    orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                    adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
                    final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel);
                    exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerSuspenderConsignacao();
                    if((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString()) && !TextHelper.isNull(funcaoAlteraMargemAde) && autdes.getAttribute(Columns.ADE_INC_MARGEM).equals(funcaoAlteraMargemAde.getMarCodigoOrigem())) {
                        possuiAdeIncMargem = true;
                    }
                }
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if(possuiFuncaoAlteraMargemAde) {
                final MargemTO margem = new MargemTO(funcaoAlteraMargemAde.getMarCodigoDestino());

                final String descricaoMargemDestino = margemController.findMargem(margem, responsavel).getMarDescricao();

                exibeChkIncidirNaMargem = podeIncidirNaMargem(possuiFuncaoAlteraMargemAde, possuiAdeIncMargem, responsavel);

                if(exibeChkIncidirNaMargem) {
                    model.addAttribute("descricaoMargemDestino", descricaoMargemDestino);
                }
            }

            // Se não tem permissão de suspensão avançada e a operação não exige motivo, então redireciona direto para a operação
            if (!temPermissaoSuspensaoAvancada && !exigeMotivoOperacao) {
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                // Executa a suspensão da consignação
                return suspender(request, response, session, model);
            }

            // Busca os períodos para o combo de seleção do período da operação
            final Set<Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);

            final List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);

            model.addAttribute("lstConsignacao", autdesList);
            model.addAttribute("exigeMotivoOperacao", exigeMotivoOperacao);
            model.addAttribute("temPermissaoSuspensaoAvancada", temPermissaoSuspensaoAvancada);
            model.addAttribute("temPermissaoAnexarSuspensao", temPermissaoAnexarSuspensao);
            model.addAttribute("periodos", periodos);
            model.addAttribute("lstTipoJustica", lstTipoJustica);
            model.addAttribute("exigeSenhaServidor", responsavel.isCsaCor() && exigeSenhaServidor);
            model.addAttribute("exibeChkIncidirNaMargem", exibeChkIncidirNaMargem);

            return viewRedirect("jsp/suspenderConsignacao/confirmarSuspensao", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }


    protected boolean podeIncidirNaMargem(boolean possuiFuncaoAlteraMargemAde, boolean possuiAdeIncMargem, AcessoSistema responsavel) {
        return possuiFuncaoAlteraMargemAde && possuiAdeIncMargem && ParamSist.paramEquals(CodedValues.TPC_POSSIBILIDADE_INCIDIR_MARGEM_SUSPENSAO_REATIVACAO, CodedValues.TPC_SIM, responsavel) ;
    }

    @RequestMapping(params = { "acao=suspenderConsignacao" })
    public String suspender(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String msg = "";

            final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            final int tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;

            // Verifica se o usuário tem permissão para suspensão avançada
            final boolean temPermissaoSuspensaoAvancada = temPermissaoSuspensaoAvancada(responsavel);

            UploadHelper uploadHelper = null;

            if (exigeMotivoOperacao(responsavel) || temPermissaoSuspensaoAvancada) {
                try {
                    uploadHelper = new UploadHelper();
                    uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
                } catch (final Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            final boolean removeIncidenciaMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, uploadHelper, "removeIncidenciaMargem"));

            final boolean alteraIncidenciaMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, uploadHelper, "incidirNaMargem"));

            if (temPermissaoSuspensaoAvancada && removeIncidenciaMargem && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "TMO_CODIGO"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            Object[] adeCodigos = null;
            final String strAdeCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO")) ? JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO") : JspHelper.verificaVarQryStr(request, uploadHelper, "ade");
            if (!TextHelper.isNull(strAdeCodigo)) {
                adeCodigos = new String[1];
                adeCodigos[0] = strAdeCodigo;
            } else {
                if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload()) {
                    adeCodigos = uploadHelper.getValoresCampoFormulario("chkSuspender").toArray();
                } else {
                    adeCodigos = request.getParameterValues("chkSuspender");
                }
                if (((adeCodigos == null) || (adeCodigos.length == 0)) && "pesquisa_avancada".equals(JspHelper.verificaVarQryStr(request, uploadHelper, "origem"))) {
                    if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload()) {
                        adeCodigos = uploadHelper.getValoresCampoFormulario("chkADE").toArray();
                    } else {
                        adeCodigos = request.getParameterValues("chkADE");
                    }
                }
            }

            if ((adeCodigos == null) || (adeCodigos.length == 0)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.selecione.ade", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos[0].toString(), responsavel);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();

            final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel);
            final boolean exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerSuspenderConsignacao();

            if (responsavel.isCsaCor() && exigeSenhaServidor) {

                if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "serAutorizacao")) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "tokenOAuth2"))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else {
                    try {
                        SenhaHelper.validarSenha(request, uploadHelper, rseCodigo, svcCodigo, false, true, false, responsavel);
                    } catch (final ZetraException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

            List<Map<String, File>> anexos = null;
            String[] visibilidadeAnexos = {CodedValues.PAP_SUPORTE, CodedValues.PAP_CONSIGNANTE, CodedValues.PAP_ORGAO, CodedValues.PAP_CONSIGNATARIA, CodedValues.PAP_CORRESPONDENTE, CodedValues.PAP_SERVIDOR};
            if (responsavel.isCseSupOrg()) {
                if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload() && (uploadHelper.getValoresCampoFormulario("aadExibe") != null)) {
                    visibilidadeAnexos = uploadHelper.getValoresCampoFormulario("aadExibe").toArray(new String[0]);
                } else {
                    visibilidadeAnexos = request.getParameterValues("aadExibe");
                }
            }

            try {
                // Salva o arquivo
                final String fileName2Senha = TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "fileName2Senha")) ? "" : JspHelper.verificaVarQryStr(request, uploadHelper, "fileName2Senha");

                if (((uploadHelper != null) && uploadHelper.hasArquivosCarregados()) || !"".equals(fileName2Senha)) {
                    anexos = new ArrayList<>(adeCodigos.length);
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    final String diretorioTemporario = diretorioRaizArquivos + File.separator + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS;
                    for (int i = 0; i < adeCodigos.length; i++) {
                        final TransferObject ade = pesquisarConsignacaoController.findAutDesconto((String) adeCodigos[i], responsavel);
                        final String path = "anexo" + File.separatorChar + DateHelper.format((Date) ade.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigos[i];
                        final File diretorioTemporarioAde = new File(diretorioTemporario + File.separatorChar + "anexo" + File.separatorChar + File.separatorChar + adeCodigos[i]);
                        if (uploadHelper.hasArquivosCarregados()) {
                            anexos.add(i, uploadHelper.salvarArquivos(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, null));
                        } else {
                            final File arquivo = new File(diretorioTemporarioAde.getPath() + File.separatorChar + fileName2Senha);
                            if (arquivo.getCanonicalPath().startsWith(arquivo.getPath())) {
                                final Map<String, File> mapAnexos = new HashMap<>();
                                mapAnexos.put(arquivo.getName(), arquivo);
                                anexos.add(i, mapAnexos);
                            } else {
                                throw new ZetraException("mensagem.erro.anexo.invalido.suspender.consignacao", responsavel);
                            }
                            final File diretorioDestino = new File(diretorioRaizArquivos + File.separator + path);
                            final File arquivoDestino = new File(diretorioDestino.getPath() + File.separator + fileName2Senha);
                            if (diretorioDestino.exists() || diretorioDestino.mkdirs()) {
                                try {
                                    FileHelper.copyFile(arquivo, arquivoDestino);
                                    final Map<String, File> mapAnexos = new HashMap<>();
                                    mapAnexos.put(arquivoDestino.getName(), arquivoDestino);
                                    anexos.add(i, mapAnexos);
                                } catch (final Exception e) {
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
                }

                if (uploadHelper != null) {
                    // Remove os arquivos carregados pois já foram copiados para as pastas corretas
                    uploadHelper.removerArquivosCarregados(responsavel);
                }

                Date dataReativacaoAutomatica = null;
                try {
                    final String dataReativacaoAutomaticaStr = JspHelper.verificaVarQryStr(request, uploadHelper, "dataReativacaoAutomatica");
                    if (!TextHelper.isNull(dataReativacaoAutomaticaStr)) {
                        dataReativacaoAutomatica = DateHelper.parse(dataReativacaoAutomaticaStr, LocaleHelper.getDatePattern());
                    }
                } catch (final ParseException ex) {
                    throw new ZetraException("mensagem.erro.data.invalida", responsavel, ex);
                }

                // POJO com parâmetros para a operação
                final SuspenderConsignacaoParametros parametros = new SuspenderConsignacaoParametros();
                parametros.setRemoveIncidenciaMargem(removeIncidenciaMargem);
                parametros.setVisibilidadeAnexos(visibilidadeAnexos);
                parametros.setDataReativacaoAutomatica(dataReativacaoAutomatica);
                parametros.setAlteraIncidenciaMargem(alteraIncidenciaMargem);
                if(alteraIncidenciaMargem) {
                    final Short marCodigo = FuncaoAlteraMargemAdeHome.findByFunCodigoAndPapCodigo(CodedValues.FUN_SUSP_CONSIGNACAO, responsavel.getPapCodigo()).getMarCodigoDestino();
                    parametros.setMarCodigo(marCodigo);
                }

                if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                    // Dados de decisão judicial
                    final String tjuCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "tjuCodigo");
                    final String cidCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "cidCodigo");
                    final String djuNumProcesso = JspHelper.verificaVarQryStr(request, uploadHelper, "djuNumProcesso");
                    final String djuData = JspHelper.verificaVarQryStr(request, uploadHelper, "djuData");
                    final String djuTexto = JspHelper.verificaVarQryStr(request, uploadHelper, "djuTexto");

                    if ((isTipoJusticaObrigatorio(responsavel) && TextHelper.isNull(tjuCodigo)) ||
                            (isComarcaJusticaObrigatorio(responsavel) && TextHelper.isNull(cidCodigo)) ||
                            (isNumeroProcessoObrigatorio(responsavel) && TextHelper.isNull(djuNumProcesso)) ||
                            (isDataDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuData)) ||
                            (isTextoDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuTexto))) {
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

                for (int i = 0; i < adeCodigos.length; i++) {
                    try {
                        CustomTransferObject tmo = null;
                        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "TMO_CODIGO"))) {
                            tmo = new CustomTransferObject();
                            tmo.setAttribute(Columns.ADE_CODIGO, adeCodigos[i]);
                            tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, uploadHelper, "TMO_CODIGO"));
                            tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_OBS"));
                            tmo.setAttribute(Columns.OCA_PERIODO, JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO"));
                        }

                        if (anexos != null) {
                            parametros.setAnexos(anexos.get(i).values());
                        }

                        suspenderConsignacaoController.suspender((String) adeCodigos[i], tmo, parametros, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.suspender.consignacao.concluido.sucesso", responsavel));

                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            final String ocaPeriodo = JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO");
                            final java.util.Date ocaPeriodDate = !TextHelper.isNull(ocaPeriodo) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;

                            autorizacaoController.criaOcorrenciaADE((String) adeCodigos[i], CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), ocaPeriodDate, (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                        }
                        if(parametros.isAlteraIncidenciaMargem()) {
                            margemController.recalculaMargemComHistorico("RSE", Arrays.asList(rseCodigo), responsavel);
                            final FuncaoAlteraMargemAde funcaoAlteraMargemAde = suspenderConsignacaoController.buscarFuncaoAlteraMargemAde(CodedValues.FUN_SUSP_CONSIGNACAO, responsavel);
                            autdes = pesquisarConsignacaoController.buscaAutorizacao((String) adeCodigos[i], responsavel);
                            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                            if((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString())) {
                                final String ocaPeriodo = JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO");
                                final java.util.Date ocaPeriodDate = !TextHelper.isNull(ocaPeriodo) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;
                                autorizacaoController.criaOcorrenciaADE((String) adeCodigos[i], CodedValues.TOC_ALTERACAO_INCIDENCIA_MARGEM_SUSPENSAO, ApplicationResourcesHelper.getMessage("mensagem.mudanca.incidencia.margem.suspensao.ocorrencia", responsavel, funcaoAlteraMargemAde.getMarCodigoOrigem().toString(), funcaoAlteraMargemAde.getMarCodigoDestino().toString()), ocaPeriodDate, responsavel);
                            }
                        }

                    } catch (final AutorizacaoControllerException ex) {
                        msg += ex.getMessage();
                        final TransferObject ade = pesquisarConsignacaoController.findAutDesconto((String) adeCodigos[i], responsavel);
                        if (TextHelper.isNull(strAdeCodigo)) {
                            msg += " " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + ": " + ade.getAttribute(Columns.ADE_NUMERO).toString();
                        }
                        msg += "<BR>";
                        session.removeAttribute(CodedValues.MSG_INFO);
                    }
                }
            } catch (final ZetraException ex) {
                msg += ex.getMessage() + "<BR>";
                session.removeAttribute(CodedValues.MSG_INFO);
            }

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            if (!TextHelper.isNull(msg)) {
                session.removeAttribute(CodedValues.MSG_INFO);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ParseException | IOException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    /**
     * Retorna TRUE se exige motivo para a suspensão da consignação
     * @param responsavel
     * @return
     */
    protected boolean exigeMotivoOperacao(AcessoSistema responsavel) {
        return ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_SUSP_CONSIGNACAO, responsavel);
    }

    /**
     * Retorna TRUE se o usuário tem permissão para suspensão avançada
     * @param responsavel
     * @return
     */
    protected boolean temPermissaoSuspensaoAvancada(AcessoSistema responsavel) {
        return (responsavel.isCseSupOrg() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) && responsavel.temPermissao(CodedValues.FUN_SUSP_AVANCADA_CONSIGNACAO);
    }

    /**
     * Retorna TRUE se exibe campo para upload de arquivo caso o usuário seja gestor ou suporte e tenha permissão para suspender e editar anexo da consignação
     * @param responsavel
     * @return
     */
    protected boolean temPermissaoAnexarSuspensao(AcessoSistema responsavel) {
        return responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);
    }
}
