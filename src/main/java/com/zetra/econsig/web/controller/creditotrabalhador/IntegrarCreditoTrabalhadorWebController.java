package com.zetra.econsig.web.controller.creditotrabalhador;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.arquivo.upload.UploadArquivoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method= {RequestMethod.POST}, value = {"/v3/integrarCreditoTrabalhador"})
public class IntegrarCreditoTrabalhadorWebController extends UploadArquivoWebController{

    @Autowired
	private UsuarioController usuarioController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
	    SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isCseSupOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {

        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            model.addAttribute("arqManual", ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_MANUAL_INTEGRACAO_ORIENTADA, responsavel));

            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);

        return viewRedirect("jsp/integrarCreditoTrabalhador/dashboardCreditoTrabalhador", request, session, model, responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=upload" })
	public String upload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam("tipo") String tipo) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean selecionaEstOrgUploadMargemRetorno = false;
		boolean selecionaEstOrgUploadContracheque = false;

        try {
			upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
		} catch (ZetraException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		return iniciar(request, response, session, model);
	}

    @Override
    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        String path = "creditoTrabalhador" + java.io.File.separatorChar;
        List<String> listPath = new ArrayList<>();

        if (responsavel.isOrg() && (((!selecionaEstOrgUploadMargemRetorno || !selecionaEstOrgUploadContracheque) && temPermissaoEst) || ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)))) {
            listPath.add(path + "est" + java.io.File.separatorChar + responsavel.getCodigoEntidadePai());
        } else if (responsavel.isOrg()) {
            listPath.add(path + "cse" + java.io.File.separatorChar + responsavel.getCodigoEntidade());
        } else if (responsavel.isCseSup()) {
            if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && !TextHelper.isNull(papCodigo)) {
                if (papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {
                    if (TextHelper.isNull(orgCodigo)) {
                        throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    listPath.add(path + "cse" + java.io.File.separatorChar + orgCodigo);
                } else if (papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {
                    if (TextHelper.isNull(estCodigo)) {
                        throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                    }
                    listPath.add(path + "est" + java.io.File.separatorChar + estCodigo);
                } else if (papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) {
                    listPath.add(path + "cse");
                }
            } else {
                listPath.add(path + "cse");
            }
        }

        return listPath;
    }
}