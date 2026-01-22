package com.zetra.econsig.web.controller.sistema;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SegurancaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ConfigurarSistemaWebController</p>
 * <p>Description: Controlador Web para vizualização e manutenção de configurações do sistema via Web.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/configurarSistema" })
public class ConfigurarSistemaWebController extends AbstractWebController {

    @Autowired
    private SegurancaController segurancaController;

    @RequestMapping(params = { "acao=verNivelSeguranca" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String nivelAtual = segurancaController.obtemNivelSeguranca(responsavel);

            // Lista com os níveis de segurança (código e descrição)
            List<TransferObject> niveisSeguranca = segurancaController.lstNivelSeguranca(responsavel);

            // Lista com os detalhes sobre o nível de segurança
            List<Map<String, String>> detalheNiveisSegurancaParamSist = segurancaController.detalharNivelSegurancaParamSist(responsavel);

            model.addAttribute("nivelAtual", nivelAtual);
            model.addAttribute("niveisSeguranca", niveisSeguranca);
            model.addAttribute("detalheNiveisSegurancaParamSist", detalheNiveisSegurancaParamSist);
        } catch (SegurancaControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterSistema/administrarNivelSeguranca", request, session, model, responsavel);
    }

}
