package com.zetra.econsig.web.controller.termoadesao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TermoAdesaoTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.termoadesao.TermoAdesaoAcao;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.TermoAdesaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: InformarTermoAdesaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso informar de termo de adesão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Date: 2018-11-09 15:53:04 -0200 (sex, 09 nov 2018) $
 */
@Controller
@RequestMapping(value = { "/v3/informarTermoAdesao" })
public class InformarTermoAdesaoWebController extends AbstractWebController {
    private static final Log LOG = LogFactory.getLog(InformarTermoAdesaoWebController.class);

    @Autowired
    private TermoAdesaoController termoAdesaoController;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = {"acao=iniciar"})
    public String iniciar(@RequestParam(value = "funCodigo", required = false, defaultValue = "") String funCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
    	final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
    		final List<String> termoAdesaoLerDepois = (List<String>) session.getAttribute("termoAdesaoLerDepois");
			final List<TermoAdesaoTO> termosAdesao = termoAdesaoController.listTermoAdesaoSemLeitura(funCodigo, termoAdesaoLerDepois, responsavel);

			model.addAttribute("funCodigo", funCodigo);
			model.addAttribute("termosAdesao", termosAdesao);
			model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.adesao.plural", responsavel));

	        return viewRedirect("jsp/manterTermoAdesao/confirmarTermoAdesao", request, session, model, responsavel);

		} catch (final TermoAdesaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
    }

    @RequestMapping(method = { RequestMethod.POST }, params = {"acao=salvar"})
    public String salvarTermoAdesao(@RequestParam(value = "funCodigo", required = false, defaultValue = "") String funCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		List<String> termoAdesaoLerDepois = (List<String>) session.getAttribute("termoAdesaoLerDepois");

		if (termoAdesaoLerDepois == null) {
			termoAdesaoLerDepois = new ArrayList<>();
		}

		try {
			final List<TermoAdesaoTO> termosAdesao = termoAdesaoController.listTermoAdesaoSemLeitura(funCodigo, termoAdesaoLerDepois, responsavel);
			int contador = termosAdesao.size();

			TermoAdesaoAcao termoAdesaoAcao = null;

			// Only one thread can execute inside a Java code block synchronized on the same
			// monitor object: evita race condition na leitura de mensagem
			synchronized (session) {
				final String userBrowser = com.zetra.econsig.helper.web.JspHelper.getUserBrowser(request);
				for (final TermoAdesaoTO termoAdesao : termosAdesao) {

					final String tadCodigo = termoAdesao.getTadCodigo();
					final String termoClassName = termoAdesao.getTadClasseAcao();

		            if (!TextHelper.isNull(termoClassName)) {
		            	termoAdesaoAcao = (TermoAdesaoAcao) Class.forName(termoClassName).getDeclaredConstructor().newInstance();
		            }

					final String confirmar = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "confirmar-leitura" + termoAdesao.getTadCodigo());
					switch (confirmar) {
					case CodedValues.TERMO_ADESAO_CONFIRMADO:
						contador--;
						termoAdesaoController.createLeituraTermoAdesaoUsuario(termoAdesao.getTadCodigo(), true, ApplicationResourcesHelper.getMessage("mensagem.leitura.termo.adesao.aceito", responsavel, userBrowser), responsavel);
						break;
					case CodedValues.TERMO_ADESAO_RECUSADO:
						contador--;
						termoAdesaoController.createLeituraTermoAdesaoUsuario(termoAdesao.getTadCodigo(), false, ApplicationResourcesHelper.getMessage("mensagem.leitura.termo.adesao.recusado", responsavel, userBrowser), responsavel);
						break;
					case CodedValues.TERMO_ADESAO_LER_DEPOIS:
						// Se marcou para ler depois, libera tambem para acessar o sistema.
						// Garante que marcou ao menos uma opcao (JavaScript faz isso tambem).
						contador--;
						if (termoAdesaoAcao != null) {
			                termoAdesaoAcao.preLerDepoisTermo(tadCodigo, responsavel);
			            }

						termoAdesaoLerDepois.add(termoAdesao.getTadCodigo());

						if (termoAdesaoAcao != null) {
			                termoAdesaoAcao.posLerDepoisTermo(tadCodigo, responsavel);
			            }
						break;
					case null:
					default:
						break;
					}
				}
			}

			if (!termoAdesaoLerDepois.isEmpty()) {
				session.setAttribute("termoAdesaoLerDepois", termoAdesaoLerDepois);
			}

			if (contador <= 0) {
				// Se marcou todas, libera para acessar sistema.
				request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true", request)));
				return "jsp/redirecionador/redirecionar";
			}

			return iniciar(funCodigo, request, response, session, model);

		} catch (final Exception ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return iniciar(funCodigo, request, response, session, model);
		}
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = {"acao=listar"})
	public String listarTermoAdesao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
			final List<TransferObject> termosAdesao = termoAdesaoController.listTermoAdesaoByUsuCodigo(responsavel);
			model.addAttribute("termosAdesao", termosAdesao);
			model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.adesao.plural", responsavel));

			return viewRedirect("jsp/manterTermoAdesao/listaTermoAdesao", request, session, model, responsavel);

		} catch (final TermoAdesaoControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = {"acao=visualizar"})
	public String visualizarTermoAdesao(@RequestParam(value = "tadCodigo", required = false, defaultValue = "") String tadCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
			final List<TransferObject> termoAdesao = new ArrayList<>();
			termoAdesao.add(termoAdesaoController.findTermoAdesaoComLeituraByTadCodigo(tadCodigo, responsavel).getFirst());

			model.addAttribute("tadCodigo", termoAdesao.getFirst().getAttribute(Columns.TAD_CODIGO));
			model.addAttribute("verTermoAdesao", true);
			model.addAttribute("termosAdesao", termoAdesao);
			model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.adesao.plural", responsavel));

			return viewRedirect("jsp/manterTermoAdesao/confirmarTermoAdesao", request, session, model, responsavel);

		} catch (final TermoAdesaoControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	@RequestMapping(method = { RequestMethod.POST }, params = {"acao=editar"})
	public String editarTermoAdesao(@RequestParam(value = "tadCodigo", required = false, defaultValue = "") String tadCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		List<String> termoAdesaoLerDepois = (List<String>) session.getAttribute("termoAdesaoLerDepois");

		if (termoAdesaoLerDepois == null) {
			termoAdesaoLerDepois = new ArrayList<>();
		}

		try {
			TermoAdesaoAcao termoAdesaoAcao = null;
			final String userBrowser = com.zetra.econsig.helper.web.JspHelper.getUserBrowser(request);
			final String confirmar = com.zetra.econsig.helper.web.JspHelper.verificaVarQryStr(request, "confirmar-leitura" + tadCodigo);
			TermoAdesaoTO termoAdesaoTO = new TermoAdesaoTO();
			termoAdesaoTO.setTadCodigo(tadCodigo);

			termoAdesaoTO = termoAdesaoController.findTermoAdesao(termoAdesaoTO, responsavel);

			final String termoClassName = termoAdesaoTO.getTadClasseAcao();

            if (!TextHelper.isNull(termoClassName)) {
            	termoAdesaoAcao = (TermoAdesaoAcao) Class.forName(termoClassName).getDeclaredConstructor().newInstance();
            }

			switch (confirmar) {
			case CodedValues.TERMO_ADESAO_CONFIRMADO:
				termoAdesaoController.createLeituraTermoAdesaoUsuario(tadCodigo, true, ApplicationResourcesHelper.getMessage("mensagem.leitura.termo.adesao.aceito", responsavel, userBrowser), responsavel);
				break;
			case CodedValues.TERMO_ADESAO_RECUSADO:
				termoAdesaoController.createLeituraTermoAdesaoUsuario(tadCodigo, false, ApplicationResourcesHelper.getMessage("mensagem.leitura.termo.adesao.recusado", responsavel, userBrowser), responsavel);
				break;
			case CodedValues.TERMO_ADESAO_LER_DEPOIS:

				if (termoAdesaoAcao != null) {
	                termoAdesaoAcao.preLerDepoisTermo(tadCodigo, responsavel);
	            }

				termoAdesaoLerDepois.add(tadCodigo);

				if (termoAdesaoAcao != null) {
	                termoAdesaoAcao.posLerDepoisTermo(tadCodigo, responsavel);
	            }
				break;
			case null:
			default:
				break;
			}

			if (!termoAdesaoLerDepois.isEmpty()) {
				session.setAttribute("termoAdesaoLerDepois", termoAdesaoLerDepois);
			}

			session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.opcao.termo.adesao.alterado.sucesso", responsavel));
			return visualizarTermoAdesao(tadCodigo, request, response, session, model);

		} catch (final Exception ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return visualizarTermoAdesao(tadCodigo, request, response, session, model);
		}
	}
}
