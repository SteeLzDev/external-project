package com.zetra.econsig.web.controller.convenio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ManterConvenioCorrespondenteWebController</p>
 * <p>Description: Listar e editar convênio correspondente</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25753 $
 * $Date: 2019-04-10 17:27:43 -0200 (qua, 10 abr 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterConvenioCorrespondente" })
public class ManterConvenioCorrespondenteWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterConvenioCorrespondenteWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    private CorrespondenteTransferObject buscarCorrespondente(HttpServletRequest request) throws ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Recuperando o csaCodigo
        String corCodigo = request.getParameter("cor");
        String csaCodigo = (responsavel.isCsa() ? responsavel.getCsaCodigo() : request.getParameter("csa"));
        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(corCodigo)) {
            throw new ConsignatariaControllerException("mensagem.usoIncorretoSistema", responsavel);
        }

        // Valida se o correspondente pode ser editado pelo usuário de CSA
        CorrespondenteTransferObject correspondente = consignatariaController.findCorrespondente(corCodigo, responsavel);
        if (correspondente == null || !correspondente.getCsaCodigo().equals(csaCodigo)) {
            throw new ConsignatariaControllerException("mensagem.usoIncorretoSistema", responsavel);
        }

        return correspondente;
    }

    //Listar serviços para manutenção de convênio do correspondente
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        CorrespondenteTransferObject correspondente = null;
        try {
            correspondente = buscarCorrespondente(request);
        } catch (ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean podeEditarCnvCor = responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE);
        boolean podeConsultarCnvCor = responsavel.temPermissao(CodedValues.FUN_CONS_CONV_CORRESPONDENTE);
        boolean podeEditarCor = responsavel.temPermissao(CodedValues.FUN_EDT_CORRESPONDENTES);

        String bloqueiaTodos = JspHelper.verificaVarQryStr(request, "blockAll");

        if (podeEditarCnvCor) {
            if (bloqueiaTodos != null && bloqueiaTodos.equals("true")) {
                convenioController.bloqueiaCnvCor(correspondente.getCorCodigo(), responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servico.correspondente.bloqueado.sucesso", responsavel));
            } else if (bloqueiaTodos != null && bloqueiaTodos.equals("false")) {
                convenioController.desBloqueiaCnvCor(correspondente.getCorCodigo(), responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servico.correspondente.desbloqueado.sucesso", responsavel));
            }
        }

        // Recuperando serviços
        List<TransferObject> servicos = null;
        try {
            servicos = convenioController.listCnvCorrespondente(correspondente.getCorCodigo(), responsavel);
        } catch (ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            servicos = new ArrayList<>();
        }

        model.addAttribute("servicos", servicos);
        model.addAttribute("csaCodigo", correspondente.getCsaCodigo());
        model.addAttribute("corCodigo", correspondente.getCorCodigo());
        model.addAttribute("corNome", correspondente.getCorNome());
        model.addAttribute("podeEditarCnvCor", podeEditarCnvCor);
        model.addAttribute("podeEditarCor", podeEditarCor);
        model.addAttribute("podeConsultarCnvCor", podeConsultarCnvCor);

        return viewRedirect("jsp/manterCorrespondente/listarServico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultarConvenios(@RequestParam(value = "svc_codigo", required = true) String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        CorrespondenteTransferObject correspondente = null;
        try {
            correspondente = buscarCorrespondente(request);
        } catch (ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean podeEditarCnvCor = responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE);

        ServicoTransferObject servico = convenioController.findServico(svcCodigo, responsavel);

        // Recuperando convênios referente ao serviço selecionado
        List<TransferObject> convenios = null;
        try {
            convenios = convenioController.listOrgCnvCorrespondente(correspondente.getCorCodigo(), svcCodigo, responsavel);
        } catch (ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            convenios = new ArrayList<>();
        }

        model.addAttribute("convenios", convenios);
        model.addAttribute("csaCodigo", correspondente.getCsaCodigo());
        model.addAttribute("corCodigo", correspondente.getCorCodigo());
        model.addAttribute("corNome", correspondente.getCorNome());
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", servico.getSvcDescricao());
        model.addAttribute("svcIdentificador", servico.getSvcIdentificador());
        model.addAttribute("podeEditarCnvCor", podeEditarCnvCor);
        model.addAttribute("_paginacaoSubTitulo", ApplicationResourcesHelper.getMessage("rotulo.paginacao.registros.sem.estilo", responsavel, String.valueOf(1), String.valueOf(convenios.size()), String.valueOf(convenios.size())));

        return viewRedirect("jsp/manterCorrespondente/listarConvenios", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarConvenios(@RequestParam(value = "svc_codigo", required = true) String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        return consultarConvenios(svcCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarConvenioCorrespondente(@RequestParam(value = "svc_codigo", required = true) String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE)) {
            CorrespondenteTransferObject correspondente = null;
            try {
                correspondente = buscarCorrespondente(request);
            } catch (ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            try {
                // Cria os convênios especificados
                List<String> cnvCodigos = request.getParameterValues("CNV_CODIGO") != null ? Arrays.asList(request.getParameterValues("CNV_CODIGO")) : null;
                convenioController.criaConvenioCorrespondente(correspondente.getCorCodigo(), svcCodigo, cnvCodigos, responsavel);
                // Sucesso!
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.convenio.alteracoes.sucesso", responsavel));
            } catch (ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        return consultarConvenios(svcCodigo, request, response, session, model);
    }

    @RequestMapping(params = {"acao=alterarTodos"})
    public String alterarTodos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE)) {
            CorrespondenteTransferObject correspondente = null;
            try {
                correspondente = buscarCorrespondente(request);
            } catch (ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean bloqueiaTodos = "true".equals(JspHelper.verificaVarQryStr(request, "blockAll"));
            if (bloqueiaTodos) {
                convenioController.bloqueiaCnvCor(correspondente.getCorCodigo(), responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servico.correspondente.bloqueado.sucesso", responsavel));
            } else {
                convenioController.desBloqueiaCnvCor(correspondente.getCorCodigo(), responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servico.correspondente.desbloqueado.sucesso", responsavel));
            }
        }

        return iniciar(request, response, session, model);
    }
}
