package com.zetra.econsig.web.controller.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SolicitarCertificadoDigitalWebController</p>
 * <p>Description: Controlador Web para o caso de uso de autenticação de usuário com certificado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
@Controller
@RequestMapping(value = { "/v3/solicitarCertificadoDigital" })
public class SolicitarCertificadoDigitalWebController extends AutenticarUsuarioCertificadoDigitalWebController {
    @Override
    @RequestMapping(params = { "acao=iniciar" }, method = { RequestMethod.GET, RequestMethod.POST })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.iniciar(request, response, session, model);
    }
}
