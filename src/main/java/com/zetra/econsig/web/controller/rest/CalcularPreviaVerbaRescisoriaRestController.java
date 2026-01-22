package com.zetra.econsig.web.controller.rest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CalcularPreviaVerbaRescisoriaRestController</p>
 * <p>Description: Rest Controller para calcular a prévia de pagamentos usando a verba rescisória</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * */
@RestController
public class CalcularPreviaVerbaRescisoriaRestController {

    @Autowired
    private VerbaRescisoriaController verbaRescisoriaController;

    @RequestMapping(value = "/v3/calcularPreviaRescisao", method = RequestMethod.POST)
    public List<TransferObject> calcularPreviaRescisao(@RequestParam(value = "vrrCodigo", required = true) String vrrCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> previa = null;
        BigDecimal vrrValor = new BigDecimal(0.00);
        try {
            vrrValor = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "vrrValor"), NumberHelper.getLang(), "en"));
            previa = verbaRescisoriaController.calcularPreviaPagamentoVerbaRescisoria(vrrCodigo, vrrValor, responsavel);
        } catch (VerbaRescisoriaControllerException | ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        return previa;
    }
}
