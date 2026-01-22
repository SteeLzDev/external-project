package com.zetra.econsig.web.controller.prazo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.helper.prazo.PrazoSvcCsa;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarPrazoConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutenção de Prazo por Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 28054 $
 * $Date: 2019-10-25 15:17:08 -0300 (sex, 25 out 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarPrazoConsignataria" })
public class EditarPrazoConsignatariaWebController extends AbstractWebController {

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");

        if (svcCodigo.equals("") || svcDescricao.equals("") || csaCodigo.equals("") || titulo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<PrazoTransferObject> prazos = null;
        List<PrazoTransferObject> prazosCsa = null;
        try {
            prazos = simulacaoController.findPrazoAtivoByServico(svcCodigo, responsavel);
            prazosCsa = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);

            Collections.sort(prazos);
            Collections.sort(prazosCsa);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            prazos = new ArrayList<>();
            prazosCsa = new ArrayList<>();
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("titulo", titulo);
        model.addAttribute("prazos", prazos);
        model.addAttribute("prazosCsa", prazosCsa);
        model.addAttribute("podeEditarPrazo", responsavel.temPermissao(CodedValues.FUN_EDT_PRAZO));

        return viewRedirect("jsp/manterServico/editarPrazoConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String codigo = JspHelper.verificaVarQryStr(request, "PRZ_CODIGO");
        String status = JspHelper.verificaVarQryStr(request, "PRZ_ATIVO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        if (!TextHelper.isNull(codigo) && !TextHelper.isNull(status)) {
            try {
                if (status.equals(CodedValues.STS_ATIVO.toString())) {
                    simulacaoController.desbloqueiaPrazoCsa(csaCodigo, codigo, responsavel);
                } else {
                    simulacaoController.bloqueiaPrazoCsa(csaCodigo, codigo, responsavel);
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquearTudo" })
    public String bloquearTudo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        try {
            simulacaoController.desativaSvcPrazoPorCsa(svcCodigo, csaCodigo, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=desbloquearTudo" })
    public String desbloquearTudo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        try {
            simulacaoController.ativaSvcPrazoPorCsa(svcCodigo, csaCodigo, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String prazoIni = JspHelper.verificaVarQryStr(request, "PRZ_VLR_INI");
        String prazoFim = JspHelper.verificaVarQryStr(request, "PRZ_VLR_FIM");
        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        short ini = Short.valueOf(prazoIni).shortValue();
        short fim = Short.valueOf(prazoFim).shortValue();
        for (short i = ini; i <= fim; i++) {
            try {
                List<PrazoTransferObject> prazosCsaSvc = null;
                List<PrazoTransferObject> prazosCsa = null;
                prazosCsaSvc = simulacaoController.findPrazoAtivoByServico(svcCodigo, responsavel);
                prazosCsa = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
                Iterator<PrazoTransferObject> it = prazosCsaSvc.iterator();
                PrazoTransferObject prazo = null;
                String prz_codigo;
                Short prz_vlr;
                while (it.hasNext()) {
                    prazo = it.next();
                    prz_codigo = prazo.getPrzCodigo();
                    prz_vlr = prazo.getPrzVlr();

                    if (prz_vlr.equals(Short.valueOf(i)) && prazosCsa.contains(prazo)) {
                        simulacaoController.bloqueiaPrazoCsa(csaCodigo, prz_codigo, responsavel);
                    }
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=desbloquear" })
    public String desbloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String prazoIni = JspHelper.verificaVarQryStr(request, "PRZ_VLR_INI");
        String prazoFim = JspHelper.verificaVarQryStr(request, "PRZ_VLR_FIM");
        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        short ini = Short.valueOf(prazoIni).shortValue();
        short fim = Short.valueOf(prazoFim).shortValue();
        for (short i = ini; i <= fim; i++) {
            try {
                List<PrazoTransferObject> prazosCsaSvc = null;
                List<PrazoTransferObject> prazosCsa = null;
                prazosCsaSvc = simulacaoController.findPrazoAtivoByServico(svcCodigo, responsavel);
                prazosCsa = simulacaoController.findPrazoCsaByServico(svcCodigo, csaCodigo, responsavel);
                Iterator<PrazoTransferObject> it = prazosCsaSvc.iterator();
                PrazoTransferObject prazo = null;
                String przCodigo;
                Short przVlr;
                while (it.hasNext()) {
                    prazo = it.next();
                    przCodigo = prazo.getPrzCodigo();
                    przVlr = prazo.getPrzVlr();

                    if (przVlr.equals(Short.valueOf(i)) && !prazosCsa.contains(prazo)) {
                        simulacaoController.desbloqueiaPrazoCsa(csaCodigo, przCodigo, responsavel);
                    }
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }
}
