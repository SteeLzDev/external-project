package com.zetra.econsig.web.controller.vinculo;

import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/manterVinculoCsaRse"})
public class EditarVinculoCsaRseWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarVinculoCsaRseWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = "acao=iniciar")
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigo = request.getParameter("csaCodigo");

        List<VinculoConsignataria> listVinculoCsa = new ArrayList<>();
        try {
            listVinculoCsa = consignatariaController.findVinculosCsa(csaCodigo, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("listVinculoCsa", listVinculoCsa);
        model.addAttribute("titulo", responsavel.getNomeEntidade());

        return viewRedirect("jsp/vinculoCsaRse/listarVinculoCsaRse", request, session, model, responsavel);
    }

    @RequestMapping(params = "acao=novo")
    public String novo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigo = request.getParameter("csaCodigo");

        List<VinculoRegistroServidor> listVinculoRse = new ArrayList<>();
        try {
            listVinculoRse = consignatariaController.findVinculosRseParaCsa(csaCodigo, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("listVinculoRse", listVinculoRse);
        model.addAttribute("titulo", responsavel.getNomeEntidade());

        return viewRedirect("jsp/vinculoCsaRse/incluirVinculoCsaRse", request, session, model, responsavel);
    }

    @RequestMapping(params = "acao=editar")
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigo = request.getParameter("csaCodigo");
        String vcsCodigo = request.getParameter("vcsCodigo");

        List<VinculoRegistroServidor> listVinculoRse = new ArrayList<>();
        VinculoConsignataria vinculoConsignataria = null;
        List<VinculoRegistroServidor> vinculoRegistroServidor = null;
        try {
            listVinculoRse = consignatariaController.findVinculosRseParaCsa(csaCodigo, responsavel);
            vinculoConsignataria = consignatariaController.findVinculoCsa(vcsCodigo, responsavel);
            vinculoRegistroServidor = consignatariaController.findVinculoRseCsa(vcsCodigo, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("vinculoRse", !vinculoRegistroServidor.isEmpty() ? vinculoRegistroServidor.get(0) : null);
        model.addAttribute("vinculoConsignataria", vinculoConsignataria);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("listVinculoRse", listVinculoRse);
        model.addAttribute("titulo", responsavel.getNomeEntidade());

        return viewRedirect("jsp/vinculoCsaRse/incluirVinculoCsaRse", request, session, model, responsavel);
    }

    @RequestMapping(params = "acao=salvar")
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        ParamSession paramSession = ParamSession.getParamSession(session);

        String vcsDescricao = JspHelper.verificaVarQryStr(request, "vcsDescricao");
        String vcsIdentificador = JspHelper.verificaVarQryStr(request, "vcsIdentificador");
        String vrsCodigoAssociacao = JspHelper.verificaVarQryStr(request, "vrsCodigo");
        String csaCodigo = request.getParameter("csaCodigo");
        String vcsCodigo = request.getParameter("vcsCodigo") != null ? request.getParameter("vcsCodigo") : null;

        VinculoConsignataria vinculoConsignataria = new VinculoConsignataria();
        vinculoConsignataria.setVcsCodigo(vcsCodigo);
        vinculoConsignataria.setCsaCodigo(csaCodigo);
        vinculoConsignataria.setVcsAtivo(CodedValues.VCS_ATIVO);
        vinculoConsignataria.setVcsDescricao(vcsDescricao);
        vinculoConsignataria.setVcsIdentificador(vcsIdentificador);
        vinculoConsignataria.setVcsDataCriacao(DateHelper.getSystemDatetime());

        int salveOk = 0;
        try {
            salveOk = consignatariaController.salvarEditarVinculoRseCsa(vinculoConsignataria, vrsCodigoAssociacao, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (salveOk == 1) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.descricao.dupliacada", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } else if (salveOk == 2) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.identificador.duplicado", responsavel));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.salvo.sucesso", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = "acao=bloquearDesbloquear")
    public String bloquearDesbloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String vcsCodigo = request.getParameter("vcsCodigo");
        Short tipo = Short.valueOf(request.getParameter("tipo"));

        try {
            final String vrsCodigo = "";
            VinculoConsignataria vinculoConsignataria = consignatariaController.findVinculoCsa(vcsCodigo, responsavel);
            if (tipo.equals(CodedValues.VCS_ATIVO)) {
                vinculoConsignataria.setVcsAtivo(CodedValues.VCS_INATIVO);
            } else {
                vinculoConsignataria.setVcsAtivo(CodedValues.VCS_ATIVO);
            }
            consignatariaController.salvarEditarVinculoRseCsa(vinculoConsignataria, vrsCodigo, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (tipo.equals(CodedValues.VCS_ATIVO)) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio", responsavel));
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desbloqueio", responsavel));
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = "acao=excluir")
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String vcsCodigo = request.getParameter("vcsCodigo");

        try {
            consignatariaController.excluirVinculoCsa(vcsCodigo, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.exclusao", responsavel));
        return iniciar(request, response, session, model);
    }
}
