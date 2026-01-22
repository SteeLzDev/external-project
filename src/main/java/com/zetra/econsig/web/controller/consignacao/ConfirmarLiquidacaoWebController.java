package com.zetra.econsig.web.controller.consignacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: ConfirmarLiquidacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ConfirmarLiquidacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/confirmarLiquidacao" })
public class ConfirmarLiquidacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarLiquidacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        carregarListaServico(request, session, model, responsavel);

        // Habilita exibição de campo para filtro por data
        model.addAttribute("exibirFiltroDataInclusao", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/confirmarLiquidacao?acao=confirmar";
        String funCodigo = CodedValues.FUN_CONF_LIQUIDACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token de sessão para evitar a chamada direta à operação
            if (request.getParameter("ADE_CODIGO") != null && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String adeCodigo = null;
            if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else {
                adeCodigo = request.getParameter("ADE_CODIGO").toString();
            }

            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_CONF_LIQUIDACAO, responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject tmo = null;
            if (request.getParameter("TMO_CODIGO") != null) {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                tmo.setAttribute(Columns.OCA_PERIODO, JspHelper.verificaVarQryStr(request, "OCA_PERIODO"));
            }

            java.util.Date ocaPeriodoDate = null;
            String ocaPeriodo = JspHelper.verificaVarQryStr(request, "OCA_PERIODO");

            if (!TextHelper.isNull(ocaPeriodo)) {
                ocaPeriodoDate = (!TextHelper.isNull(ocaPeriodo)) ? DateHelper.parse(ocaPeriodo, "yyyy-MM-dd") : null;
            }

            LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
            parametrosLiquidacao.setOcaPeriodo(ocaPeriodoDate);

            liquidarConsignacaoController.liquidar(adeCodigo, tmo, parametrosLiquidacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.concluido.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), ocaPeriodoDate, (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            if (responsavel.isCsaCor()) {
                // Verifica se a consignatária pode ser desbloqueada automaticamente
                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.csa.desbloqueada.automaticamente", responsavel));
                }

                if (autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_AGUARD_LIQUI_COMPRA)) {
                    // Executa o desbloqueio automático também para os contratos resultantes da compra.
                    // Evita que caso alguma consignatária tenha sido bloqueada por não informação
                    // de pagamento de saldo devedor ela seja desbloqueada, se possível, em função da
                    // liquidação do contrato originário.
                    List<String> adesDestinoCompra = compraContratoController.recuperarAdesCodigosDestinoCompra(adeCodigo);
                    compraContratoController.executarDesbloqueioAutomaticoConsignatarias(adesDestinoCompra, responsavel);
                }
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.confirmar.liquidacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/confirmarLiquidacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/confirmarLiquidacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.liquidar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.confirmar.liquidacao.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("CONF_LIQUIDACAO", CodedValues.FUN_CONF_LIQUIDACAO, descricao, descricaoCompleta, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // DESENV-12090: Adiciona opção para cancelar solicitação de liquidação
        link = "../v3/solicitarLiquidacao?acao=efetivarCancelamentoSolicitacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.solicitar.liquidacao.abrev", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.solicitar.liquidacao", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.solicitar.liquidacao.consignacao.clique.aqui", responsavel);
        msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelar.solicitar.liquidacao", responsavel);
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("CANC_SOLICITACAO_LIQUIDACAO", CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO, descricao, descricaoCompleta, "cancelar.gif", "btnCancelarSolicitacaoLiquidacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = "../v3/confirmarLiquidacao?acao=detalharConsignacao";
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
        criterio.setAttribute("TIPO_OPERACAO", "confirmar_liquidacao");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        try {
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("")) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
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
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }
}
