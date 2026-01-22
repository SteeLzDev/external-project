package com.zetra.econsig.web.controller.folha;

import java.util.Date;
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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaDesfazUltimoRetorno;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: DesfazerUltimoRetornoWebController</p>
 * <p>Description: REST Controller para desfazer último retorno.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision:  $
 * $Date:  $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/desfazerRetorno" })
public class DesfazerUltimoRetornoWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DesfazerUltimoRetornoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ImpRetornoController impRetornoController;

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		SynchronizerToken.saveToken(request);

		boolean isEst = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO));

		if (!responsavel.isCseSupOrg()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

        String estCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "estCodigo")) ? JspHelper.verificaVarQryStr(request, "estCodigo") : (String) request.getAttribute("estCodigo");
        String orgCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "orgCodigo")) ? JspHelper.verificaVarQryStr(request, "orgCodigo") : (String) request.getAttribute("orgCodigo");

		if (isEst) {
			estCodigo = responsavel.getCodigoEntidadePai();
			orgCodigo = null;
		} else if (responsavel.isOrg()) {
		    estCodigo = null;
			orgCodigo = responsavel.getCodigoEntidade();
		}

        List<TransferObject> lstEstabelecimentos;
        List<TransferObject> lstOrgaos;
        try {
            TransferObject criterio = null;
            lstEstabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);
            lstOrgaos = consignanteController.lstOrgaos(criterio, responsavel);
        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

		Date ultPeriodoDate = null;
		List<TransferObject> historicoParcelas = null;
		boolean existePeriodoExportado = false;
		boolean temProcessamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, responsavel);

		// Verifica se existe algum processo rodando para o usuário
		String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
		if (!temProcessoRodando) {

			try {
				ultPeriodoDate = impRetornoController.getUltimoPeriodoRetorno(orgCodigo, estCodigo, responsavel);
				existePeriodoExportado = impRetornoController.existeOutroPeriodoExportado(orgCodigo, estCodigo, ultPeriodoDate, responsavel);

				if (temProcessamentoFerias) {
					historicoParcelas = impRetornoController.getHistoricoParcelasAgrupado(ultPeriodoDate, orgCodigo, estCodigo, responsavel);
				}
			} catch (Exception ex) {
				session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
		}

		model.addAttribute("estCodigo", estCodigo);
		model.addAttribute("orgCodigo", orgCodigo);
		model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);
		model.addAttribute("lstOrgaos", lstOrgaos);
		model.addAttribute("temProcessoRodando", temProcessoRodando);
		model.addAttribute("historicoParcelas", historicoParcelas);
		model.addAttribute("ultPeriodoDate", ultPeriodoDate);
		model.addAttribute("existePeriodoExportado", existePeriodoExportado);

		return viewRedirect("jsp/desfazerRetorno/desfazerUltimoRetorno", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=desfazer" })
	public String desfazer(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		//Valida o token se action é desfazer, isto trata a primeira chamada à página (via menu)
		if (!SynchronizerToken.isTokenValid(request)) {
		  session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
		  return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		SynchronizerToken.saveToken(request);

		boolean isEst = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO));

		if (!responsavel.isCseSupOrg()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

        String estCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "estCodigo")) ? JspHelper.verificaVarQryStr(request, "estCodigo") : (String) request.getAttribute("estCodigo");
        String orgCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "orgCodigo")) ? JspHelper.verificaVarQryStr(request, "orgCodigo") : (String) request.getAttribute("orgCodigo");

        if (isEst) {
            estCodigo = responsavel.getCodigoEntidadePai();
            orgCodigo = null;
        } else if (responsavel.isOrg()) {
            estCodigo = null;
            orgCodigo = responsavel.getCodigoEntidade();
        }

		// Verifica se existe algum processo rodando para o usuário
		String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";

		boolean recalcularMargem = JspHelper.verificaVarQryStr(request, "recalcularMargem").equals("true");
		boolean desfazerMovimento = JspHelper.verificaVarQryStr(request, "desfazerMovimento").equals("true");
		String[] parcelas = null;
		boolean temProcessamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, responsavel);

		if (temProcessamentoFerias) {
			parcelas = request.getParameterValues("parcelas");
			if (parcelas == null || parcelas.length == 0) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.desfaz.ultimo.retorno.selecione.periodo", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
		}

		ProcessaDesfazUltimoRetorno processo = new ProcessaDesfazUltimoRetorno(recalcularMargem, desfazerMovimento, orgCodigo, estCodigo, parcelas, responsavel);
		processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.desfaz.ultimo.retorno", responsavel));
		processo.start();
		ControladorProcessos.getInstance().incluir(chave, processo);
		session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.ultimo.retorno.sendo.desfeito", responsavel));

		return iniciar(request, response, session, model);
	}
}