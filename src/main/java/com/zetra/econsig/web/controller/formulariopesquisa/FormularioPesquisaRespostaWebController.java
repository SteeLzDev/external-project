package com.zetra.econsig.web.controller.formulariopesquisa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.FormularioPesquisaResposta;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaRespostaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = { "/v3/formularioResposta" })
public class FormularioPesquisaRespostaWebController extends ControlePaginacaoWebController {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
			.getLog(ManterFormularioPesquisaWebController.class);

	@Autowired
	public FormularioPesquisaController formularioPesquisaController;

	@Autowired
	public FormularioPesquisaRespostaController formularioPesquisaRespostaController;

	@Autowired
	public UsuarioController usuarioController;

	@Autowired
	public ServidorController servidorController;

	@RequestMapping(method = RequestMethod.POST, params = { "acao=responder" })
	public String responderFormularioPesquisa(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Model model) {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		if (!SynchronizerToken.isTokenValid(request)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper
					.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
					responsavel);
		}

		FormularioPesquisaTO formulario = null;
		try {
			String fpeCodigo = formularioPesquisaController.verificaFormularioParaResponder(responsavel.getUsuCodigo(),
					responsavel);
			formulario = formularioPesquisaController.findByPrimaryKey(fpeCodigo);
		} catch (final FindException | FormularioPesquisaControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
					responsavel);
		}

		if (Boolean.TRUE.equals(formulario.isFpeBloqueiaSistema())) {
			session.setAttribute("formularioObrigatorio", "1");
		}

		model.addAttribute("obrigatorio", formulario.isFpeBloqueiaSistema());
		model.addAttribute("formulario", formulario);
		return viewRedirect("jsp/manterFormularioPesquisaResposta/manterFormularioPesquisaResposta", request,
				session, model, responsavel);
	}

	/**
	 * Salva respostas
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = { "/salvarResposta" })
	@ResponseBody
	public ResponseEntity<String> salvarResposta(@RequestParam(value = "json", required = true) String json,
			@RequestParam(value = "fpeCodigo", required = true) String fpeCodigo, HttpSession session,
			HttpServletRequest request) {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		// Valida o token de sessão para evitar a chamada direta à operação
		if (!SynchronizerToken.isTokenValid(request)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		try {
			FormularioPesquisaResposta formResposta = new FormularioPesquisaResposta();
			formResposta.setFprDtCriacao(DateHelper.getSystemDatetime());
			formResposta.setFprJson(json);
			formResposta.setFpeCodigo(fpeCodigo);
			formResposta.setUsuCodigo(responsavel.getUsuCodigo());
			formularioPesquisaRespostaController.createFormularioPesquisaResposta(formResposta);
			session.removeAttribute("formularioObrigatorio");
		} catch (final CreateException e) {
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<>("ERRO", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, params = { "acao=mobile" })
	public String mobile(@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "usuCodigo", required = true) String usuCodigo,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {
			UsuarioChaveSessao usuChaveSessao = usuarioController.validateToken(token);
			if (!usuChaveSessao.getUsuCodigo().equals(usuCodigo)) {
				session.setAttribute(CodedValues.MSG_ERRO,
						ApplicationResourcesHelper.getMessage(
								"mensagem.erro.interno.contate.administrador",
								responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session,
						model,
						responsavel);
			}
			FormularioPesquisaTO formulario = null;
			try {
				String fpeCodigo = formularioPesquisaController.verificaFormularioParaResponder(
						usuChaveSessao.getUsuCodigo(), responsavel);
				formulario = formularioPesquisaController.findByPrimaryKey(fpeCodigo);
			} catch (final FindException | FormularioPesquisaControllerException ex) {
				LOG.error(ex.getMessage(), ex);
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.encontrar.formulario.pesquisa", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session,
						model,
						responsavel);
			}

			if (Boolean.TRUE.equals(formulario.isFpeBloqueiaSistema())) {
				session.setAttribute("formularioObrigatorio", "1");
			}

			model.addAttribute("obrigatorio", formulario.isFpeBloqueiaSistema());
			model.addAttribute("formulario", formulario);
			model.addAttribute("token", token);
			model.addAttribute("usuCodigo", usuCodigo);
			return viewRedirect(
					"jsp/manterFormularioPesquisaResposta/manterFormularioPesquisaRespostaMobile",
					request,
					session, model, responsavel);

		} catch (final UsuarioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO,
					ApplicationResourcesHelper.getMessage(
							"mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model,
					responsavel);
		}
	}

	/**
	 * Salva respostas Mobile
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = { "/salvarRespostaMobile" })
	@ResponseBody
	public ResponseEntity<String> salvarRespostaMobile(@RequestParam(value = "json", required = true) String json,
			@RequestParam(value = "fpeCodigo", required = true) String fpeCodigo,
			@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "usuCodigo", required = true) String usuCodigo,
			HttpSession session, HttpServletRequest request, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		try {
			UsuarioChaveSessao usuChaveSessao = usuarioController.validateToken(token);
			if (!usuChaveSessao.getUsuCodigo().equals(usuCodigo)) {
				session.setAttribute(CodedValues.MSG_ERRO,
						ApplicationResourcesHelper.getMessage(
								"mensagem.erro.interno.contate.administrador",
								responsavel));
				return new ResponseEntity<>("ERRO", HttpStatus.BAD_REQUEST);
			}

			FormularioPesquisaResposta formResposta = new FormularioPesquisaResposta();
			formResposta.setFprDtCriacao(DateHelper.getSystemDatetime());
			formResposta.setFprJson(json);
			formResposta.setFpeCodigo(fpeCodigo);
			formResposta.setUsuCodigo(usuChaveSessao.getUsuCodigo());
			formularioPesquisaRespostaController.createFormularioPesquisaResposta(formResposta);
			session.removeAttribute("formularioObrigatorio");

			return new ResponseEntity<>(formResposta.toString(), HttpStatus.OK);
		} catch (final CreateException ex) {
			LOG.error(ex.getMessage(), ex);
			return new ResponseEntity<>("ERRO", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final UsuarioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			return new ResponseEntity<>("ERRO", HttpStatus.BAD_REQUEST);
		}
	}
}