package com.zetra.econsig.web.controller.leilao;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: InformarContatoLeilaoFinalizadoWebController</p>
 * <p>Description: Controlador Web base para a tela de informação de contato para leilão finalizado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/informarContatoLeilaoFinalizado" })
public class InformarContatoLeilaoFinalizadoWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformarContatoLeilaoFinalizadoWebController.class);

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
    	try {
    	    String dias = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO, responsavel).toString();
            model.addAttribute("dias", dias);

            model.addAttribute("leiloes", leilaoSolicitacaoController.lstLeilaoFinalizadoSemContato(responsavel));
            // irá registrar para estes leilões que o servidor irá ver na tela
            session.setAttribute("leiloes", leilaoSolicitacaoController.lstLeilaoFinalizadoSemContato(responsavel));
    		return viewRedirect("jsp/leilao/informarContatoLeilaoFinalizado", request, session, model, responsavel);

    	} catch (Exception ex) {
    		LOG.error(ex.getMessage(), ex);
    		session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
    		return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    	}
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model,  AcessoSistema responsavel) throws ViewHelperException {
    	model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.titulo.informar.contato.leilao.finalizado", responsavel));
    }

	@RequestMapping(method = { RequestMethod.POST }, params = {"acao=salvar"})
	public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isSer() || !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
		try {
            @SuppressWarnings("unchecked")
            List<TransferObject> leiloes = (List<TransferObject>)session.getAttribute("leiloes");
            String recusa = JspHelper.verificaVarQryStr(request, "recusa");
            if (recusa == null || !recusa.equalsIgnoreCase("sim")) {
                String email = JspHelper.verificaVarQryStr(request, "email");

                if (TextHelper.isNull(email) || !TextHelper.isEmailValid(email)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String ddd = JspHelper.verificaVarQryStr(request, "ddd");
                String telefone = JspHelper.verificaVarQryStr(request, "telefone");

                if (!TextHelper.isNull(ddd) || !TextHelper.isNull(telefone)) {
                    if (TextHelper.isNull(telefone)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
                leilaoSolicitacaoController.salvarInformacaoContatoLeilaoFinalizado(leiloes, email, ddd, telefone, responsavel);
            } else {
                leilaoSolicitacaoController.recusarInformacaoContatoLeilaoFinalizado(leiloes, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.atualizacao.informacao.contato.leilao.finalizado.sucesso", responsavel));
			session.removeAttribute("LeilaoFinalizadoSemContato");
			session.removeAttribute("leiloes");

			return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

	}
}
