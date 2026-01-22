package com.zetra.econsig.web.controller.compra;

import java.io.IOException;
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
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractEfetivarAcaoConsignacaoWebController;

/**
 * <p>Title: RetirarConsignacaoCompraWebController</p>
 * <p>Description: Controlador Web para o caso de uso RetirarConsignacaoCompra.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/retirarConsignacaoCompra" })
public class RetirarConsignacaoCompraWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RetirarConsignacaoCompraWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.retirar.consignacao.compra.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/retirarConsignacaoCompra");
        model.addAttribute("imageHeader", "i-operacional");

        try {
            if (parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel)) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/retirarConsignacaoCompra?acao=retirarContratoDeCompra";
        String funCodigo = CodedValues.FUN_RETIRAR_CONTRATO_COMPRA;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/retirarConsignacaoCompra?acao=retirarContratoDeCompra&opt=rcc";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.retirar.compra.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.retirar.compra", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.retirar.consignacao.compra.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.retirarCompra", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("RETIRAR_CONTRATO_COMPRA", CodedValues.FUN_RETIRAR_CONTRATO_COMPRA, descricao, descricaoCompleta, "retirar_contrato_compra.png", "btn", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        if (responsavel.isCseSupOrg()) {
            // Adiciona o editar consignação
            link = "../v3/retirarConsignacaoCompra?acao=detalharConsignacao";
            descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
            descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
            msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
            msgConfirmacao = "";

            acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));
        }

        return acoes;
    }

    @RequestMapping(params = { "acao=retirarContratoDeCompra" })
    public String retirar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, AutorizacaoControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (TextHelper.isNull(request.getParameter("ADE_CODIGO"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String adeCodigo = request.getParameter("ADE_CODIGO").toString();
        try {
            String obs = null;
            CustomTransferObject tmo = null;
            if (request.getParameter("TMO_CODIGO") == null) {
                obs = request.getParameter("obs");
            } else {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
            }

            compraContratoController.retirarContratoCompra(adeCodigo, obs, tmo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.retirar.consignacao.compra.concluido.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                try {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                } catch (AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new AutorizacaoControllerException(ex);
                }
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }
        } catch (CompraContratoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }
        ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";

    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "retirar_contrato_compra");
        return criterio;
    }
}
