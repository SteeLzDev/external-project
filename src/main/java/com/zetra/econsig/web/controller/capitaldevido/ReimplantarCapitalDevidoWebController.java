package com.zetra.econsig.web.controller.capitaldevido;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractListarTodasConsignacoesWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReimplantarCapitalDevidoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ReimplantarCapitalDevido.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reimplantarCapitalDevido" })
public class ReimplantarCapitalDevidoWebController extends AbstractListarTodasConsignacoesWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReimplantarCapitalDevidoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.reimplantar.capital.devido.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/reimplantarCapitalDevido");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        final String link = "../v3/reimplantarCapitalDevido?acao=iniciarReimplantacao&_skip_history_=true";
        final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.reimplantar.capital.devido.abreviado", responsavel);
        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.reimplantar.capital.devido.clique.aqui", responsavel);
        final String msgConfirmacao = "";
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("REIMPLANTAR_CAPITAL_DEVIDO", CodedValues.FUN_REIMPLANTAR_CAPITAL_DEVIDO, descricao, "reimplantar.png", "btnReimplantarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "reimplantar_capital_devido");
        return criterio;
    }

    @RequestMapping(params = { "acao=iniciarReimplantacao" })
    public String iniciarReimplantacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Salva o Token de sincronização para evitar duplo request
        SynchronizerToken.saveToken(request);

        final String adeCodigo = request.getParameter("ADE_CODIGO");

        BigDecimal capitalDevido = null;

        //Busca o contrato a ser alterado
        CustomTransferObject autdes = null;

        try {
            capitalDevido = reimplantarConsignacaoController.calcularCapitalDevido(adeCodigo, responsavel);
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (capitalDevido == null) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sem.capital.devido.a.reimplantar", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();

        final BigDecimal adeVlrAtual = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);

        final boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
        final String rotuloPeriodicidadePrazo = quinzenal ? "" : "(" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")";

        ParamSvcTO paramSvcCse = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
        final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);

        List<TransferObject> lstMtvOperacao = null;
        try {
            lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
        } catch (final TipoMotivoOperacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("capitalDevido", capitalDevido);
        model.addAttribute("autdes", autdes);
        model.addAttribute("adeVlrAtual", adeVlrAtual);
        model.addAttribute("rotuloPeriodicidadePrazo", rotuloPeriodicidadePrazo);
        model.addAttribute("labelTipoVlr", labelTipoVlr);
        model.addAttribute("lstMtvOperacao", lstMtvOperacao);

        return viewRedirect("jsp/reimplantarCapitalDevido/reimplantarCapitalDevido", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=confirmarReimplantacao" })
    public String confirmarReimplantacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String adeCodigo = request.getParameter("ADE_CODIGO");
        final String adeVlrStr = JspHelper.verificaVarQryStr(request, "adeVlr");

        //Busca o contrato a ser alterado
        CustomTransferObject autdes = null;

        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (TextHelper.isNull(adeVlrStr)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ade.valor", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String novoPrazoStr = JspHelper.verificaVarQryStr(request, "adePrazoEdt");
        if (TextHelper.isNull(novoPrazoStr)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ade.prazo", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        BigDecimal adeVlr;
        try {
            adeVlr = new BigDecimal(NumberHelper.reformat(adeVlrStr, NumberHelper.getLang(), "en"));
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }
        final Integer novoPrazo = Integer.valueOf(novoPrazoStr);

        if (novoPrazo == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.reimp.capital.devidor.prazo.zerado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
        final String ocaObs = JspHelper.verificaVarQryStr(request, "ocaObs");

        if (TextHelper.isNull(tmoCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            reimplantarConsignacaoController.reimplantarCapitalDevido(autdes, adeVlr, novoPrazo, tmoCodigo, ocaObs, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
            try {
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
            session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reimplantar.capital.devido.sucesso", responsavel));

        // Volta um passo antes para que o redirecionamento seja feito para a tela de listagem de ADE
        paramSession.halfBack();

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

}
