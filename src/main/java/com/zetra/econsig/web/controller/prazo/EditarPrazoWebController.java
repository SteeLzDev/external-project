package com.zetra.econsig.web.controller.prazo;

import java.util.ArrayList;
import java.util.Collections;
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
 * <p>Title: EditarPrazoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutenção de Prazo.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 28054 $
 * $Date: 2019-10-25 15:17:08 -0300 (sex, 25 out 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarPrazo" })
public class EditarPrazoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarPrazoWebController.class);

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");

        if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(titulo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<PrazoTransferObject> prazos = null;
        try {
            prazos = simulacaoController.findPrazoByServico(svcCodigo, responsavel);
            Collections.sort(prazos);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            prazos = new ArrayList<>();
        }

        model.addAttribute("prazos", prazos);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("podeEditarPrazo", responsavel.temPermissao(CodedValues.FUN_EDT_PRAZO));

        return viewRedirect("jsp/manterServico/editarPrazo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String codigo = JspHelper.verificaVarQryStr(request, "PRZ_CODIGO");
        String status = JspHelper.verificaVarQryStr(request, "PRZ_ATIVO");

        if (!TextHelper.isNull(codigo) && !TextHelper.isNull(status)) {
            try {
                simulacaoController.updatePrazo(codigo, Short.valueOf(status), responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=inserir" })
    public String inserir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String novoPrazoFim = JspHelper.verificaVarQryStr(request, "PRZ_VLR_FIM");
        String novoPrazoIni = JspHelper.verificaVarQryStr(request, "PRZ_VLR_INI");
        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        boolean podeEditarPrazo = responsavel.temPermissao(CodedValues.FUN_EDT_PRAZO);

        if (podeEditarPrazo) {
            try {
                if (TextHelper.isNull(novoPrazoFim)) {
                    try {
                        simulacaoController.createPrazo(svcCodigo, Short.valueOf(novoPrazoIni), responsavel);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.prazo.prazo.existe", responsavel));
                    }
                } else {
                    short ini = Short.valueOf(novoPrazoIni).shortValue();
                    short fim = Short.valueOf(novoPrazoFim).shortValue();
                    for (short i = ini; i <= fim; i++) {
                        try {
                            simulacaoController.createPrazo(svcCodigo, Short.valueOf(i), responsavel);
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.prazo.prazo.existe", responsavel));
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            }
        }

        return iniciar(request, response, session, model);
    }

    private String bloquearDesbloquearTudo(Short status, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        try {
            simulacaoController.ativaDesativaSvcPrazo(svcCodigo, status, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquearTudo" })
    public String bloquearTudo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        return bloquearDesbloquearTudo(Short.valueOf("0"), request, response, session, model);
    }

    @RequestMapping(params = { "acao=desbloquearTudo" })
    public String desbloquearTudo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        return bloquearDesbloquearTudo(Short.valueOf("1"), request, response, session, model);
    }

    private String bloquearDesbloquearParcial(Short status, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String prazoIni = JspHelper.verificaVarQryStr(request, "PRZ_VLR_INI");
            String prazoFim = JspHelper.verificaVarQryStr(request, "PRZ_VLR_FIM");
            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            short ini = Short.valueOf(prazoIni);
            short fim = Short.valueOf(prazoFim);

            List<PrazoTransferObject> prazosSvc = simulacaoController.findPrazoByServico(svcCodigo, responsavel);
            for (PrazoTransferObject prazo : prazosSvc) {
                String przCodigo = prazo.getPrzCodigo();
                short przVlr = prazo.getPrzVlr();
                if (przVlr >= ini && przVlr <= fim) {
                    simulacaoController.updatePrazo(przCodigo, status, responsavel);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
        }

        PrazoSvcCsa.reset();
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        return bloquearDesbloquearParcial(Short.valueOf("0"), request, response, session, model);
    }

    @RequestMapping(params = { "acao=desbloquear" })
    public String desbloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        return bloquearDesbloquearParcial(Short.valueOf("1"), request, response, session, model);
    }
}
