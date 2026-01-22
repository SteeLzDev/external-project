package com.zetra.econsig.web.controller.boleto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.boleto.BoletoServidorController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ConsultarBoletoWebController</p>
 * <p>Description: Consultar boleto de servidor</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarBoleto" })
public class ConsultarBoletoWebController extends ManterBoletoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarBoletoWebController.class);

    @Autowired
    private BoletoServidorController boletoServidorController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            TransferObject criterio = null;

            int total = boletoServidorController.countBoletoServidor(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            List<TransferObject> boletoServidor = boletoServidorController.listarBoletoServidor(criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador("../v3/consultarBoleto?acao=iniciar", "rotulo.correspondente.singular", total, size, requestParams, false, request, model);

            model.addAttribute("boletoServidor", boletoServidor);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("exibePaginacao", true);
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.boleto.lote.titulo", responsavel));

            return viewRedirect("jsp/consultarBoleto/listarBoletoServidor", request, session, model, responsavel);

        } catch (BoletoServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarPendentes" })
    public String listarPendentes(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        }
        SynchronizerToken.saveToken(request);

        try {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("SOMENTE_NAO_BAIXADO", true);

            List<TransferObject> boletoServidor = boletoServidorController.listarBoletoServidor(criterio, -1, -1, responsavel);

            model.addAttribute("boletoServidor", boletoServidor);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("exibePaginacao", false);
            model.addAttribute("linkPaginacao", null);
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.boleto.servidor.nao.visualizado.titulo", responsavel));

            return viewRedirect("jsp/consultarBoleto/listarBoletoServidor", request, session, model, responsavel);

        } catch (BoletoServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    @RequestMapping(params = { "acao=download" })
    public void download(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        try {
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
            String bosCodigo = request.getParameter("bosCodigo");
            boletoServidorController.atualizaDataDownloadBoleto(bosCodigo, responsavel);

            super.download(request, response, session, model);

        } catch (BoletoServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
    }
}
