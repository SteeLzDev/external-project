package com.zetra.econsig.web.controller.faq;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.AvaliacaoFaqTO;
import com.zetra.econsig.exception.AvaliacaoFaqControllerException;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.faq.AvaliacaoFaqController;
import com.zetra.econsig.service.faq.FaqController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VisualizarSobreWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Faq.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco$
 * $Date: 2020-11-20 11:21:32 -0200 (Sex, 20 nov 2020)$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarFaq" })
public class VisualizarFaqWebController extends AbstractWebController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarFaqWebController.class);

	@Autowired
	private FaqController faqController;

	@Autowired
	private AvaliacaoFaqController avfController;

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

		SynchronizerToken.saveToken(request);

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		AcessoSistema previewEntidade = new AcessoSistema(null, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
		model.addAttribute("tituloPagina",
				ApplicationResourcesHelper.getMessage("rotulo.faq.subtitulo.pt", responsavel));

		boolean mostraComboPreview = responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDITAR_FAQ);
		int filtro = responsavel.isSup() ? 6 : 1;
		if (mostraComboPreview) {
			try {
				filtro = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
			} catch (Exception ex) {
			}
			switch (filtro) {
			case 1:
				previewEntidade.setTipoEntidade("CSE");
				break;
			case 2:
				previewEntidade.setTipoEntidade("CSA");
				break;
			case 3:
				previewEntidade.setTipoEntidade("COR");
				break;
			case 4:
				previewEntidade.setTipoEntidade("ORG");
				break;
			case 5:
				previewEntidade.setTipoEntidade("SER");
				break;
			case 6:
				previewEntidade.setTipoEntidade("SUP");
				break;
			default:
				previewEntidade.setTipoEntidade("CSE");
				break;
			}
		}

		List<TransferObject> faqs = null;
		String filtroAvaliacaoPesquisa = null;
		filtroAvaliacaoPesquisa = (String) model.asMap().get("filtroAvaliacaoPesquisa");

		try {
			if (mostraComboPreview && TextHelper.isNull(filtroAvaliacaoPesquisa)) {
				faqs = faqController.pesquisaFaq(responsavel.getUsuCodigo(),previewEntidade, 0);
			} else if (TextHelper.isNull(filtroAvaliacaoPesquisa)) {
				faqs = faqController.pesquisaFaq(responsavel.getUsuCodigo(), responsavel, 0);
			} else {
				faqs = (List<TransferObject>) model.asMap().get("faqs");
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}

		for (TransferObject faq : faqs) {
			String faqTexto = (String) faq.getAttribute(Columns.FAQ_TEXTO);

			if (faqTexto.contains("increment_video[")) {
				faqTexto = faqTexto.replaceAll("increment_video\\[", "<iframe src=\"https://player.vimeo.com/video/");
				faqTexto = faqTexto.replaceAll("\\]final",
						"\" style=\"width: 100%; height: 40em; border:0;\" title=\"vimeo video\"></iframe>");
				faqTexto = faqTexto.replaceAll("increment_url_", " https://vimeo.com/");

				faq.setAttribute(Columns.FAQ_TEXTO, faqTexto);
			}
		}

		String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT,
				responsavel);
		if (TextHelper.isNull(emailSuporte)) {
			emailSuporte = "suporte@zetrasoft.com.br";
		}

		model.addAttribute("mostraComboPreview", mostraComboPreview);
		model.addAttribute("filtro", filtro);
		model.addAttribute("emailSuporte", emailSuporte);
		model.addAttribute("faqs", faqs);

		return viewRedirect("jsp/visualizarFaq/visualizarFaq", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=pesquisarAvaliacaoFaq" })
	public String pesquisarAvaliacaoFaq(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) throws AvaliacaoFaqControllerException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		 // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

		List<TransferObject> faqs = null;
		String filtroAvaliacaoPesquisa = null;

		try {
			filtroAvaliacaoPesquisa = JspHelper.verificaVarQryStr(request, "FILTRO");
			faqs = avfController.pesquisaAvaliacaoFaq(filtroAvaliacaoPesquisa, responsavel, 0);
		} catch (FaqControllerException ex) {
			LOG.error(ex.getMessage());
		}

		model.addAttribute("faqs", faqs);
		model.addAttribute("filtroAvaliacaoPesquisa", filtroAvaliacaoPesquisa);

		return iniciar(request, response, session, model);
	}

	@RequestMapping(params = { "acao=salvarAvaliacaoFaq" })
	public String salvarAvaliacaoFaq(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		 // Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }


		try {
			String faqCodigo = JspHelper.verificaVarQryStr(request, "faqCodigo");
			String avfCodigo = !TextHelper.isNull(request.getParameter("avfCodigo")) ? JspHelper.verificaVarQryStr(request, "avfCodigo") : "";
			String avfNota = JspHelper.verificaVarQryStr(request, "avaliacaoFaq_" + faqCodigo);
			String avfComentario = JspHelper.verificaVarQryStr(request, "avaliacaoFaqComentario_" + faqCodigo);

			AvaliacaoFaqTO avaliacaoFaqTO = new AvaliacaoFaqTO();

			avaliacaoFaqTO.setUsuCodigo(responsavel.getUsuCodigo());
			avaliacaoFaqTO.setAvfFaqCodigo(faqCodigo);
			avaliacaoFaqTO.setAvfCodigo(avfCodigo);
			avaliacaoFaqTO.setAvfNota(TextHelper.forHtmlContent(avfNota));
			avaliacaoFaqTO.setAvfData(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
			avaliacaoFaqTO.setAvfComentario(TextHelper.forHtmlContent(avfComentario));

			AvaliacaoFaqTO avaliacaoFaq = avfController.findAvaliacaoFaq(avaliacaoFaqTO, responsavel);

			//Cria o primeiro registro.
			if (avaliacaoFaq == null) {
				avfController.createAvaliacaoFaq(avaliacaoFaqTO, responsavel);
			}

			//Cria registro entre transição de avaliações, ou seja, "S" para "N" e vice versa.
			if (!TextHelper.isNull(avfCodigo) && (!avfNota.equals(avaliacaoFaq.getAvfNota()))) {
				//Seta o comentário como vazio por que a avaliação como
				//"SIM" não tem opção de comentário.
				if (avfNota.equals("1")) {
					avaliacaoFaqTO.setAvfComentario("");
				}
				avfController.createAvaliacaoFaq(avaliacaoFaqTO, responsavel);
			}

			//Realiza o update para o cenário de avaliação não util, caso o usuário
			//queria incluir ou alterar o comentário.
			if (!TextHelper.isNull(avfCodigo) && avfNota.equals(avaliacaoFaq.getAvfNota()) ) {
				avfController.updateAvaliacaoFaq(avaliacaoFaqTO, responsavel);
			}

			String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
			if (TextHelper.isNull(emailSuporte)) {
				emailSuporte = "suporte@zetrasoft.com.br";
			}

			if (avfNota.equals("0")) {
				EnviaEmailHelper.enviarEmailAvaliacaoFaqNaoUtil(responsavel.getName(), emailSuporte, avfComentario, responsavel.getNomeEntidade(), responsavel.getNomeEntidadePai(), responsavel);
			}

			model.addAttribute("emailSuporte", emailSuporte);

			return iniciar(request, response, session, model);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

	}

}
