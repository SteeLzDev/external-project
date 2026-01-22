package com.zetra.econsig.web.controller.motivoreclamacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReclamacaoRegistroServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ReclamacaoRegistroServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>
 * Title: ManterMotivoReclamacaoWebController
 * </p>
 * <p>
 * Description: Controlador Web responsável por manter motivo reclamação.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2021
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author: $ $Revision: $ $Date: $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/motivoReclamacao" })
public class ManterMotivoReclamacaoWebController extends ControlePaginacaoWebController {

	@Autowired
	private ReclamacaoRegistroServidorController rrsController;

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {

			SynchronizerToken.saveToken(request);

			int total = rrsController.countTipoMotivoReclamacao(-1, -1, responsavel);
			int size = JspHelper.LIMITE;
			int offset = 0;
			try {
				offset = Integer.parseInt(request.getParameter("offset"));
			} catch (Exception ex) {}

			// Monta lista de parâmetros através dos parâmetros de request
			Set<String> params = new HashSet<>(request.getParameterMap().keySet());

			// Ignora os parâmetros abaixo
			params.remove("offset");
			params.remove("back");
			params.remove("linkRet");
			params.remove("linkRet64");
			params.remove("eConsig.page.token");
			params.remove("_skip_history_");
			params.remove("pager");
			params.remove("acao");

			List<String> requestParams = new ArrayList<>(params);
			String linkAction = request.getRequestURI() + "?acao=iniciar";
			configurarPaginador(linkAction, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

			List<TransferObject> motivoReclamacao = rrsController.lstTipoMotivoReclamacao(offset, size, responsavel);
			model.addAttribute("motivoReclamacao", motivoReclamacao);

			return viewRedirect("jsp/manterMotivoReclamacao/listarMotivoReclamacao", request, session, model, responsavel);
		} catch (ReclamacaoRegistroServidorControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	@RequestMapping(params = { "acao=salvar" })
	public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {

			//Valida o token de sessão para evitar a chamada direta à operação
			if (!SynchronizerToken.isTokenValid(request)) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}

			SynchronizerToken.saveToken(request);
			String tmr_codigo  = request.getParameter("tmrCodigo");

			if (!TextHelper.isNull(tmr_codigo)) {
				// Atualiza o motivo reclamação.
				CustomTransferObject motivoReclamacao = new CustomTransferObject();
				motivoReclamacao.setAttribute(Columns.TMR_CODIGO, tmr_codigo);
				motivoReclamacao.setAttribute(Columns.TMR_DESCRICAO, JspHelper.verificaVarQryStr(request, "tmrDescricao"));

				rrsController.updateTipoMotivoReclamacao(motivoReclamacao, responsavel);
				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.tipo.motivo.reclamacao.sucesso", responsavel));
			} else {
				// Insere o motivo reclamação.
				tmr_codigo = rrsController.createTipoMotivoReclamacao(JspHelper.verificaVarQryStr(request, "tmrDescricao"), responsavel);
				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.criar.tipo.motivo.reclamacao.sucesso", responsavel));
			}

			TransferObject motivoReclamacao = null;
			if (!TextHelper.isNull(tmr_codigo)) {
				try {
					motivoReclamacao = rrsController.findTipoMotivoReclamacao(tmr_codigo, responsavel);
				} catch (Exception ex) {
					session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
				}
			}

			model.addAttribute("motivoReclamacao", motivoReclamacao);
			model.addAttribute("tmr_codigo", tmr_codigo);

			return iniciar(request, response, session, model);
		} catch (Exception ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	@RequestMapping(params = { "acao=editar" })
	public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {

			//Valida o token de sessão para evitar a chamada direta à operação
			if (!SynchronizerToken.isTokenValid(request)) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}

			String tmr_codigo  = request.getParameter("tmrCodigo");

			TransferObject motivoReclamacao = null;
			if (!TextHelper.isNull(tmr_codigo)) {
				try {
					motivoReclamacao = rrsController.findTipoMotivoReclamacao(tmr_codigo, responsavel);
				} catch (Exception ex) {
					session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
					return iniciar(request, response, session, model);
				}
			}

			model.addAttribute("motivoReclamacao", motivoReclamacao);
			model.addAttribute("tmr_codigo", tmr_codigo);

			return viewRedirect("jsp/manterMotivoReclamacao/editarMotivoReclamacao", request, session, model, responsavel);

		} catch (InstantiationException | IllegalAccessException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

	}

	@RequestMapping(params = { "acao=excluir" })
	public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {

			//Valida o token de sessão para evitar a chamada direta à operação
			if (!SynchronizerToken.isTokenValid(request)) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}

			SynchronizerToken.saveToken(request);

			String tmr_codigo  = request.getParameter("tmrCodigo");

			// Exclui o motivo reclamacão
			rrsController.removeTipoMotivoReclamacao(tmr_codigo, responsavel);
			session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.tipo.motivo.reclamacao.sucesso", responsavel));

		} catch (Exception ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/motivoReclamacao?acao=iniciar", request)));
		return "jsp/redirecionador/redirecionar";

	}

}
