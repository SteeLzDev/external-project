package com.zetra.econsig.web.controller.boasvindas;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p> * Title: VisualizarBoasVindasWebController * </p>
 * <p> * Description: Controlador Web para Boas Vindas. * </p>
 * <p> * Copyright: Copyright (c) 2002-2017 * </p>
 * <p> * Company: ZetraSoft * </p>
 * $Author: anderson.assis $
 * $Revision: 30120 $
 * $Date: 2020-08-12 14:15:47 -0300 (qua, 12 ago 2020) $
 */

@Controller
@RequestMapping(value = { "/v3/boasVindas" })
public class VisualizarBoasVindasWebController extends AbstractWebController {

    @Autowired
    private MensagemController mensagemController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws MensagemControllerException {
    	AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

    	String linkLoginPadrao = null;
    	String linkLoginServidor = null;
    	List<TransferObject> lstMensagens = null;
    	CustomTransferObject criterio = new CustomTransferObject();
    	criterio.setAttribute(Columns.MEN_PUBLICA, CodedValues.TPC_SIM);
    	lstMensagens = mensagemController.lstMensagemBoasVindas(criterio, responsavel);

    	linkLoginPadrao = "autenticarUsuario";
    	linkLoginServidor = "autenticar";

    	linkLoginPadrao += (linkLoginPadrao.indexOf('?') > 0 ? "&" : "?") + "t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
    	linkLoginServidor += (linkLoginServidor.indexOf('?') > 0 ? "&" : "?") + "t=" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");


    	model.addAttribute("linkLoginPadrao", linkLoginPadrao);
    	model.addAttribute("linkLoginServidor", linkLoginServidor);
    	model.addAttribute("lstMensagens", lstMensagens);

    	return viewRedirect("jsp/index", request, session, model, responsavel);
    }
}