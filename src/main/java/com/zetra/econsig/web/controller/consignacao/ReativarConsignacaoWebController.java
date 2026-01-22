package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.zetra.econsig.dto.parametros.ReativarConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
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
 * <p>Title: ReativarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ReativarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reativarConsignacao" })
public class ReativarConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReativarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private MargemController margemController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.reativar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/reativarConsignacao");
        model.addAttribute("acaoListarCidades", "reativarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("nomeCampo", "chkReativar");
        model.addAttribute("ativarVerificaoDataFim", true);
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_TEXTO_DECISAO, responsavel));
            model.addAttribute("exibirAnexo", ShowFieldHelper.showField(FieldKeysConstants.REATIVAR_CONSIGNACAO_ANEXO, responsavel));

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
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_TEXTO_DECISAO, responsavel);
    }

    @Override
    protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.REATIVAR_CONSIGNACAO_ANEXO, responsavel);
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        if (responsavel.isCseSupOrg()) {
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        }
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/reativarConsignacao?acao=efetivarAcao";
        final String linkAdicional = "../v3/reativarConsignacao?acao=confirmarReativacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.reativar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.reativar", responsavel);
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("REAT_CONSIGNACAO", CodedValues.FUN_REAT_CONSIGNACAO, descricao, descricaoCompleta, "bloqueado.gif", "btnReativarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, linkAdicional, "chkReativar"));

        // Adiciona o editar consignação
        link = "../v3/reativarConsignacao?acao=detalharConsignacao";
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
        criterio.setAttribute("TIPO_OPERACAO", "reativar");
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, responsavel)) {
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

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String urlDestino = "../v3/reativarConsignacao?acao=reativarConsignacao";
        final String funCodigo = CodedValues.FUN_REAT_CONSIGNACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return confirmarReativacao(request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=confirmarReativacao" })
    public String confirmarReativacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String adeCodigo = request.getParameter("ADE_CODIGO");
            String[] adeCodigos = request.getParameterValues("chkReativar");
            if ((adeCodigos == null) || (adeCodigos.length == 0)) {
                adeCodigos = request.getParameterValues("chkADE");
            }

            if (TextHelper.isNull(adeCodigo) && (adeCodigos == null)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.selecione.ade", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Dados dos contratos
            CustomTransferObject autdes = null;
            final List<CustomTransferObject> autdesList = new ArrayList<>();
            final Set<String> svcCodigoSet = new HashSet<>();
            String rseCodigo = null;
            String svcCodigo = null;
            String csaCodigo = null;
            String sadCodigo = null;
            String orgCodigo = null;
            Date adeAnoMesFim = null;
            Short adeIncMargem = null;
            BigDecimal adeVlr = null;

            MargemDisponivel margemDisponivel = null;
            final Map<Short, BigDecimal> margemDisponivelMap = new HashMap<>();

            boolean margemFicaraNegativa = false;
            boolean corrigeIncidenciaMargem = false;
            boolean exigeSenhaServidor = false;

            // somente exibe campo para upload de arquivo caso o usuário seja gestor ou suporte
            // e tenha permissão para reativar e editar anexo da consignação.
            final boolean temPermissaoAnexarReativar = temPermissaoAnexarReativar(responsavel);

            final List<String> adesInconsistentes = new ArrayList<>();
            boolean exibeChkIncidirNaMargem = false;
            boolean possuiAdeIncMargem = false;
            boolean possuiOcorrenciaAutorizacao = false;

            final FuncaoAlteraMargemAde funcaoAlteraMargemAde = suspenderConsignacaoController.buscarFuncaoAlteraMargemAde(CodedValues.FUN_REAT_CONSIGNACAO, responsavel);

            final boolean possuiFuncaoAlteraMargemAde = (funcaoAlteraMargemAde != null);

            if ((adeCodigos != null) && TextHelper.isNull(adeCodigo)) {
                for (final String adeCodigo2 : adeCodigos) {
                    autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo2, responsavel);
                    autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                    autdesList.add(autdes);
                    svcCodigoSet.add(autdes.getAttribute(Columns.SVC_CODIGO).toString());

                    rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
                    svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                    csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
                    sadCodigo = autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString();
                    orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                    adeAnoMesFim = DateHelper.leastDate((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM), adeAnoMesFim);
                    adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
                    adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);

                    // parâmetros do serviço
                    final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                    final Short svcIncMargem = paramSvc.getTpsIncideMargem();

                    if (!margemDisponivelMap.containsKey(svcIncMargem)) {
                        // recupera margem disponível por serviço
                        margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, null, responsavel);
                        margemDisponivelMap.put(svcIncMargem, margemDisponivel.getMargemRestante());
                    }
                    // subtrai da margem o valor do contrato
                    if (CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && ((adeIncMargem == null) || (adeIncMargem.shortValue() == 0)) && (svcIncMargem != null) && (svcIncMargem.shortValue() != 0)) {
                        corrigeIncidenciaMargem = true;
                        adesInconsistentes.add(autdes.getAttribute(Columns.ADE_NUMERO).toString());
                        margemDisponivelMap.put(svcIncMargem, margemDisponivelMap.get(svcIncMargem).subtract(adeVlr));
                        if (margemDisponivelMap.get(svcIncMargem).signum() < 0) {
                            margemFicaraNegativa = true;
                        }
                    }

                    if (!exigeSenhaServidor) {
                        exigeSenhaServidor = paramSvc.isTpsExigeSenhaSerReativarConsignacao();
                    }
                    if ((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString()) && !TextHelper.isNull(funcaoAlteraMargemAde) && autdes.getAttribute(Columns.ADE_INC_MARGEM).equals(funcaoAlteraMargemAde.getMarCodigoOrigem())) {
                        possuiAdeIncMargem = true;
                    }
                    if (!autorizacaoController.findByAdeTocCodigo(autdes.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TOC_ALTERACAO_INCIDENCIA_MARGEM_SUSPENSAO, responsavel).isEmpty()) {
                        possuiOcorrenciaAutorizacao = true;
                    }
                }
            } else {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                autdesList.add(autdes);
                svcCodigoSet.add(autdes.getAttribute(Columns.SVC_CODIGO).toString());
                // recupera margem disponível por serviço
                rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
                svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
                sadCodigo = autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString();
                orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
                adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
                adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);

                final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final Short svcIncMargem = paramSvc.getTpsIncideMargem();
                exigeSenhaServidor = paramSvc.isTpsExigeSenhaSerReativarConsignacao();

                margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, null, responsavel);
                margemDisponivelMap.put(svcIncMargem, margemDisponivel.getMargemRestante());

                if (CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && ((adeIncMargem == null) || (adeIncMargem.shortValue() == 0)) && (svcIncMargem != null) && (svcIncMargem.shortValue() != 0) && margemDisponivelMap.containsKey(svcIncMargem)) {
                    corrigeIncidenciaMargem = true;
                    adesInconsistentes.add(autdes.getAttribute(Columns.ADE_NUMERO).toString());
                    margemDisponivelMap.put(svcIncMargem, margemDisponivelMap.get(svcIncMargem).subtract(adeVlr));
                    if (margemDisponivelMap.get(svcIncMargem).signum() < 0) {
                        margemFicaraNegativa = true;
                    }
                }
                if ((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString())) {
                    possuiAdeIncMargem = true;
                }
                if (!autorizacaoController.findByAdeTocCodigo(autdes.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TOC_ALTERACAO_INCIDENCIA_MARGEM_SUSPENSAO, responsavel).isEmpty()) {
                    possuiOcorrenciaAutorizacao = true;
                }
            }

            if (possuiFuncaoAlteraMargemAde && possuiOcorrenciaAutorizacao) {
                final MargemTO margem = new MargemTO(funcaoAlteraMargemAde.getMarCodigoDestino());

                final String descricaoMargemDestino = margemController.findMargem(margem, responsavel).getMarDescricao();

                exibeChkIncidirNaMargem = podeIncidirNaMargem(possuiFuncaoAlteraMargemAde, possuiAdeIncMargem, responsavel);

                if (exibeChkIncidirNaMargem) {
                    model.addAttribute("descricaoMargemDestino", descricaoMargemDestino);
                }
            }

            // verifica se o usuário tem permissão de executar a função dada para um Serviço
            final String funCodigo = CodedValues.FUN_REAT_CONSIGNACAO;
            for (final String element : svcCodigoSet) {
                svcCodigo = element;
                if (!AcessoFuncaoServico.temAcessoFuncao(request, funCodigo, responsavel.getUsuCodigo(), svcCodigo)) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Busca os períodos para o combo de seleção do período da operação
            final Set<Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);

            String termoAceite = ApplicationResourcesHelper.getMessage("mensagem.reativacao.contrato.termoaceite", responsavel);
            if ((adesInconsistentes != null) && !adesInconsistentes.isEmpty()) {
                String adesNumeros = null;
                for (final String s : adesInconsistentes) {
                    if (adesNumeros == null) {
                        adesNumeros = s;
                    } else {
                        adesNumeros = adesNumeros.concat(", ").concat(s);
                    }
                }
                termoAceite = termoAceite.concat(" (" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + ": ").concat(adesNumeros).concat(")");
            }

            //Verificamos da lista qual dos contratos possuem ocorrência de suspensão por parcela rejeitada
            if (ParamSist.getBoolParamSist(CodedValues.TPC_REATIVAR_CONTRATO_SUSP_PRD_REJEITADA_EXIGE_CONF_GESTOR, responsavel) && ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel) && exigeSenhaServidor) {
                final List<String> adeCodigosFiltrar = new ArrayList<>();
                if ((adeCodigos != null) && TextHelper.isNull(adeCodigo)) {
                    adeCodigosFiltrar.addAll(Arrays.asList(adeCodigos));
                } else {
                    adeCodigosFiltrar.add(adeCodigo);
                }

                final List<TransferObject> adeCodigosSuspensoRejeitadaFolha = suspenderConsignacaoController.verificaContratosForamSuspensosPrdRejeitada(adeCodigosFiltrar, responsavel);
                if ((adeCodigosSuspensoRejeitadaFolha != null) && !adeCodigosSuspensoRejeitadaFolha.isEmpty()) {
                    final StringBuilder adeNumerosRejeitada = new StringBuilder();
                    String separador = "";
                    for (final TransferObject ade : adeCodigosSuspensoRejeitadaFolha) {
                        adeNumerosRejeitada.append(separador).append(ade.getAttribute(Columns.ADE_CODIGO).toString());
                        separador = ",";
                    }
                    model.addAttribute("adesParcelaRejeitada", adeNumerosRejeitada.toString());
                }
            }

            model.addAttribute("lstConsignacao", autdesList);
            model.addAttribute("temPermissaoAnexarReativar", temPermissaoAnexarReativar);
            model.addAttribute("margemFicaraNegativa", margemFicaraNegativa);
            model.addAttribute("periodos", periodos);
            model.addAttribute("termoAceite", termoAceite);
            model.addAttribute("corrigeIncidenciaMargem", corrigeIncidenciaMargem);
            model.addAttribute("exigeSenhaServidor", responsavel.isCsaCor() && exigeSenhaServidor);
            model.addAttribute("exibeChkIncidirNaMargem", exibeChkIncidirNaMargem);

            final List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);
            model.addAttribute("lstTipoJustica", lstTipoJustica);

            if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && !TextHelper.isNull(ApplicationResourcesHelper.getMessage("mensagem.decisao.judicial.opcao.reativar.consignacao", responsavel))) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.decisao.judicial.opcao.reativar.consignacao", responsavel));
            }

            return viewRedirect("jsp/reativarConsignacao/confirmarReativacao", request, session, model, responsavel);

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    protected boolean podeIncidirNaMargem(boolean possuiFuncaoAlteraMargemAde, boolean possuiAdeIncMargem, AcessoSistema responsavel) {
        return possuiFuncaoAlteraMargemAde && possuiAdeIncMargem && ParamSist.paramEquals(CodedValues.TPC_POSSIBILIDADE_INCIDIR_MARGEM_SUSPENSAO_REATIVACAO, CodedValues.TPC_SIM, responsavel);
    }

    @RequestMapping(params = { "acao=reativarConsignacao" })
    public String reativar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            final int tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;
            UploadHelper uploadHelper = null;
            try {
                uploadHelper = new UploadHelper();
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean alteraIncidenciaMargem = Boolean.parseBoolean(JspHelper.verificaVarQryStr(request, uploadHelper, "incidirNaMargem"));

            String msg = "";
            String msgSucesso = "";
            Object[] adeCodigos = null;

            if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO"))) {
                if ((uploadHelper != null) && uploadHelper.isRequisicaoUpload()) {
                    adeCodigos = uploadHelper.getValoresCampoFormulario("chkReativar").toArray();
                } else {
                    adeCodigos = request.getParameterValues("chkReativar");
                    if (TextHelper.isNull(adeCodigos)) {
                        adeCodigos = request.getParameterValues("chkADE");
                    }
                }
            } else {
                adeCodigos = new String[1];
                adeCodigos[0] = JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO");
            }

            if (TextHelper.isNull(adeCodigos)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String adeCodigosSuspensoRejeitadaFolha = JspHelper.verificaVarQryStr(request, uploadHelper, "adeCodigosSuspensoRejeitadaFolha");
            final String confirmRetornoDesconto = JspHelper.verificaVarQryStr(request, uploadHelper, "confirmRetornoDesconto");

            if (!TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha) && TextHelper.isNull(confirmRetornoDesconto)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.reativar.consignacao.confirmacao.retorno.desconto.servidor.obrigatorio", responsavel));
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

            final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final boolean exigeSenhaServidor = paramSvcTo.isTpsExigeSenhaSerReativarConsignacao();

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
            String[] visibilidadeAnexos = { CodedValues.PAP_SUPORTE, CodedValues.PAP_CONSIGNANTE, CodedValues.PAP_ORGAO, CodedValues.PAP_CONSIGNATARIA, CodedValues.PAP_CORRESPONDENTE, CodedValues.PAP_SERVIDOR };
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
                                throw new ZetraException("mensagem.erro.anexo.invalido.reativar.consignacao", responsavel);
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
                                    throw new ZetraException("mensagem.erro.upload.anexo.reativar.consignacao", responsavel);
                                }
                            } else {
                                throw new ZetraException("mensagem.erro.upload.anexo.reativar.consignacao", responsavel);
                            }
                            FileHelper.deleteDir(diretorioTemporarioAde.getPath());
                        }
                    }
                }

                if (uploadHelper != null) {
                    // Remove os arquivos carregados pois já foram copiados para as pastas corretas
                    uploadHelper.removerArquivosCarregados(responsavel);
                }

                // POJO com parâmetros para a operação
                final ReativarConsignacaoParametros parametros = new ReativarConsignacaoParametros();
                parametros.setVisibilidadeAnexos(visibilidadeAnexos);
                parametros.setAlteraIncidenciaMargem(alteraIncidenciaMargem);
                if (alteraIncidenciaMargem) {
                    final Short marCodigo = FuncaoAlteraMargemAdeHome.findByFunCodigoAndPapCodigo(CodedValues.FUN_REAT_CONSIGNACAO, responsavel.getPapCodigo()).getMarCodigoDestino();
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
                        // Se informado, pelo menos tipo de justiça, texto e data devem ser informados.
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

                        suspenderConsignacaoController.reativar((String) adeCodigos[i], tmo, parametros, responsavel);

                        final TransferObject ade = pesquisarConsignacaoController.findAutDesconto((String) adeCodigos[i], responsavel);
                        if (adeCodigos.length > 1) {
                            msgSucesso += " " + ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.reativada", responsavel) + ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + ": " + ade.getAttribute(Columns.ADE_NUMERO).toString();
                            if (!TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha) && adeCodigosSuspensoRejeitadaFolha.contains((String) adeCodigos[i])) {
                                msgSucesso += " " + ApplicationResourcesHelper.getMessage("mensagem.reativar.consignacao.aguardando.confirmacao.gestor", responsavel, visibilidadeAnexos);
                            }
                            msgSucesso += "<BR>";
                        } else {
                            msgSucesso = ApplicationResourcesHelper.getMessage("mensagem.reativar.consignacao.concluido.sucesso", responsavel);
                            if (!TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha) && adeCodigosSuspensoRejeitadaFolha.contains((String) adeCodigos[i])) {
                                msgSucesso += " " + ApplicationResourcesHelper.getMessage("mensagem.reativar.consignacao.aguardando.confirmacao.gestor", responsavel, visibilidadeAnexos);
                            }
                        }

                        if (!TextHelper.isNull(adeCodigosSuspensoRejeitadaFolha) && adeCodigosSuspensoRejeitadaFolha.contains((String) adeCodigos[i]) && responsavel.isCsaCor() && exigeSenhaServidor) {
                            autorizacaoController.criaOcorrenciaADE((String) adeCodigos[i], CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pela.senha.do.servidor", responsavel), responsavel);
                        }

                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            final String ocaPeriodo = JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO");
                            final java.util.Date ocaPeriodDate = !TextHelper.isNull(ocaPeriodo) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;

                            autorizacaoController.criaOcorrenciaADE((String) adeCodigos[i], CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), ocaPeriodDate, (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                        }
                        if (parametros.isAlteraIncidenciaMargem()) {
                            margemController.recalculaMargemComHistorico("RSE", Arrays.asList(rseCodigo), responsavel);
                            final FuncaoAlteraMargemAde funcaoAlteraMargemAde = suspenderConsignacaoController.buscarFuncaoAlteraMargemAde(CodedValues.FUN_REAT_CONSIGNACAO, responsavel);
                            autdes = pesquisarConsignacaoController.buscaAutorizacao((String) adeCodigos[i], responsavel);
                            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                            if ((autdes.getAttribute(Columns.ADE_INC_MARGEM) != null) && !"0".equals(autdes.getAttribute(Columns.ADE_INC_MARGEM).toString())) {
                                final String ocaPeriodo = JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO");
                                final java.util.Date ocaPeriodDate = !TextHelper.isNull(ocaPeriodo) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;
                                autorizacaoController.criaOcorrenciaADE((String) adeCodigos[i], CodedValues.TOC_ALTERACAO_INCIDENCIA_MARGEM_REATIVACAO, ApplicationResourcesHelper.getMessage("mensagem.mudanca.incidencia.margem.reativacao.ocorrencia", responsavel, funcaoAlteraMargemAde.getMarCodigoOrigem().toString(), funcaoAlteraMargemAde.getMarCodigoDestino().toString()), ocaPeriodDate, responsavel);
                            }
                        }
                    } catch (final AutorizacaoControllerException ex) {
                        msg += ex.getMessage();
                        final TransferObject ade = pesquisarConsignacaoController.findAutDesconto((String) adeCodigos[i], responsavel);
                        if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "ADE_CODIGO"))) {
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

            try {
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
                    // Inclui alerta na sessão do usuário se o período usado só permite reduções
                    java.sql.Date ocaPeriodo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, uploadHelper, "OCA_PERIODO"), "yyyy-MM-dd")) : null;
                    if (ocaPeriodo == null) {
                        ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
                    }
                    final java.sql.Date ocaPeriodoValido = PeriodoHelper.getInstance().validarAdeAnoMesIni(null, DateHelper.toSQLDate(ocaPeriodo), responsavel);
                    if (!ocaPeriodoValido.equals(ocaPeriodo)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.data.base.operacao.ajustada.periodo.apenas.reducoes", responsavel));
                    }
                }
            } catch (final PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!TextHelper.isNull(msg) && !TextHelper.isNull(msgSucesso)) {
                session.setAttribute(CodedValues.MSG_INFO, msgSucesso);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            } else if (!TextHelper.isNull(msg) && TextHelper.isNull(msgSucesso)) {
                session.removeAttribute(CodedValues.MSG_INFO);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            } else {
                session.setAttribute(CodedValues.MSG_INFO, msgSucesso);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ParseException | IOException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    protected boolean temPermissaoAnexarReativar(AcessoSistema responsavel) {
        return responsavel.temPermissao(CodedValues.FUN_REAT_CONSIGNACAO) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);
    }
}
