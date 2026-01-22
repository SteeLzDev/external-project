package com.zetra.econsig.web.controller.mensagem;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>
 * Title: ConfirmarMensagemWebController
 * </p>
 * <p>
 * Description: Controlador Web para o caso de uso Confirmar Mensagem.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2017
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author$ $Revision$ $Date$
 */
@Controller
@RequestMapping(value = { "/v3/confirmarMensagem" })
public class ConfirmarMensagemWebController extends AbstractWebController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarMensagemWebController.class);

	@Autowired
	private ConsignatariaController consignatariaController;

	@Autowired
	private MensagemController mensagemController;

	// Resolve a WARN abaixo quando é feito um Refresh (F5) na tela de confirmação de mensagem, visto que o recurso não tem ACR_PARAMETRO/ACR_OPERACAO cadastrados
	// org.springframework.web.bind.UnsatisfiedServletRequestParameterException: Parameter conditions "acao=salvar" OR "acao=iniciar" not met for actual request parameters:
	@RequestMapping(method = RequestMethod.GET)
	public String redirecionar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
	    return iniciar(request, response, session, model);
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		int menDias = 0;
		Object paramMenLidaIndividualmente = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_PARA_LEITURA_DE_MENSAGEM_INDIVIDUALMENTE, responsavel);
		int paramMenLidaIndividualmenteInt = 0;

		if (TextHelper.isNum(paramMenLidaIndividualmente)) {
			paramMenLidaIndividualmenteInt = Integer.valueOf(paramMenLidaIndividualmente.toString());
		}

		model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.mensagem.plural", responsavel));
		try {
			CustomTransferObject criterio = new CustomTransferObject();
			String usuDataCad = request.getParameter("usu_data_cad");
			if (TextHelper.isNull(usuDataCad)) {
				usuDataCad = session.getAttribute("usu_data_cad").toString();
			}
			criterio.setAttribute(Columns.USU_DATA_CAD, DateHelper.parse(usuDataCad, "yyyy-MM-dd"));

			List<MensagemTO> mensagens = mensagemController.lstMensagemUsuarioSemLeitura(criterio, responsavel);
			if (responsavel.isSer() || responsavel.isCseOrg()) {
				for (MensagemTO menTO : mensagens) {
					menDias = DateHelper.dayDiff(menTO.getMenData());
					menDias = menDias < 0 ? menDias * -1 : menDias;
					if(menDias < paramMenLidaIndividualmenteInt) {
						menTO.setMenLidaIndividualmente("S");
					} else if (menDias >= paramMenLidaIndividualmenteInt) {
						menTO.setMenLidaIndividualmente("N");
					}
				}
			}
			// Salva quantidade de mensagens sem leitura na sessão
			session.setAttribute("mensagem_sem_leitura", Integer.valueOf(mensagens.size()));
			// Inclui no model as mensagens que devem ser exibidas na interface
			model.addAttribute("mensagens", mensagens);
			model.addAttribute("usu_data_cad", usuDataCad);
			model.addAttribute("paramMenLidaIndividualmenteInt", paramMenLidaIndividualmenteInt);

			return viewRedirect("jsp/confirmarMensagem/confirmarMensagem", request, session, model, responsavel);
		} catch (ParseException | MensagemControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=salvar" })
	public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // DESENV-19985 : Redireciona usuário para página de erro pois este método deve vir de um POST da página de mensagens não lidas
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return "forward:/v3/exibirMensagem?acao=exibirMsgSessao&" + SynchronizerToken.generateToken4URL(request);
	    }

		int menDias = 0;

		Object paramMenLidaIndividualmente = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_PARA_LEITURA_DE_MENSAGEM_INDIVIDUALMENTE, responsavel);
		int paramMenLidaIndividualmenteInt = 0;

		if (TextHelper.isNum(paramMenLidaIndividualmente)) {
			paramMenLidaIndividualmenteInt = Integer.valueOf(paramMenLidaIndividualmente.toString());
		}

		try {
			CustomTransferObject criterio = new CustomTransferObject();
			String usuDataCad = request.getParameter("usu_data_cad");
			if (TextHelper.isNull(usuDataCad)) {
				usuDataCad = session.getAttribute("usu_data_cad").toString();
			}
			criterio.setAttribute(Columns.USU_DATA_CAD, DateHelper.parse(usuDataCad, "yyyy-MM-dd"));

			if (session.getAttribute("mensagem_sem_leitura") == null) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
				return iniciar(request, response, session, model);
			}

			boolean confirmou = false;
			int contador = ((Integer) session.getAttribute("mensagem_sem_leitura")).intValue();
			try {
				// Only one thread can execute inside a Java code block synchronized on the same
				// monitor object: evita race condition na leitura de mensagem
				synchronized (session) {
					List<MensagemTO> mensagens = mensagemController.lstMensagemUsuarioSemLeitura(criterio, responsavel);
					Integer cont = 0;
					for (MensagemTO menTO : mensagens) {
						if (responsavel.isSer() || responsavel.isCseOrg()) {
							menDias = DateHelper.dayDiff(menTO.getMenData());
							menDias = menDias < 0 ? menDias * -1 : menDias;
							if(menDias < paramMenLidaIndividualmenteInt) {
								menTO.setMenLidaIndividualmente("S");
							} else if (menDias >= paramMenLidaIndividualmenteInt) {
								menTO.setMenLidaIndividualmente("N");
							}
						}
						String confirma = responsavel.isCseOrg() || responsavel.isSer() ? JspHelper.verificaVarQryStr(request, menTO.getMenLidaIndividualmente() + cont.toString()) : JspHelper.verificaVarQryStr(request, "confirma" + menTO.getMenCodigo());
						if (confirma.equals(CodedValues.TPC_SIM)) {
							// Se confirmou leitura da mensagem, entao insere na tabela
							// LeituraMensagemUsuario.
							contador--;
							CustomTransferObject lmu = new CustomTransferObject();
							lmu.setAttribute(Columns.LMU_MEN_CODIGO, menTO.getMenCodigo());
							lmu.setAttribute(Columns.LMU_USU_CODIGO, responsavel.getUsuCodigo());
							lmu.setAttribute(Columns.LMU_DATA, new Date());
							mensagemController.createLeituraMensagemUsuario(lmu, responsavel);
							confirmou = true;
						} else if (confirma.equals(CodedValues.TPC_NAO)) {
							// Se marcou para ler depois, libera tambem para acessar o sistema.
							// Garante que marcou ao menos uma opcao (JavaScript faz isso tambem).
							contador--;
						}
						if (responsavel.isCseOrg() || responsavel.isSer()) {
							cont++;
						}
					}
				}

				// Se confirmou a leitura de uma mensagem
				if (confirmou) {
					// Verifica se existe bloqueio de consignatária por mensagem não lida
					Integer diasBloqMsgNaoLida = 0;
					try {
						diasBloqMsgNaoLida = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_CSA_MENSAGEM_NAO_LIDA, responsavel).toString());
					} catch (NullPointerException | NumberFormatException e) {
					}

					// Verificar o desbloqueio da consignatária
					boolean desbloqueioAutomaticoMensagemLeituraPendente = responsavel.isCsaCor() && diasBloqMsgNaoLida > 0;
					if (desbloqueioAutomaticoMensagemLeituraPendente) {
						if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(responsavel.getCsaCodigo(), responsavel)) {
							session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
						}
					}
				}
				if (contador <= 0) {
					// Se marcou todas, libera para acessar sistema.
					session.removeAttribute("mensagem_sem_leitura");
					request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true", request)));
					return "jsp/redirecionador/redirecionar";
				}

				return iniciar(request, response, session, model);

			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
				session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
				return iniciar(request, response, session, model);
			}
		} catch (ParseException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

}
