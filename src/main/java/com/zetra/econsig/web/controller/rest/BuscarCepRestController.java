package com.zetra.econsig.web.controller.rest;

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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: BuscarCepRestController</p>
 * <p>Description: Buscar Cep Rest Controller</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: eduardo.figueiredo $
 * $Revision: 25045 $
 * $Date: 2018-07-20 09:37:29 -0300 (Sex, 20 jul 2018) $
 * */
@RestController
public class BuscarCepRestController {

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping(value = "/v3/buscaCep", method = RequestMethod.POST)
    public List<TransferObject> findCep(@RequestParam(value = "cep", required = true) String cep, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> ceps = null;
        try {
            ceps = sistemaController.findCep(cep, responsavel);
            if (ceps.isEmpty()) {
                ceps = null;
            }
        } catch (ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        return ceps;
    }
}
