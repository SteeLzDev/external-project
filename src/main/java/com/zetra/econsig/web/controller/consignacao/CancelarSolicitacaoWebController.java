package com.zetra.econsig.web.controller.consignacao;

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
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: CancelarSolicitacaoWebController </p>
 * <p> Description: Controlador Web para o caso de uso Cancelar Solicitacao. </p>
 * <p> Copyright: Copyright (c) 2002-2017 </p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarSolicitacao" })
public class CancelarSolicitacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarSolicitacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        // Adiciona ao model as informações específicas da operação
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.cancReserva", responsavel));
        model.addAttribute("msgConfirmacao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		String urlDestino = "../v3/cancelarSolicitacao?acao=cancelar";
        String funCodigo = CodedValues.FUN_CANC_SOLICITACAO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
	}

	@RequestMapping(params = { "acao=cancelar" })
	public String cancelar(HttpServletRequest request, HttpServletResponse response, HttpSession session,Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
			ParamSession paramSession = ParamSession.getParamSession(session);

			// Valida o token de sessão para evitar a chamada direta à operação
			if (!SynchronizerToken.isTokenValid(request)) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);// RETORNAR
			}
			SynchronizerToken.saveToken(request);

			if (request.getParameter("ADE_CODIGO") == null || responsavel.getUsuCodigo() == null) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);// RETORNAR
			}

			try {
	            String adeCodigo = request.getParameter("ADE_CODIGO").toString();

	            CustomTransferObject tipoMotivoOperacao = null;
				if (request.getParameter("TMO_CODIGO") != null) {
					tipoMotivoOperacao = new CustomTransferObject();
					tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
					tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
					tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
				}

				cancelarConsignacaoController.cancelar(adeCodigo, tipoMotivoOperacao, responsavel);
				// verifica desbloqueio automático de consignatária no cancelamento manual
				CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
				String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
				consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);

				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.concluido.sucesso", responsavel));

				if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
					autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
					(String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
					(AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
					session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
					session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
				}

			} catch (AutorizacaoControllerException mae) {
				session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
	            LOG.error(mae);
			}

			request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
			return "jsp/redirecionador/redirecionar";

		} catch (ConsignatariaControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			LOG.error(ex.getMessage(), ex);
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}
}